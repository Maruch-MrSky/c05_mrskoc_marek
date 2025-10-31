package model.rasterops.filler;

import model.objectdata.Point2D;
import model.rasterdata.Raster;

import java.awt.*;
import java.util.Stack;

public class SeedFill {

    private final Raster img;

    public SeedFill(Raster img) {
        this.img = img;
    }

    public void seedFill4(Point2D point, Color newColor) {
        seedFill(point.x, point.y, newColor, false);
    }
    
    public void seedFill8(Point2D point, Color newColor) {
        seedFill(point.x, point.y, newColor, true);
    }

    public void seedFill4(int px, int py, Color newColor) {
        seedFill(px, py, newColor, false);
    }

    public void seedFill8(int px, int py, Color newColor) {
        seedFill(px, py, newColor, true);
    }

    private void seedFill(int px, int py, Color newColor, boolean eightNeighbours) {
        if (!inBounds(px, py)) return; // bod mimo obrazovku
        if (img == newColor) return; // polygon už je vyplněný

        Color targetColor = new Color(img.getPixel(px, py), true);
        if (targetColor.equals(newColor)) return;

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(px, py));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            int x = p.x;
            int y = p.y;

            if (!inBounds(x, y)) continue;

            Color current = new Color(img.getPixel(x, y), true);
            if (!current.equals(targetColor)) continue;
            img.setPixel(x, y, newColor.getRGB());
            // rekurze pro 4 sousedy
            stack.push(new Point(x + 1, y));
            stack.push(new Point(x - 1, y));
            stack.push(new Point(x, y + 1));
            stack.push(new Point(x, y - 1));
            // rekurze pro 8 sousedů - diagonály
            if (eightNeighbours) {
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
