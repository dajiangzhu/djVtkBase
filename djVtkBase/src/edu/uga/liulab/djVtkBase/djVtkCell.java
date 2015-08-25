/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

import java.util.*;

/**
 *
 * @author dj
 */
public class djVtkCell extends djVtkObj {

    public int cellId = -1;
    public List<djVtkPoint> pointsList = new ArrayList<djVtkPoint>();
    public int flag = -1;
    public List<Set<Integer>> connSurPnts;
    public float xBound_min = 0.0f;
    public float xBound_max = 0.0f;
    public float yBound_min = 0.0f;
    public float yBound_max = 0.0f;
    public float zBound_min = 0.0f;
    public float zBound_max = 0.0f;

    public void initial_ConnSurPnts() {
        connSurPnts = new ArrayList<Set<Integer>>(2);
        connSurPnts.add(new HashSet<Integer>());
        connSurPnts.add(new HashSet<Integer>());
    }

    public djVtkCell() {
        this.initial_ConnSurPnts();
    }

    public djVtkCell(int cellIndex) {
        this.cellId = cellIndex;
        this.initial_ConnSurPnts();
    }

    public void calBoundOfCell() {
        float xMin = 1000.0f;
        float yMin = 1000.0f;
        float zMin = 1000.0f;
        float xMax = 0.0f;
        float yMax = 0.0f;
        float zMax = 0.0f;

        for (int i = 0; i < this.pointsList.size(); i++) {
            if (this.pointsList.get(i).x < xMin) {
                xMin = this.pointsList.get(i).x;
            }
            if (this.pointsList.get(i).x > xMax) {
                xMax = this.pointsList.get(i).x;
            }

            if (this.pointsList.get(i).y < yMin) {
                yMin = this.pointsList.get(i).y;
            }
            if (this.pointsList.get(i).y > yMax) {
                yMax = this.pointsList.get(i).y;
            }

            if (this.pointsList.get(i).z < zMin) {
                zMin = this.pointsList.get(i).z;
            }
            if (this.pointsList.get(i).z > zMax) {
                zMax = this.pointsList.get(i).z;
            }
        }

        this.xBound_min = xMin;
        this.xBound_max = xMax;
        this.yBound_min = yMin;
        this.yBound_max = yMax;
        this.zBound_min = zMin;
        this.zBound_max = zMax;
    }
}
