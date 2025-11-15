// src/model/objectdata/Rectangle.java
package model.objectdata;

public class Rectangle extends Polygon {

    private static final double EPS = 1e-9;

    public Rectangle(Point2D point1, Point2D point2, Point2D heightPoint) {
        super();

        double ax = point1.getX();
        double ay = point1.getY();
        double bx = point2.getX();
        double by = point2.getY();

        // základna musí být horizontální
        Line base = new Line(point1, point2);
        if (!base.isHorizontal()) {
            throw new IllegalArgumentException("Základna musí být horizontální.");
        }
        // nenulová délka základny
        double baseLength = Math.abs(bx - ax);
        if (baseLength == 0.0) {
            throw new IllegalArgumentException("Základna musí být nenulová.");
        }
        // nenulová výška
        double height = heightPoint.getY() - ay;
        if (height == 0.0) {
            throw new IllegalArgumentException("Výška musí být nenulová.");
        }

        Point2D A = point1;
        Point2D B = point2;
        Point2D C = new Point2D(bx, by + height);
        Point2D D = new Point2D(ax, ay + height);

        addItem(A);
        addItem(B);
        addItem(C);
        addItem(D);
    }
}
