package controller;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.objectdata.Rectangle;
import model.objectdata.PolygonClipper;

import model.rasterops.rasterizer.LineRasterizer;
import model.rasterops.rasterizer.LineRasterizerBresenham;
import model.rasterops.rasterizer.LineRasterizerColoredBresenham;
import model.rasterops.rasterizer.LineRasterizerTrivial;
import model.rasterops.rasterizer.PolygonRasterizer;

import model.rasterops.filler.FloodFill;
import model.rasterops.filler.ScanLine;
import model.rasterops.filler.SeedFill;
import model.rasterops.clipper.SutherlandHodgmanCutting;

import view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;
    private Point2D startPoint;
    private Point2D endPoint;
    private Line draggedLine;
    private Color seedFillColor = new Color(0x337733); // barva vyplněni SeedFill
    private Color scanLineColor = new Color(0x0888aa); // barva vyplnění ScanLine
    private Color clipColor = new Color(0xffd700); // barva ořezávacího polygonu
    private Polygon polygon = new Polygon();
    private PolygonClipper clipperPolygon = null;

    private final List<Line> lines = new ArrayList<>();
    private final List<Polygon> polygons = new ArrayList<>();
    private final List<Filling> fills = new ArrayList<>();

    private final LineRasterizer lineRasterizer;
    private final LineRasterizerColoredBresenham lineRasterizerColorful;

    private int grabbedPoint = -1; // index přesunovaného vrcholu, -1 = nic
    private int grabbedPolygon = -1; // index přesunovaného polygonu, -1 = nic, -2 = aktuální polygon, -3 = clipper polygon

    private boolean shifted = false;
    private boolean colorfull = false; // neimplementováno
    private boolean rezimRectangle = false;
    private boolean usingScanline = false;
    private boolean clipperPoly = false;
    private boolean clipReverse = false;

    private static class Filling {
        final int x, y;
        final Color color;

        Filling(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    public Controller2D(Panel panel) {
        this.panel = panel;
        //this.lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        this.lineRasterizer = new LineRasterizerBresenham(panel.getRaster());
        this.lineRasterizerColorful = new LineRasterizerColoredBresenham(panel.getRaster());
        initObjects();
        initListeners(panel);
    }

    // obrácení orientace ořezávacího polygonu
    private void reverseClipperOrientation() {
        if (clipperPolygon == null) return;
        int n = clipperPolygon.size();
        if (n < 2) return;
        List<Point2D> pts = new ArrayList<>();
        for (int i = 0; i < n; i++) pts.add(clipperPolygon.getItem(i));
        clipperPolygon.clear();
        for (int i = n - 1; i >= 0; i--) {
            Point2D p = pts.get(i);
            clipperPolygon.addItem(new Point2D(p.getX(), p.getY()));
        }
        clipperPolygon.validate();
    }

    @Override
    public void initObjects() {
        panel.clear();
        panel.repaint();
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D clickPoint = new Point2D(e.getX(), e.getY());
                // reset grabu při novém kliknutí
                grabbedPoint = -1;
                grabbedPolygon = -1;
// PRAVÉ TLAČÍTKO
                if (javax.swing.SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger()) {
                    // přesunovaní vrcholu aktuálního polygonu
                    if (polygon.findNearestPoint(clickPoint) != -1 && polygon.getItem(polygon.findNearestPoint(clickPoint)).distanceTo(clickPoint) <= 10) { // hledání bodu v aktuálním polygonu s tolerancí 10 pixelů
                        grabbedPolygon = -2; // aktuální polygon
                        grabbedPoint = polygon.findNearestPoint(clickPoint);
                        return;
                    }

                    // přesunovaní vrcholu ořezávacího polygonu (pokud existuje)
                    if (clipperPolygon != null) {
                        int nearestClip = clipperPolygon.findNearestPoint(clickPoint);
                        if (nearestClip != -1 && clipperPolygon.getItem(nearestClip).distanceTo(clickPoint) <= 10) {
                            grabbedPolygon = -3; // clipping polygon
                            grabbedPoint = nearestClip;
                            return;
                        }
                    }

                    // přesunovaní vrcholu uložených polygonů
                    for (int i = 0; i < polygons.size(); i++) { // hledání bodu v ostatních polygonech
                        Polygon poly = polygons.get(i);
                        int nearest = poly.findNearestPoint(clickPoint);
                        if (nearest != -1 && poly.getItem(nearest).distanceTo(clickPoint) <= 10) { // tolerance 10 pixelů
                            grabbedPolygon = i;
                            grabbedPoint = nearest;
                            return;
                        }
                    }
                    // seedfill
                    int x = e.getX();
                    int y = e.getY();
//                    // omezení na počátek seedfillu pouze uvnitř polygonu
//                    for (Polygon poly : polygons) { // hledání polygonu obsahujícího bod kliku
//                        if (poly.size() < 3) continue;
//                        if (poly.pointInPolygon(x, y)) {
//                            fills.add(new Filling(x, y, seedFillColor)); // ukladani vykresleni
//                            vykresleni();
//                            return;
//                        }
//                    }
                    // seedfill bez omezení
                    fills.add(new Filling(x, y, seedFillColor));
                    vykresleni();
                    return;
// LEVÉ TLAČÍTKO
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    // režim obdelníku
                    if (rezimRectangle) {
                        // začátek základny (konec v mouseReleased)
                        if (startPoint == null) {
                            startPoint = new Point2D(e.getX(), e.getY());
                            endPoint = null;
                            draggedLine = null;
                            vykresleni();
                            return;
                        }
                        // výška obdelníku
                        if (startPoint != null && endPoint != null) {
                            Point2D heightPoint = new Point2D(e.getX(), e.getY());
                            Rectangle rectangle = new Rectangle(startPoint, endPoint, heightPoint);
                            // přidání hran a polygonu
                            for (int i = 0; i < rectangle.size(); i++) {
                                Point2D a = rectangle.getItem(i);
                                Point2D b = rectangle.getItem((i + 1) % rectangle.size());
                                lines.add(new Line(a, b, 0xffffff));
                            }
                            polygons.add(rectangle);
                            // reset po vytvoření obdélníku
                            startPoint = null;
                            endPoint = null;
                            draggedLine = null;
                            rezimRectangle = false; // vypnutí režimu rectangle po vytvoření
                            vykresleni();
                            return;
                        }
                        return;
                    }

                    // normální polygon
                    if (polygon.size() == 0) {
                        startPoint = new Point2D(e.getX(), e.getY());
                    } else {
                        startPoint = polygon.getLast();
                    }
// PROSTŘEDNÍ TLAČÍTKO (kolečko myši)
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    // přidání vrcholu do nejbližší hrany uložených polygonů
                    for (Polygon poly : polygons) {
                        if (poly.size() < 2) continue;
                        double minDist = Double.MAX_VALUE;
                        int insertIndex = -1;
                        Point2D projection = null;
                        for (int i = 0; i < poly.size(); i++) { // hledání nejbližší hrany polygonu
                            Point2D a = poly.getItem(i);
                            Point2D b = poly.getItem((i + 1) % poly.size());
                            Point2D proj = projectPointOnLineSegment(a, b, clickPoint);
                            double dist = proj.distanceTo(clickPoint);
                            if (dist < minDist) {
                                minDist = dist;
                                insertIndex = i + 1;
                                projection = proj;
                            }
                        }
                        // vložení nového vrcholu, pokud je blízko hrany
                        if (minDist <= 10) { // tolerance 10 pixelů od hrany
                            poly.addItemToIndex(insertIndex, projection);
                            Point2D a = poly.getItem((insertIndex - 1 + poly.size()) % poly.size());
                            Point2D b = poly.getItem((insertIndex + 1) % poly.size());
                            // odstranění staré hrany
                            lines.removeIf(line ->
                                    (line.getStart().equals(a) && line.getEnd().equals(b)) || (line.getStart().equals(b) && line.getEnd().equals(a))
                            );
                            lines.add(new Line(a, projection, 0xffffff));
                            lines.add(new Line(projection, b, 0xffffff));
                            vykresleni();
                            return;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPoint == null) return; // bez startPoint se nic neděje
                // dokončení základny obdélníku
                if (rezimRectangle) {
                    if (startPoint != null && endPoint == null) {
                        endPoint = new Point2D(e.getX(), (int) Math.round(startPoint.getY()));
                        draggedLine = new Line(startPoint, endPoint, 0xffffff); // zobrazení základny
                        vykresleni();
                        return;
                    }
                    return;
                }

                grabbedPoint = -1;
                grabbedPolygon = -1;
                int color; // fix přetejkání když se použije draggedLine v novém polygonu

                Point2D newPoint;
                if (draggedLine != null) {
                    newPoint = draggedLine.getEnd();
                    color = draggedLine.getColor();
                } else {
                    newPoint = new Point2D(e.getX(), e.getY());
                    color = 0xffffff;
                }
                if (polygon.size() == 0) {
                    if (startPoint != null) {
                        endPoint = newPoint;
                        polygon.addItem(startPoint);
                        polygon.addItem(endPoint);
                        lines.add(new Line(startPoint, endPoint, color));
                    }
                } else {
                    Point2D first = polygon.getFirst();
                    double dx = newPoint.getX() - first.getX();
                    double dy = newPoint.getY() - first.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    // klik blízko počátku ukončuje polygon
                    if (distance < 10) { // tolerance 10 pixelů
                        if (polygon.size() > 0) {
                            Point2D last = polygon.getLast();
                            lines.add(new Line(last, first, color));
                        }
                        polygons.add(polygon); // uložení polygonu
                        polygon = new Polygon(); // nový polygon
                        startPoint = null;
                        endPoint = null;
                        draggedLine = null;
                        vykresleni();
                        return;
                    } else {
                        // přidat nový vrchol a uložit úsečku mezi předchozím a novým
                        Point2D prev = polygon.getLast();
                        polygon.addItem(newPoint);
                        lines.add(new Line(prev, newPoint, color));
                    }
                }
                startPoint = null;
                endPoint = null;
                draggedLine = null;
                vykresleni();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // přesouvání vrcholu polygonu
                if (grabbedPoint != -1) {
                    if (grabbedPolygon == -2) { // aktuální polygon
                        Point2D p = polygon.getItem(grabbedPoint);
                        p.setX(e.getX());
                        p.setY(e.getY());
                    } else if (grabbedPolygon == -3) { // ořezávací polygon
                        Point2D p = clipperPolygon.getItem(grabbedPoint);
                        p.setX(e.getX());
                        p.setY(e.getY());
                    } else { // uložený polygon
                        Polygon pgn = polygons.get(grabbedPolygon);
                        Point2D p = pgn.getItem(grabbedPoint);
                        p.setX(e.getX());
                        p.setY(e.getY());
                    }
                    vykresleni();
                    return;
                }
                // při tažení v rectangle režimu nic nekreslit
                if (rezimRectangle && startPoint != null) return;
                // běžné úsečeky/polygon
                if (startPoint != null) {
                    int x2 = e.getX();
                    int y2 = e.getY();
                    // úsečka chytaná na vertikály/horizontály/diagonály
                    if (shifted) {
                        int dx = x2 - (int) Math.round(startPoint.getX());
                        int dy = y2 - (int) Math.round(startPoint.getY());
                        if (Math.abs(dx) > Math.abs(dy) * 2) {
                            y2 = (int) Math.round(startPoint.getY()); // horizontální úsečka
                        } else if (Math.abs(dx) * 2 < Math.abs(dy)) {
                            x2 = (int) Math.round(startPoint.getX()); // vertikální úsečka
                        } else {
                            int diagonala = (Math.abs(dx) > Math.abs(dy)) ? Math.abs(dx) : Math.abs(dy); // diagonální úsečka
                            x2 = (int) Math.round(startPoint.getX()) + (dx >= 0 ? diagonala : -diagonala);
                            y2 = (int) Math.round(startPoint.getY()) + (dy >= 0 ? diagonala : -diagonala);
                        }
                    }
                    endPoint = new Point2D(x2, y2);
                    draggedLine = new Line(startPoint, endPoint, 0xffffff);
                    vykresleni();
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
// SHIFT - chytání úseček na vertikály/horizontály/diagonály
                    case KeyEvent.VK_SHIFT -> {
                        shifted = true;
                    }
// X - smazat poslední bod aktuálního polygonu
                    case KeyEvent.VK_X -> {
                        if (polygon.size() > 0) { // maže pouze bod
                            int lastIndex = polygon.size() - 1;
                            if (polygon.size() > 1) { // maže úsečky s tímto bodem spojenou
                                Point2D last = polygon.getItem(lastIndex);
                                Point2D prev = polygon.getItem(lastIndex - 1);
                                lines.removeIf(line -> (line.getStart().equals(prev) && line.getEnd().equals(last)) || (line.getStart().equals(last) && line.getEnd().equals(prev))
                                );
                            }
                            polygon.removeItem(lastIndex);
                            vykresleni();
                        }
                    }
// C - vymazat vše
                    case KeyEvent.VK_C -> {
                        // reset všech objektů
                        polygon.clear();
                        polygons.clear();
                        lines.clear();
                        fills.clear();
                        // reset režimů
                        colorfull = false;
                        rezimRectangle = false;
                        usingScanline = false;
                        clipperPoly = false;
                        clipReverse = false;
                        // reset pomocných proměnných
                        draggedLine = null;
                        startPoint = null;
                        endPoint = null;
                        clipperPolygon = null;
                        // vykreslení prázdného plátna
                        panel.getRaster().clear();
                        panel.repaint();
                    }
// V - zrušit ntaženou úsečku
                    case KeyEvent.VK_V -> {
                        draggedLine = null;
                        startPoint = null;
                        endPoint = null;
                        vykresleni();
                    }
// B - přepnout barevný režim (Neimplementováno)
                    case KeyEvent.VK_B -> {
                        // notTODO po stisku B se vykreslí úsečka s lineárním přechodem dvou barev
                        colorfull = !colorfull;
                    }
// D - přepínání režimu obdélníku
                    case KeyEvent.VK_D -> {
                        rezimRectangle = !rezimRectangle;
                        // při přepnutí režimu zrušíme nedokončený obdélník
                        startPoint = null;
                        endPoint = null;
                        draggedLine = null;
                        vykresleni();
                    }
// F - přepínání ScanLine a SeedFill
                    case KeyEvent.VK_F -> {
                        usingScanline = !usingScanline;
                        vykresleni();
                    }
// G - vytvoření ořezávacího polygonu
                    case KeyEvent.VK_G -> {
                        Point mouse = panel.getMousePosition();
                        double cx, cy;
                        if (mouse != null) {
                            cx = mouse.getX();
                            cy = mouse.getY();
                        } else {
                            cx = panel.getWidth() / 2.0;
                            cy = panel.getHeight() / 2.0;
                        }
                        double radius = Math.min(panel.getWidth(), panel.getHeight()) / 3.0; // pětiúhelník (2/3) plátna
                        clipperPolygon = PolygonClipper.regularPentagon(cx, cy, radius);
                        clipperPolygon.validate();
                        vykresleni();
                    }
// H - obrácení orientace ořezávacího polygonu
                    case KeyEvent.VK_H -> {
                        clipReverse = !clipReverse;
                        vykresleni();
                    }
// ENTER - dokončení aktuálního polygonu
                    case KeyEvent.VK_ENTER -> {
                        if (polygon.size() > 1) {
                            if (polygon.size() > 2) {
                                Point2D last = polygon.getLast();
                                Point2D first = polygon.getFirst();
                                lines.add(new Line(last, first, 0xffffff));
                            }
                            polygons.add(polygon); // uložení polygonu
                            polygon = new Polygon(); // nový polygon
                            startPoint = null;
                            endPoint = null;
                            draggedLine = null;
                            vykresleni();
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // SHIFT je aktivní pouze při držení
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shifted = false;
                }
            }
        });
        // fokus panelu na klavesnici
        panel.setFocusable(true);
        panel.requestFocusInWindow();
    }

    // projekce bodu na úsečku (přidávání vrcholů na hrany)
    private Point2D projectPointOnLineSegment(Point2D a, Point2D b, Point2D clickPoint) {
        double ax = a.getX(), ay = a.getY();
        double bx = b.getX(), by = b.getY();
        double clickPointX = clickPoint.getX(), clickPointY = clickPoint.getY();
        double dx = bx - ax;
        double dy = by - ay;
        if (dx == 0 && dy == 0) {
            return new Point2D(ax, ay); // hrana je bod
        }
        double t = ((clickPointX - ax) * dx + (clickPointY - ay) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t)); // omezení na úsečku
        return new Point2D(ax + t * dx, ay + t * dy);
    }

    // vykreslování objektů, fillů a pružné úsečky
    private void vykresleni() {
        panel.getRaster().clear();
        for (Line l : lines) { // vykreslení všech úseček
            lineRasterizer.rasterize(l);
        }
        // vykreslení všech uložených polygonů
        PolygonRasterizer pr = new PolygonRasterizer(lineRasterizer);
        // vykreslení bodů polygonu
        for (Polygon poly : polygons) {
            pr.rasterize(poly, true);
        }
        // aktuální polygon (neuzavřený)
        pr.rasterize(polygon, false);
        // ořezávací polygon
        if (clipperPolygon != null && clipperPolygon.size() >= 2) {
            for (int i = 0; i < clipperPolygon.size(); i++) {
                Point2D a = clipperPolygon.getItem(i);
                Point2D b = clipperPolygon.getItem((i + 1) % clipperPolygon.size());
                lineRasterizer.rasterize(new Line(a, b, clipColor.getRGB()));
            }
        }

        // vyplnění polygonů buď ScanLine nebo SeedFill
        if (usingScanline) { // ScanLine
            ScanLine scanLineFilling = new ScanLine(panel.getRaster());
            if (clipperPolygon != null && clipperPolygon.isValid()) {
                // ořezávání polygonů
                SutherlandHodgmanCutting cutter = new SutherlandHodgmanCutting();
                if (!clipReverse) {
                    // průnik
                    List<Polygon> clipped = new ArrayList<>();
                    for (Polygon p : polygons) {
                        Polygon c = cutter.cut(p, clipperPolygon);
                        if (c != null && c.size() >= 3) clipped.add(c);
                    }
                    scanLineFilling.fillAll(clipped, scanLineColor);
                } else {
                    // rozdíl
                    scanLineFilling.fillAll(polygons, scanLineColor);
                    List<Polygon> clipped = new ArrayList<>();
                    for (Polygon p : polygons) {
                        Polygon c = cutter.cut(p, clipperPolygon);
                        if (c != null && c.size() >= 3) clipped.add(c);
                    }
                    // vyplnění barvou pozadí
                    scanLineFilling.fillAll(clipped, panel.getBackground());
                }

            } else {
                // bez ořezávacího polygonu
                scanLineFilling.fillAll(polygons, scanLineColor);
            }
        } else { // SeedFill
            if (!fills.isEmpty()) {
                SeedFill filler = new SeedFill(panel.getRaster());
                for (Filling f : fills) {
                    // TODO přepínání mezi 4mi a 8mi sousedy
                    filler.seedFill4(f.x, f.y, f.color);
                    //filler.seedFill8(f.x, f.y, f.color);
                }
            }
        }

        if (draggedLine != null) { // tvoření normální pružné úsečky / zobrazení základny obdelníku
            if (polygon.size() == 0) {
                (colorfull ? lineRasterizerColorful : lineRasterizer).rasterize(draggedLine);
            } else {
                Point2D firstPolyToPruz = polygon.size() > 0 ? polygon.getFirst() : startPoint;
                Point2D lastPolyToPruz = polygon.size() > 0 ? polygon.getLast() : startPoint;
                // úsečka od prvního vrcholu polygonu k pružné úsečce
                (colorfull ? lineRasterizerColorful : lineRasterizer).rasterize(
                        firstPolyToPruz.getX(), firstPolyToPruz.getY(),
                        draggedLine.getEnd().getX(), draggedLine.getEnd().getY(),
                        Color.WHITE
                );
                // úsečka od posledního vrcholu polygonu k pružné úsečce
                (colorfull ? lineRasterizerColorful : lineRasterizer).rasterize(
                        lastPolyToPruz.getX(), lastPolyToPruz.getY(),
                        draggedLine.getEnd().getX(), draggedLine.getEnd().getY(),
                        Color.WHITE
                );
            }
        }
        panel.repaint();
    }
}
