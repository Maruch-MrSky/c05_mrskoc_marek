package model.rasterops.filler;

import model.objectdata.Line;
import model.objectdata.Polygon;
import model.rasterops.rasterizer.LineRasterizer;
import model.rasterops.rasterizer.PolygonRasterizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLine {

    private Polygon poly;
    private LineRasterizer liner;
    private PolygonRasterizer polyLiner;

    public ScanLine(Polygon poly, LineRasterizer liner, PolygonRasterizer polyLiner) {
        this.poly = poly;
        this.liner = liner;
        this.polyLiner = polyLiner;
    }

    public void fill() {
        List<Line> edges = new ArrayList<>();
        int n = poly.size();
        for (int i = 0; i < n; i++) {
            var p1 = poly.getItem(i);
            var p2 = poly.getItem((i + 1) % n);
            Line edge = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            if (!edge.isHorizontal()) {
                edge.orientate(); // zajistí správnou orientaci (minY -> maxY)
                edges.add(edge);
            }
        }

        if (edges.isEmpty()) {
            // nic k vyplnění
            //polyLiner.rasterize(poly);
            return;
        }

        // 1b. nalezení yMin a yMax
        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int y = poly.getItem(i).getY();
            if (y < yMin) yMin = y;
            if (y > yMax) yMax = y;
        }

        // 2. scanline: pro každý y spočítat průsečíky, seřadit a vykreslit úsečky mezi páry
        for (int y = yMin; y <= yMax; y++) {
            List<Double> intersections = new ArrayList<>();
            for (Line e : edges) {
                // standardní test: zahrnout hrany, kde y je v intervalu [yMin, yMax)
                if (e.getYmin() <= y && y < e.getYmax()) {
                    double ix = e.getIntersection(y);
                    intersections.add(ix);
                }
            }

            if (intersections.isEmpty()) continue;

            Collections.sort(intersections);

            // vykreslit mezi každým lichým a sudým průsečíkem
            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                int x1 = (int) Math.ceil(intersections.get(i));
                int x2 = (int) Math.floor(intersections.get(i + 1));
                if (x1 <= x2) {
                    Line span = new Line(x1, y, x2, y);
                    liner.rasterize(span);
                }
            }
        }
    }
}
