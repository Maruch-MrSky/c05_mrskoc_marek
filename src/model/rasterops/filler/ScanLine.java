package model.rasterops.filler;

import model.objectdata.Line;
import model.objectdata.Point2D;
import model.objectdata.Polygon;
import model.rasterdata.Raster;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLine {
    private final Raster raster;
    private final int backgroundRGB;
    private static final double EPS = 1e-9;

    public ScanLine(Raster raster) {
        this.raster = raster;
        this.backgroundRGB = raster.getBackgroundRGB();
    }

    public void fillAll(List<Polygon> polygons, Color color) {
        if (polygons == null) return;
        for (Polygon p : polygons) {
            fillPolygon(p, color);
        }
    }

    private void fillPolygon(Polygon poly, Color color) {
        if (poly == null || poly.size() < 3) return;
        // nalezení rozsahu Y
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < poly.size(); i++) {
            double y = poly.getItem(i).getY();
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }
        // sken horizontálních řádků
        int yStart = (int) Math.floor(minY);
        int yEnd = (int) Math.ceil(maxY);
        int rgb = color.getRGB();

        // pro každý řádek zjistit průsečíky s polygonem
        for (int y = yStart; y < yEnd; y++) {
            double scanY = y + 0.5; // horizontála uprostřed pixelu
            List<Double> intersections = new ArrayList<>();

            // sběr průsečíků s každou hranou
            for (int i = 0, n = poly.size(); i < n; i++) {
                Point2D a = poly.getItem(i);
                Point2D b = poly.getItem((i + 1) % n);

                Line edge = new Line(a, b);
                // přeskočit horizontální hrany
                if (edge.isHorizontal()) continue;

                double ay = a.getY();
                double by = b.getY();
                double yMin = Math.min(ay, by);
                double yMax = Math.max(ay, by);

                // pravidlo [yMin, yMax) aby se nevyskytovalo dvojí počítání vrcholů
                if (scanY >= yMin && scanY < yMax) {
                    double ix = edge.getIntersection(scanY);
                    intersections.add(ix);
                }
            }
            if (intersections.isEmpty()) continue; // žádné průsečíky
            // seřadit průsečíky podle x
            Collections.sort(intersections);

            // vyplnit intervaly mezi páry průsečíků (even-odd)
            for (int k = 0; k + 1 < intersections.size(); k += 2) {
                double left = intersections.get(k);
                double right = intersections.get(k + 1);
                // přidání EPS pro správné zaokrouhlování hran
                int xStart = (int) Math.ceil(left + EPS);
                int xEnd = (int) Math.floor(right - EPS);
                if (xEnd < xStart) continue;
                // vyplnit pixely mezi xStart a xEnd
                for (int x = xStart; x <= xEnd; x++) {
                    try {
                        int existing = raster.getPixel(x, y);
                        if (existing != backgroundRGB) continue; // nepřepisovat již vykreslené hrany / pixely
                        raster.setPixel(x, y, rgb);
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        }
    }
}
