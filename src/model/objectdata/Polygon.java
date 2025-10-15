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

    // done TODO addItemToIndex getFirst, getLast, clear
    public void addItemToIndex(int index, Point2D newItem) {
        if (index >= 0 && index <= polygon.size()) {
            polygon.add(index, newItem);
        }
    }

    // done TODO removeItem
    public void removeItem(int index) {
        if (index >= 0 && index <= polygon.size()) {
            polygon.remove(index);
        }
    }

    // done TODO getItem
    public Point2D getItem(int index) {
        if (index >= 0 && index < polygon.size()) {
            return polygon.get(index);
        }
        return null;
    }

    // done TODO getFisrt
    public Point2D getFirst() {
        if (polygon.size() > 0) {
            return getItem(0);
        }
        return null;
    }

    // done TODO getLast
    public Point2D getLast() {
        if (polygon.size() > 0) {
            return getItem(polygon.size() - 1);
        }
        return null;
    }

    // done TODO creal
    public void clear() {
        polygon.clear();
    }

    // done TODO findNearestPoint
    public int findNearestPoint(Point2D point) {
        if (polygon.size() > 0) {
            int nearestIndex = 0;
            double minDistance = polygon.get(0).distanceTo(point);
            for (int i = 1; i < polygon.size(); i++) {
                double distance = polygon.get(i).distanceTo(point);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestIndex = i;
                }
            }
            return nearestIndex;
        } else {
            return -1;
        }
    }

    // done TODO list všech bodů
    public ArrayList<Point2D> getPolygon() {
        return new ArrayList<>(polygon);
    }
}
