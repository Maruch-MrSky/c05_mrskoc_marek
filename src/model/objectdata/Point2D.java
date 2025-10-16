package model.objectdata;

public class Point2D {
    public int x, y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(double x, double y) {
        this.x = (int) Math.round(x);
        this.y = (int) Math.round(y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double distanceTo(Point2D other) {
        int dx = other.x - this.x; //q1-p1
        int dy = other.y - this.y; //q2-p2
        return Math.sqrt(dx*dx + dy*dy); //dx*dx místo Math.pow(dx,2) pro jednoduchost
    }

}
