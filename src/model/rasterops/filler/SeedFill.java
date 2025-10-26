package model.rasterops.filler;

import model.rasterdata.Raster;

import java.awt.*;

public class SeedFill {

    private final Raster img;

    public SeedFill(Raster img) {
        this.img = img;
    }

    public void seedFill4(int x, int y, Color newCol) {
        // podminka pro osetreni hranice obrazovky
        // podminka img.getPixel(x, y) == backgroundColor
        // img.setPixel(x, y, newCol);
        // seedFill4(x + 1, y, newCol);
        // seedFill4(x - 1, y, newCol);
        // seedFill4(x, y + 1, newCol);
        // seedFill4(x, y - 1, newCol);
    }

    public void seedFill8() {
        // TODO
    }

}
