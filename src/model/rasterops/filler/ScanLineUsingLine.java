package model.rasterops.filler;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterdata.Raster;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLineUsingLine {
    private final Raster raster;

    public ScanLineUsingLine(Raster raster) {
        this.raster = raster;
    }

    public void fillAll(List<Polygon> polygons, Color color) {
        if (polygons == null) return;
        for (Polygon p : polygons) {
            fillPolygon(p, color);
        }
    }

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
        int rgb = color.getRGB();

        for (int y = yStart; y < yEnd; y++) {
            double scanY = y + 0.5; // testovací horizontála uprostřed pixelu
            List<Double> intersections = new ArrayList<>();

            // sběr průsečíků s každou hranou (využívá Line.*)
            for (int i = 0, n = poly.size(); i < n; i++) {
                Point2D a = poly.getItem(i);
                Point2D b = poly.getItem((i + 1) % n);

                Line edge = new Line(a, b);
                if (edge.isHorizontal()) continue; // přeskočit horizontální úsečky

                double ay = a.getY();
                double by = b.getY();

                double yMin = Math.min(ay, by);
                double yMax = Math.max(ay, by);

                // pravidlo [yMin, yMax) - zabrání dvojímu počítání vrcholů
                if (scanY >= yMin && scanY < yMax) {
                    try {
                        // volání s double scanY místo int zaokrouhlování
                        double ix = edge.getIntersection(scanY);
                        intersections.add(ix);
                    } catch (IllegalArgumentException ignored) {
                        // úsečka je horizontální nebo téměř horizontální — přeskočeno výše
                    }
                }
            }

            if (intersections.isEmpty()) continue;
            Collections.sort(intersections);

            // vyplnit intervaly mezi páry průsečíků (even-odd)
            for (int k = 0; k + 1 < intersections.size(); k += 2) {
                double left = intersections.get(k);
                double right = intersections.get(k + 1);
                int xStart = (int) Math.ceil(left - 1e-9);
                int xEnd = (int) Math.floor(right + 1e-9);
                if (xEnd < xStart) continue;
                for (int x = xStart; x <= xEnd; x++) {
                    try {
                        raster.setPixel(x, y, rgb);
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        }
    }
}
