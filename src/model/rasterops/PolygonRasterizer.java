package model.rasterops;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;

import java.awt.*;
import java.util.List;

public class PolygonRasterizer {

    private LineRasterizer liner;

    public PolygonRasterizer(LineRasterizer liner) {
        this.liner = liner;
    }

    public void rasterize(List<Line> listOfLines) {
        for (Line line : listOfLines) {
            liner.rasterize(line);
        }
    }

    public void rasterize(Polygon polygon) {
        // TODO podminka pro velikost polygonu
        int size = polygon.size();
        if (size < 2) {
            return; // musí mít alespoň dva body pro vykkreslení úsečky
        }
        for (int i = 0; i < polygon.size(); i++) {
            Point2D p1 = polygon.getItem(i);
            // TODO pridat podmienku
            Point2D p2 = polygon.getItem((i + 1) % polygon.size());

            liner.rasterize(
                    p1.getX(),
                    p1.getY(),
                    p2.getX(),
                    p2.getY(),
                    Color.WHITE
            );
        }
    }
}
