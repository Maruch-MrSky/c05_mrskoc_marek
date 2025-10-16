package controller;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import model.objectdata.*;
import model.objectdata.Polygon;
import model.rasterops.*;
import view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;
    private Point2D startPoint;
    private Point2D endPoint;
    private Line draggedLine;


    //private final Polygon polygon = new Polygon();
    private final java.util.List<Polygon> polygons = new ArrayList<>();
    private Polygon polygon = new Polygon();
    private final LineRasterizer lineRasterizer;

    private boolean shifted = false;

    public Controller2D(Panel panel) {
        this.panel = panel;
        //this.lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        this.lineRasterizer = new LineRasterizerBresenham(panel.getRaster());
        initObjects();
        initListeners(panel);
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
                if (polygon.size() == 0) {
                    startPoint = new Point2D(e.getX(), e.getY());
                } else {
                    startPoint = polygon.getFirst();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPoint == null) return;
                Point2D newPoint;
                if (draggedLine != null) {
                    newPoint = draggedLine.getEnd();
                } else {
                    newPoint = new Point2D(e.getX(), e.getY());
                }
                if (polygon.size() == 0) {
                    if (startPoint != null) {
                        endPoint = newPoint;
                        polygon.addItem(startPoint);
                        polygon.addItem(endPoint);
                    }
                } else {
                    Point2D first = polygon.getFirst();
                    double dx = newPoint.getX() - first.getX();
                    double dy = newPoint.getY() - first.getY();
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    // klik blízko počátku ukončuje polygon
                    if (dist < 7) { // tolerance 7 pixelů
                        polygons.add(polygon); // uložení polygonu
                        polygon = new Polygon(); // nový polygon
                        startPoint = null;
                        endPoint = null;
                        draggedLine = null;
                        vykresleni();
                        return;
                    } else {
                        polygon.addItem(newPoint);
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
                if (startPoint != null) {
                    int x2 = e.getX();
                    int y2 = e.getY();

                    if (shifted) {
                        int dx = x2 - startPoint.x; // maybe .getX()?
                        int dy = y2 - startPoint.y; // maybe .getY()?
                        if (Math.abs(dx) > Math.abs(dy) * 2) {
                            y2 = startPoint.y; // horizontální úsečka
                        } else if (Math.abs(dx) * 2 < Math.abs(dy)) {
                            x2 = startPoint.x; // vertikální úsečka
                        } else {
                            int diagonala = (Math.abs(dy) < Math.abs(dx)) ? Math.abs(dx) : Math.abs(dy);
                            x2 = startPoint.x + (dx >= 0 ? diagonala : -diagonala);
                            y2 = startPoint.y + (dy >= 0 ? diagonala : -diagonala);
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
                    case KeyEvent.VK_C -> {
                        polygon.clear();
                        draggedLine = null; // zruší aktuální tažení úsečky
                        startPoint = null; // <─┤
                        endPoint = null; // <───┘
                        panel.getRaster().clear();
                        panel.repaint(); // překreslení panelu/plátna
                    }
                    case KeyEvent.VK_V -> {
                        draggedLine = null; // zruší aktuální tažení úsečky
                        startPoint = null; // <─┤
                        endPoint = null; // <───┘
                        vykresleni();
                    }
                    case KeyEvent.VK_B -> {
                        // TODO po stisku B se vykreslí úsečka s lineárním přechodem dvou barev
//
                    }
                    case KeyEvent.VK_SHIFT -> {
                        shifted = true;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shifted = false;
                }
            }
        });
        panel.setFocusable(true);   // fokus panelu na klavesnici
        panel.requestFocusInWindow();
    }

    private void vykresleni() {
        panel.getRaster().clear();
        PolygonRasterizer pr = new PolygonRasterizer(lineRasterizer);
        for (Polygon poly : polygons) { // vykreslení všech polygonů
            pr.rasterize(poly, true); // vykreslení bodů polygonu
        }
        pr.rasterize(polygon, false);

        if (draggedLine != null) {
            if (polygon.size() == 0) { // tvoření normální pružné úsečky
                lineRasterizer.rasterize(draggedLine);
            } else {
                Point2D firstPolyToPruz = polygon.size() > 0 ? polygon.getFirst() : startPoint;

                lineRasterizer.rasterize(
                        firstPolyToPruz.getX(), firstPolyToPruz.getY(), // počátek polygonu
                        draggedLine.getEnd().getX(), draggedLine.getEnd().getY(), // pohyblivý bod
                        Color.WHITE
                );
                Point2D lastPolyToPruz = polygon.size() > 0 ? polygon.getLast() : startPoint;
                lineRasterizer.rasterize(
                        lastPolyToPruz.getX(), lastPolyToPruz.getY(), // poslední bod polygonu
                        draggedLine.getEnd().getX(), draggedLine.getEnd().getY(), // pohyblivý bod
                        Color.WHITE
                );
            }
        }
        panel.repaint();
    }
}
