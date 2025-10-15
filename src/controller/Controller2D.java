package controller;

//  import java.awt.event.MouseAdapter;
//  import java.awt.event.MouseEvent;
//  import java.awt.event.MouseMotionAdapter;
//  import java.awt.event.KeyAdapter;
//  import java.awt.event.KeyEvent;

import java.awt.*;
import java.awt.event.*;

//  import model.objectdata.Point2D;
//  import model.objectdata.Line;
//  import model.objectdata.Polygon;
import model.objectdata.*;
//  import model.rasterdata.Raster;
//  import model.rasterdata.RasterBI;
import model.objectdata.Polygon;
//  import model.rasterops.LineRasterizer;
//  import model.rasterops.LineRasterizerGraphics;
//  import model.rasterops.LineRasterizerTrivial;
//  import model.rasterops.PolygonRasterizer;
import model.rasterops.*;
import view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;
    private Point2D startPoint;
    private Point2D endPoint;
    private Line draggedLine;

    //    private final List<Line> lines = new ArrayList<>();
//    private final LineRasterizer LineRasterizer;
    private final Polygon polygon = new Polygon();
    private final LineRasterizer lineRasterizer;

    private boolean shifted = false;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
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
        // TODO MouseListener, MouseRelease
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = new Point2D(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPoint != null) {
                    endPoint = new Point2D(e.getX(), e.getY());
//                    Line line = new Line(startPoint, endPoint, draggedLine.getColor());
//                    lines.add(line); // uloží úsečku pro vykreslení
                    polygon.addItem(startPoint);
                    polygon.addItem(endPoint);

                    startPoint = null;
                    endPoint = null;
                    draggedLine = null;
                    vykresleni();
                }
            }
        });

        // TODO MouseMotion
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

        // TODO KeyListener (klávesa C maže panel/plátno)
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C -> {
//                        lines.clear();
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
                        // TODO barevná pružná přímka
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
        panel.setFocusable(true);   //fokus panelu na klavesnici
        panel.requestFocusInWindow();
    }

    private void vykresleni() {
        panel.getRaster().clear();
//        for (Line line : lines) {
//            LineRasterizer.rasterize(line);
//        }
        PolygonRasterizer pr = new PolygonRasterizer(lineRasterizer);
        pr.rasterize(polygon); // vykreslení bodů polynomu

        if (draggedLine != null) {
            lineRasterizer.rasterize(draggedLine); // vykreslení pružné úsečky
            if (polygon.size() > 0) {
                Point2D firstPolyToPruz = polygon.getItem(0);
                lineRasterizer.rasterize(
                        firstPolyToPruz.getX(), firstPolyToPruz.getY(), // počátek polygonu
                        draggedLine.getEnd().getX(), draggedLine.getEnd().getY(), // konec pružné úsečky
                        Color.WHITE
                );
                Point2D lastPolyToPruz = polygon.getItem(polygon.size() - 1);
                lineRasterizer.rasterize(
                        lastPolyToPruz.getX(), lastPolyToPruz.getY(), // poslední bod polygonu
                        draggedLine.getStart().getX(), draggedLine.getStart().getY(), // počátek pružné úsečky
                        Color.WHITE
                );
            }
        }
        panel.repaint();
    }
}
