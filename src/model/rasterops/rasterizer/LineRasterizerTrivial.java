package model.rasterops.rasterizer;

import model.rasterdata.Raster;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        if (dx == 0) { // vertikální úsečka
            if (y1 > y2) {
                int tmp = y1;
                y1 = y2;
                y2 = tmp;
            }
            for (int y = y1; y <= y2; y++) {
                raster.setPixel(x1, y, color.getRGB());
            }
            return;
        }
        if (dy == 0) { // horizontální úsečka
            if (x1 > x2) {
                int tmp = x1;
                x1 = x2;
                x2 = tmp;
            }
            for (int x = x1; x <= x2; x++) {
                raster.setPixel(x, y1, color.getRGB());
            }
            return;
        }

        if (Math.abs(dx) > Math.abs(dy)) { // vykreslování podle X
            if (x1 > x2) {
                int tmpX = x1, tmpY = y1;
                x1 = x2;
                x2 = tmpX;
                y1 = y2;
                y2 = tmpY;
            }
            double k = (double) dy / dx;
            double q = y1 - k * x1;
            for (int x = x1; x <= x2; x++) {
                int y = (int) Math.round(k * x + q);
                raster.setPixel(x, y, color.getRGB());
            }
        } else { // vykreslování podle Y
            if (y1 > y2) {
                int tmpX = x1, tmpY = y1;
                x1 = x2;
                x2 = tmpX;
                y1 = y2;
                y2 = tmpY;
            }
            double k = (double) dx / dy; // x = k*y + q
            double q = x1 - k * y1;
            for (int y = y1; y <= y2; y++) {
                int x = (int) Math.round(k * y + q);
                raster.setPixel(x, y, color.getRGB());
            }
        }
    }
}
