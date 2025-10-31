package model.rasterops.filler;

import model.rasterdata.Raster;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class SeedFill {

    private final Raster img;

    public SeedFill(Raster img) {
        this.img = img;
    }

    public void seedFill4(int px, int py, Color newColor) {
        seedFill(px, py, newColor, false);
    }

    public void seedFill8(int px, int py, Color newColor) {
        seedFill(px, py, newColor, true);
    }

    public void seedFill(int px, int py, Color newColor, boolean eightNeighbours) {
        if (!inBounds(px, py)) return; // bod mimo obrazovku
        if (img == newColor) return; // polygon uz je vyplneny

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(px, py));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            int x = p.x;
            int y = p.y;

            if (!inBounds(x, y)) continue;
            if (img.getPixel(x, y) != img.getPixel(px, py)) continue;

            img.setPixel(x, y, newColor.getRGB());
            // four neighbours
            stack.push(new Point(x + 1, y));
            stack.push(new Point(x - 1, y));
            stack.push(new Point(x, y + 1));
            stack.push(new Point(x, y - 1));

            if (eightNeighbours) {
                // eight neighbours - diagonals
                stack.push(new Point(x + 1, y + 1));
                stack.push(new Point(x - 1, y - 1));
                stack.push(new Point(x + 1, y - 1));
                stack.push(new Point(x - 1, y + 1));
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight();
    }
}
