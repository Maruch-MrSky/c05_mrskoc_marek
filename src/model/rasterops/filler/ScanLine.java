package model.rasterops.filler;

import model.objectdata.Line;
import model.objectdata.Polygon;
import model.rasterops.rasterizer.LineRasterizer;
import model.rasterops.rasterizer.PolygonRasterizer;

import java.util.ArrayList;
import java.util.List;

public class ScanLine {

    private Polygon poly;
    private LineRasterizer liner;
    private PolygonRasterizer polyLiner;

    public ScanLine(Polygon poly, LineRasterizer liner, PolygonRasterizer polyLiner) {
        this.poly = poly;
        this.liner = liner;
        this.polyLiner = polyLiner;
    }

    public void fill() {
        // 1. časť
        List<Line> edges = new ArrayList<>();

        // for iterujem cez polygon

        // if edge !isHorizontal()
        // edge.orientate()
        // pridam do zoznamu edges

        // v tomto momente je vytvoreny zoznam hran daneho polygonu

        int yMin, yMax;
        // yMin = poly.get(0).getY()
        // yMax porovnavam postupne s ostatnymi = cyklus

        // 2. časť
        // for loop iteruj od yMin po yMax
        // vytvorit zoznam pro prusecniky

        // iteruj zoznamom hran
        // existuje prusecnik? = intersectionExists
        // pokial ano, spocitam ho getIntersection(int y)
        // ulozim do zoznamu pre prusecniky

        // zoradenie priesecnikov lubovolnym algoritmom

        // vezmem jednotlive priesecniky a vykreslujem medzi nimi usecku
        // vykreslujem medzi kazdym lichym a sudym

        // konec cyklu

        // obtiahnem hranicu polygonu

    }
}
