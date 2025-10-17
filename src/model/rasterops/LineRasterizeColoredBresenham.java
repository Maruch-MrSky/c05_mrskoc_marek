package model.rasterops;

import model.rasterdata.Raster;

import java.awt.*;

public class LineRasterizeColoredBresenham extends LineRasterizer {

    private Color colorStart = Color.RED;
    private Color colorEnd = Color.BLUE;

    public LineRasterizeColoredBresenham(Raster raster) {
        super(raster);
    }

    public void setColors(Color start, Color end) {
        this.colorStart = start;
        this.colorEnd = end;
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int smerX = x1 < x2 ? 1 : -1;
        int smerY = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int lineLength = Math.max(dx, dy);
        int steps = 0;

        while (true) {
            double t = (lineLength == 0) ? 0.0 : (double) steps / lineLength; // interpolace barev
            int r = (int) (colorStart.getRed() * (1 - t) + colorEnd.getRed() * t);
            int g = (int) (colorStart.getGreen() * (1 - t) + colorEnd.getGreen() * t);
            int b = (int) (colorStart.getBlue() * (1 - t) + colorEnd.getBlue() * t);
            int rgb = new Color(r, g, b).getRGB();
            raster.setPixel(x1, y1, rgb);

            if (x1 == x2 && y1 == y2) break;
            if (2 * err > -dy) {
                err -= dy;
                x1 += smerX;
            }
            if (2 * err < dx) {
                err += dx;
                y1 += smerY;
            }
            steps++;
        }
    }
}
