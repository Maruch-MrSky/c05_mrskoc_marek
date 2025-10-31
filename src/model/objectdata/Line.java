package model.objectdata;

import java.awt.*;

public class Line {
    private Point2D start;
    private Point2D end;
    private final int color;

    public Line(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
        this.color = Color.WHITE.getRGB();
    }

    public Line(Point2D start, Point2D end, int color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Line(int x1, int y1, int x2, int y2) {
        this.start = new Point2D(x1, y1);
        this.end = new Point2D(x2, y2);
        this.color = Color.WHITE.getRGB();
    }

    public Line(int x1, int y1, int x2, int y2, int color) {
        this.start = new Point2D(x1, y1);
        this.end = new Point2D(x2, y2);
        this.color = color;
    }

    public Point2D getStart() {
        return start;
    }

    public Point2D getEnd() {
        return end;
    }

    // min a max hodnoty x a y
    public int getXmin() {
        return Math.min(start.getX(), end.getX());
    }

    public int getXmax() {
        return Math.max(start.getX(), end.getX());
    }

    public int getYmin() {
        return Math.min(start.getY(), end.getY());
    }

    public int getYmax() {
        return Math.max(start.getY(), end.getY());
    }

    public int getColor() {
        return color;
    }

    public boolean isHorizontal() {
        return start.getY() == end.getY();
    }

    public void orientate() {
        if (start.getY() > end.getY()) {
            Point2D temp = start;
            start = end;
            end = temp;
        }
    }

    public double getIntersection(int y) {
        if (start.getY() == end.getY()) {
            throw new IllegalArgumentException("úsečka je rovnoběžaná s osou X");
        }
        // lineární interpolace: x = x1 + (y - y1) * (x2 - x1) / (y2 - y1)
        return start.getX() + (double)(y - start.getY()) * (end.getX() - start.getX()) / (end.getY() - start.getY());
    }


}
