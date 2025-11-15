package model.rasterops.filler;

import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.objectdata.Line;
import model.rasterdata.Raster;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLine {
    private final Raster raster;

    public ScanLine(Raster raster) {
        this.raster = raster;
    }

    // volaní vyplnění scanline pro všechny polygony
    public void fillAll(List<Polygon> polygons, Color color) {
        if (polygons == null) return;
        for (Polygon p : polygons) {
            fillPolygon(p, color);
        }
    }

    // vyplnění polygonu metodou scanline
    private void fillPolygon(Polygon poly, Color color) {
        if (poly == null || poly.size() < 3) return;

        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < poly.size(); i++) {
            double y = poly.getItem(i).getY();
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        int yStart = (int) Math.floor(minY);
        int yEnd = (int) Math.ceil(maxY);

        for (int y = yStart; y < yEnd; y++) {
            double scanY = y + 0.5; // testovací horizontála uprostřed pixelu
            List<Double> intersections = new ArrayList<>();

            for (int i = 0; i < poly.size(); i++) {
                Point2D a = poly.getItem(i);
                Point2D b = poly.getItem((i + 1) % poly.size());
                double x1 = a.getX(), y1 = a.getY();
                double x2 = b.getX(), y2 = b.getY();

                // ignorovat horizontální hrany
                if (Math.abs(y1 - y2) < 1e-9) continue;

                double yMin = Math.min(y1, y2);
                double yMax = Math.max(y1, y2);

                // pravidlo [yMin, yMax) - zapobíhá dvojitému počítání vrcholů
                if (scanY >= yMin && scanY < yMax) {
                    double t = (scanY - y1) / (y2 - y1);
                    double ix = x1 + t * (x2 - x1);
                    intersections.add(ix);
                }
            }

            if (intersections.isEmpty()) continue;
            Collections.sort(intersections);

            // spojování dvojic průsečíků
            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                int x1 = (int) Math.ceil(intersections.get(i) - 1e-9);
                int x2 = (int) Math.floor(intersections.get(i + 1) + 1e-9);
                if (x2 < x1) continue;
                for (int x = x1; x <= x2; x++) {
                    raster.setPixel(x, y, color.getRGB());
                }
            }
        }
    }
}
