package model.rasterops;

import model.rasterdata.Raster;

public class LineRasterizerBresenham extends LineRasterizer {

    public LineRasterizerBresenham(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        //skrácený IF pro směr posunu
        int smerX = x1 < x2 ? 1 : -1;
        int smerY = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            raster.setPixel(x1, y1, color.getRGB()); // vykreslení aktuálního bodu
            if (x1 == x2 && y1 == y2) break; //došlo se ke konečnému bodu
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += smerX;
            }
            if (e2 < dx) {
                err += dx;
                y1 += smerY;
            }
        }
    }
}
