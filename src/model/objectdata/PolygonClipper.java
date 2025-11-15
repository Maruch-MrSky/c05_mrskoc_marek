package model.objectdata;

import java.util.Collection;

public class PolygonClipper extends Polygon {

    public PolygonClipper() {
        super();
    }

    public PolygonClipper(Collection<Point2D> points) {
        this();
        if (points != null) {
            for (Point2D p : points) addItem(p);
        }
    }

    public static PolygonClipper fromPolygon(Polygon poly) {
        PolygonClipper polyClip = new PolygonClipper();
        if (poly != null) {
            for (int i = 0; i < poly.size(); i++) {
                polyClip.addItem(poly.getItem(i));
            }
        }
        return polyClip;
    }

    public void validate() {
        if (size() < 5) {
            throw new IllegalArgumentException("ořezávací polygon musí mít minimálně 5 vrcholů");
        }
    }

    public boolean isValid() {
        return size() >= 5;
    }

    // pravydelný pětiúhelník jako ořezávací polygon
    public static PolygonClipper regularPentagon(double centerX, double centerY, double radius) {
        PolygonClipper polyClip = new PolygonClipper();
        double startAngle = -Math.PI / 2.0;
        for (int i = 0; i < 5; i++) {
            double angle = startAngle + 2.0 * Math.PI * i / 5.0;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            polyClip.addItem(new Point2D(x, y));
        }
        return polyClip;
    }
}
