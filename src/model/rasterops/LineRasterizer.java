package model.rasterops;

import model.objectdata.Line;
import model.rasterdata.Raster;

import java.awt.*;

public abstract class LineRasterizer {

    Raster raster;
    Color color;

    public LineRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void rasterize(Line line) {
        setColor(new Color(line.getColor()));
        drawLine(line.getStart().x, line.getStart().y, line.getEnd().x, line.getEnd().y);
    }

    public void rasterize(int x1, int y1, int x2, int y2, Color color) {
        setColor(color);
        drawLine(x1, y1, x2, y2);
    }

    //triviální vykreslení úsečky
    protected abstract void drawLine(int x1, int y1, int x2, int y2);

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

}
