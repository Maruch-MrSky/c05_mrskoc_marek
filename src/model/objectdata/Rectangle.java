package model.objectdata;

public class Rectangle extends Polygon {

    public Rectangle(Point2D point1, Point2D point2, Point2D heightPoint) {
        // rectangle dostane dva body základny a jeden bod určující výšku
        // z těchto bodů počítáme třetí a čtvrtý bod
        // heightPoint neukládáme
        super();
        double ax = point1.getX();
        double ay = point1.getY();
        double bx = point2.getX();
        double by = point2.getY();
        double hx = heightPoint.getX();
        double hy = heightPoint.getY();

        double dx = bx - ax;
        double dy = by - ay;
        double baseLenght = Math.sqrt(dx * dx + dy * dy);
        if (baseLenght == 0.0) {
            throw new IllegalArgumentException("");
        }

        // jednotkový normál k základu
        double nx = -dy / baseLenght;
        double ny = dx / baseLenght;

        // výška
        double ahx = hx - ax;
        double ahy = hy - ay;
        double height = ahx * nx + ahy * ny;

        // vrcholy obdélníku
        Point2D A = point1;
        Point2D B = point2;
        Point2D C = new Point2D(bx + nx * height, by + ny * height);
        Point2D D = new Point2D(ax + nx * height, ay + ny * height);

        addItem(A);
        addItem(B);
        addItem(C);
        addItem(D);
    }
}

