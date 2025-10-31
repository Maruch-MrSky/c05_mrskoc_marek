package model.objectdata;

import java.util.ArrayList;

public class Polygon {

    private ArrayList<Point2D> polygon; // LinkedList

    public Polygon() {
        this.polygon = new ArrayList<>();
    }

    public void addItem(Point2D newItem) {
        polygon.add(newItem);
    }

    public int size() {
        return polygon.size();
    }

    public void addItemToIndex(int index, Point2D newItem) {
        if (index >= 0 && index <= polygon.size()) {
            polygon.add(index, newItem);
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < polygon.size()) {
            polygon.remove(index);
        }
    }

    public Point2D getItem(int index) {
        if (index >= 0 && index < polygon.size()) {
            return polygon.get(index);
        }
        return null;
    }

    public Point2D getFirst() {
        if (polygon.size() > 0) {
            return getItem(0);
        }
        return null;
    }

    public Point2D getLast() {
        if (polygon.size() > 0) {
            return getItem(polygon.size() - 1);
        }
        return null;
    }

    public void clear() {
        polygon.clear();
    }

    public boolean pointInPolygon(int x, int y) {
        boolean inside = false;
        int n = size();
        if (n < 3) return false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            Point2D pi = getItem(i);
            Point2D pj = getItem(j);
            double xi = pi.getX(), yi = pi.getY();
            double xj = pj.getX(), yj = pj.getY();
            boolean intersect = ((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi + 0.0) + xi);
            if (intersect) inside = !inside; // sudost křížení hran
        }
        return inside;
    }

    public boolean pointInPolygon(Point2D point) {
        return pointInPolygon(point.getX(), point.getY());
    }

    public int findNearestPoint(Point2D point) {
        if (polygon.isEmpty()) {
            return -1;
        }
        int nearestPolyIndex = 0;
        double minDistance = polygon.get(0).distanceTo(point);
        for (int i = 1; i < polygon.size(); i++) {
            double distance = polygon.get(i).distanceTo(point);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPolyIndex = i;
            }
        }
        return nearestPolyIndex;
    }

    public ArrayList<Point2D> getPolygon() {
        return new ArrayList<>(polygon);
    }
}
