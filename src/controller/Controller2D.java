package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import model.objectdata.Point2D;
import model.objectdata.Line;
import model.objectdata.Polygon;
import model.rasterdata.Raster;
import model.rasterdata.RasterBI;
import model.rasterops.LineRasterizer;
import model.rasterops.LineRasterizerGraphics;
import model.rasterops.LineRasterizerTrivial;
import model.rasterops.PolygonRasterizer;
import view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;
    private Point2D startPoint;
    private Point2D endPoint;
    private Line draggedLine;

    private final List<Line> lines = new ArrayList<>();
    private final LineRasterizer LineRasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        LineRasterizer = new LineRasterizerTrivial(panel.getRaster());
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
                    Line line = new Line(startPoint, endPoint, 0xFFFFFF); // TODO impl. color.getRGB()
                    lines.add(line); // uloží úsečku pro vykreslení
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
                    endPoint = new Point2D(e.getX(), e.getY());
                    draggedLine = new Line(startPoint, endPoint, 0xFFFFFF);// TODO impl. color.getRGB()
                    vykresleni();
                }
            }
        });

        // TODO KeyListener (klávesa C maže panel/plátno)
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    lines.clear();  // mažání paměti úseček
                    draggedLine = null; // zruší aktuální tažení úsečky
                    panel.getRaster().clear();
                    panel.repaint();        // překreslení panelu/plátna
                }
            }
        });
        panel.setFocusable(true); //fokus panelu na klavesnici
        panel.requestFocusInWindow();
    }

    private void vykresleni() { // vykresluje uložené úsečky
        panel.getRaster().clear();
        for (Line line : lines) {
            LineRasterizer.rasterize(line);
        }
        if (draggedLine != null) {
            LineRasterizer.rasterize(draggedLine);
        }
        panel.repaint();
    }
}
