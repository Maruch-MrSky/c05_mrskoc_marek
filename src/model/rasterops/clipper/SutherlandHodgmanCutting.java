package model.rasterops.clipper;

import model.objectdata.Point2D;
import model.objectdata.Polygon;

import java.util.ArrayList;
import java.util.List;

public class SutherlandHodgmanCutting {

    private static final double EPS = 1e-9;

    public Polygon cut(Polygon subject, Polygon clipper) {
        if (subject == null || clipper == null) return new Polygon();
        if (subject.size() < 3) return new Polygon();
        if (clipper.size() < 3) return new Polygon();

        List<Point2D> output = subject.getPolygon();
        boolean clipperCCW = polygonSignedArea(clipper) > 0.0;

        for (int i = 0; i < clipper.size(); i++) {
            Point2D a = clipper.getItem(i);
            Point2D b = clipper.getItem((i + 1) % clipper.size());
            List<Point2D> input = output;
            output = new ArrayList<>();
            if (input.isEmpty()) break;

            Point2D s = input.get(input.size() - 1);
            for (Point2D e : input) {
                boolean eInside = isInside(e, a, b, clipperCCW);
                boolean sInside = isInside(s, a, b, clipperCCW);

                if (sInside && eInside) {
                    // případ 1: oba uvnitř -> přidat e
                    output.add(e);
                } else if (sInside && !eInside) {
                    // případ 2: s uvnitř, e venku -> přidat průsečík
                    Point2D ip = intersect(s, e, a, b);
                    if (ip != null) output.add(ip);
                } else if (!sInside && eInside) {
                    // případ 3: s venku, e uvnitř -> přidat průsečík a e
                    Point2D ip = intersect(s, e, a, b);
                    if (ip != null) output.add(ip);
                    output.add(e);
                }
                // případ 4: oba venku -> nic
                s = e;
            }
        }

        Polygon result = new Polygon();
        for (Point2D p : output) result.addItem(p);
        return result;
    }

    private boolean isInside(Point2D p, Point2D a, Point2D b, boolean clipperCCW) {
        // cross = (b-a) x (p-a)
        double cross = (b.getX() - a.getX()) * (p.getY() - a.getY()) - (b.getY() - a.getY()) * (p.getX() - a.getX());
        return clipperCCW ? cross >= -EPS : cross <= EPS;
    }

    private Point2D intersect(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        // řádky p1->p2 a p3->p4, řeší parametry t,u: p1 + t*(p2-p1) = p3 + u*(p4-p3)
        double x1 = p1.getX(), y1 = p1.getY();
        double x2 = p2.getX(), y2 = p2.getY();
        double x3 = p3.getX(), y3 = p3.getY();
        double x4 = p4.getX(), y4 = p4.getY();

        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (Math.abs(denom) < EPS) return null; // paralelní nebo téměř

        double px = ( (x1*y2 - y1*x2)*(x3 - x4) - (x1 - x2)*(x3*y4 - y3*x4) ) / denom;
        double py = ( (x1*y2 - y1*x2)*(y3 - y4) - (y1 - y2)*(x3*y4 - y3*x4) ) / denom;

        return new Point2D(px, py);
    }

    private double polygonSignedArea(Polygon poly) {
        double area = 0.0;
        int n = poly.size();
        for (int i = 0; i < n; i++) {
            Point2D a = poly.getItem(i);
            Point2D b = poly.getItem((i + 1) % n);
            area += a.getX() * b.getY() - b.getX() * a.getY();
        }
        return area / 2.0;
    }
}
