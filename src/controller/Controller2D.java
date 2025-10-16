package controller;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterops.LineRasterizer;
import model.rasterops.LineRasterizerBresenham;
//import model.rasterops.LineRasterizerTrivial;
import model.rasterops.PolygonRasterizer;

import view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;
    private Point2D startPoint;
    private Point2D endPoint;
    private Line draggedLine;

    private final List<Line> lines = new ArrayList<>();
    private final List<Polygon> polygons = new ArrayList<>();
    private Polygon polygon = new Polygon();
    private final LineRasterizer lineRasterizer;
    private int grabbed = -1; // index přesunovaného vrcholu polygonu, -1 = nic

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
                if (e.getButton() == MouseEvent.BUTTON3 && polygon.size() > 0) {
                    Point2D clickPoint = new Point2D(e.getX(), e.getY());
                    Point2D nearest = polygon.getItem(polygon.findNearestPoint(clickPoint));
                    if (nearest != null && nearest.distanceTo(clickPoint) <= 10) { // blízkost 10 px
                        grabbed = polygon.findNearestPoint(clickPoint);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    if (polygon.size() == 0) {
                        startPoint = new Point2D(e.getX(), e.getY());
                    } else {
                        startPoint = polygon.getLast();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                grabbed = -1; // uvolnění přesunovaného bodu polygonu
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
                        lines.add(new Line(startPoint, endPoint, draggedLine.getColor()));
                    }
                } else {
                    Point2D first = polygon.getFirst();
                    double dx = newPoint.getX() - first.getX();
                    double dy = newPoint.getY() - first.getY();
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    // klik blízko počátku ukončuje polygon
                    if (dist < 7) { // tolerance 7 pixelů
                        if (polygon.size() > 0) {
                            Point2D last = polygon.getLast();
                            lines.add(new Line(last, first, 0xffffff));
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
                        lines.add(new Line(prev, newPoint, 0xffffff));
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
                if (grabbed != -1) {
                    polygon.getItem(grabbed).setX(e.getX());
                    polygon.getItem(grabbed).setY(e.getY());
                    vykresleni();
                } else if (startPoint != null) {
                    int x2 = e.getX();
                    int y2 = e.getY();

                    if (shifted) {
                        int dx = x2 - (int) Math.round(startPoint.getX());
                        int dy = y2 - (int) Math.round(startPoint.getY());
                        if (Math.abs(dx) > Math.abs(dy) * 2) {
                            y2 = (int) Math.round(startPoint.getY()); // horizontální úsečka
                        } else if (Math.abs(dx) * 2 < Math.abs(dy)) {
                            x2 = (int) Math.round(startPoint.getX()); // vertikální úsečka
                        } else {
                            int diagonala = (Math.abs(dy) < Math.abs(dx)) ? Math.abs(dx) : Math.abs(dy); // diagonální úsečka
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
                    case KeyEvent.VK_C -> {
                        // vymazat vše: body polygonu i uložené úsečky
                        polygon.clear();
                        polygons.clear();
                        lines.clear();
                        draggedLine = null;
                        startPoint = null;
                        endPoint = null;
                        panel.getRaster().clear();
                        panel.repaint();
                    }
                    case KeyEvent.VK_V -> {
                        draggedLine = null;
                        startPoint = null;
                        endPoint = null;
                        vykresleni();
                    }
                    case KeyEvent.VK_B -> {
                        // TODO po stisku B se vykreslí úsečka s lineárním přechodem dvou barev
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

        // nejprve vykreslit všechny uložené úsečky
        for (Line l : lines) {
            lineRasterizer.rasterize(l);
        }

        PolygonRasterizer pr = new PolygonRasterizer(lineRasterizer);
        for (Polygon poly : polygons) { // vykreslení všech uložených polygonů (body)
            pr.rasterize(poly, true); // vykreslení bodů polygonu
        }
        pr.rasterize(polygon, false); // aktuální polygon (body, neuzavřený)

        if (draggedLine != null) {
            if (polygon.size() == 0) { // tvoření normální pružné úsečky
                lineRasterizer.rasterize(draggedLine);
            } else {
                Point2D firstPolyToPruz = polygon.size() > 0 ? polygon.getFirst() : startPoint;

                lineRasterizer.rasterize(
                        firstPolyToPruz.getX(), firstPolyToPruz.getY(),
                        draggedLine.getEnd().getX(), draggedLine.getEnd().getY(),
                        Color.WHITE
                );
                Point2D lastPolyToPruz = polygon.size() > 0 ? polygon.getLast() : startPoint;
                lineRasterizer.rasterize(
                        lastPolyToPruz.getX(), lastPolyToPruz.getY(),
                        draggedLine.getEnd().getX(), draggedLine.getEnd().getY(),
                        Color.WHITE
                );
            }
        }
        panel.repaint();
    }
}