package controller;

import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterops.LineRasterizer;
import model.rasterops.LineRasterizerGraphics;
import model.rasterops.PolygonRasterizer;
import view.Panel;

public class Controller2D implements Controller {

    private final Panel panel;

    public Controller2D(Panel panel) {
        this.panel = panel;

        initObjects();
        initListeners(panel);
    }

    @Override
    public void initObjects() {
        panel.clear();

        LineRasterizer liner = new LineRasterizerGraphics(panel.getRaster());

        // TODO

        // ukazka pro rasterizaci polygonu
        Point2D prvni = new Point2D(50, 50);
        Point2D druhy = new Point2D(200, 50);
        Point2D treti = new Point2D(150, 300);

        Polygon poly = new Polygon();
        poly.addItem(prvni);
        poly.addItem(druhy);
        poly.addItem(treti);

        PolygonRasterizer polygonRasterizer = new PolygonRasterizer(liner);
        polygonRasterizer.rasterize(poly);

        panel.repaint();
    }

    @Override
    public void initListeners(Panel panel) {
        // TODO
    }
}
