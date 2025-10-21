package model.rasterops.rasterizer;

import model.rasterdata.Raster;
import java.awt.*;

public class LineRasterizerColoredBresenham extends LineRasterizer {

    private Color colorStart = Color.RED;
    private Color colorEnd = Color.BLUE;

    public LineRasterizerColoredBresenham(Raster raster) {
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
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int lineLenght = Math.max(dx, dy); // pro interpolaci barvy
        int steps = 0;
        while (true) {
            double t = (lineLenght == 0) ? 0.0 : (double) steps / lineLenght;
            int r = (int) (colorStart.getRed() * (1 - t) + colorEnd.getRed() * t);
            int g = (int) (colorStart.getGreen() * (1 - t) + colorEnd.getGreen() * t);
            int b = (int) (colorStart.getBlue() * (1 - t) + colorEnd.getBlue() * t);
            r = Math.max(0, Math.min(255, r)); // proti přetečení
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));
            raster.setPixel(x1, y1, new Color(r, g, b).getRGB());

            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
            steps++;
        }
    }
}
