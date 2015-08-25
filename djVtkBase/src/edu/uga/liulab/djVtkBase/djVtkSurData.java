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
public class djVtkSurData extends djVtkData {

    public djVtkSurData(String strSurFile) {
        super(djVtkDataDictionary.VTK_DATATYPE_SURFACE, strSurFile);
    }
    
    public djVtkSurData() {
    }

    public void calculateCellsArea() {
        System.out.println("Calculating the area of each trangle...");
        for (int i = 0; i < this.cells.size(); i++) {
            djVtkSurCell tmpSurCell = (djVtkSurCell) this.getcell(i);
            if (tmpSurCell != null && tmpSurCell.pointsList.size() == 3) {
                ((djVtkSurCell) (this.getcell(i))).cellArea = djVtkUtil.calTrangleArea(tmpSurCell.pointsList.get(0), tmpSurCell.pointsList.get(1), tmpSurCell.pointsList.get(2));
            }
        }
    }

    /**
     * if found the point return the index of the point.
     * if not found the point , return -1;
     * @param thePoint
     * @return
     */
    public int findThePoint(djVtkPoint thePoint) {
        djVtkPoint tmpPoint = null;
        for (int i = 0; i < this.points.size(); i++) {
            tmpPoint = this.getPoint(i);
            if (tmpPoint.isEqualTo(thePoint)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Note: OutPutCell has been written!!!
     * @param seedPoint
     * @param ringNum
     * @return
     */
    public Set<djVtkPoint> getNeighbourPoints(int seedPoint, int ringNum) {
        Set<djVtkPoint> selectedPoints = new HashSet<djVtkPoint>();
        Set<djVtkPoint> seedPoints = new HashSet<djVtkPoint>();
        Set<djVtkCell> selectedCells = new HashSet<djVtkCell>();
        selectedPoints.add(this.getPoint(seedPoint));

        for (int i = 0; i < ringNum; i++) {
            seedPoints.clear();
            seedPoints.addAll(selectedPoints);
            Iterator iterSeedPoints = seedPoints.iterator();
            while (iterSeedPoints.hasNext()) {
                //selectedCells.clear();//for outputcell
                selectedCells.addAll(((djVtkPoint) iterSeedPoints.next()).cellsList);
                Iterator iterSelectedCells = selectedCells.iterator();
                while (iterSelectedCells.hasNext()) {
                    selectedPoints.addAll(((djVtkCell) iterSelectedCells.next()).pointsList);
                }
            }
        }

        this.cellsOutput.clear();
        this.cellsOutput.addAll(selectedCells);
        return selectedPoints;
    }

    public Set<djVtkPoint> decimatePatch(int seedPtID, int decimateRate, int step) {
        Set<Integer> selectedPtIDs = new HashSet<Integer>();
        Set<djVtkPoint> selectedPoints = new HashSet<djVtkPoint>();
        selectedPtIDs.add(seedPtID);
        for (int i = 1; i < step; i++) {
            Set<Integer> neighbourPtIDs = this.getNeighbourPtIDsOnSpecificRing(seedPtID, decimateRate * i);
            Iterator itNeighbourPtIDs = neighbourPtIDs.iterator();
            Set<Integer> flag = new HashSet<Integer>();
            while (itNeighbourPtIDs.hasNext()) {
                int tmpPtID = (Integer) itNeighbourPtIDs.next();
                if (!flag.contains(tmpPtID)) {
                    flag.add(tmpPtID);
                    Set<Integer> tmpNeighbourPtIDs = this.getNeighbourPtIDs(tmpPtID, decimateRate - 1);
                    tmpNeighbourPtIDs.remove(tmpPtID);
                    neighbourPtIDs.removeAll(tmpNeighbourPtIDs);
                    itNeighbourPtIDs = neighbourPtIDs.iterator();
                }
            }
            selectedPtIDs.addAll(neighbourPtIDs);
        }
        Iterator itSelectedPtIDs = selectedPtIDs.iterator();
        while (itSelectedPtIDs.hasNext()) {
            int ptID = (Integer) itSelectedPtIDs.next();
            djVtkPoint newPoint = this.getPoint(ptID);
            selectedPoints.add(newPoint);
        }
        return selectedPoints;
    }

    public Set<Integer> getNeighbourPtIDsOnSpecificRing(int seedPtID, int ringNum) {
        Set<Integer> setPtIDsMax = this.getNeighbourPtIDs(seedPtID, ringNum);
        Set<Integer> setPtIDsMin = this.getNeighbourPtIDs(seedPtID, ringNum - 1);
        setPtIDsMax.removeAll(setPtIDsMin);
        return setPtIDsMax;
    }

    public Set<Integer> getNeighbourPtIDs(int seedPoint, int ringNum) {
        Set<djVtkPoint> selectedPoints = new HashSet<djVtkPoint>();
        Set<Integer> selectedPtIDs = new HashSet<Integer>();
        Set<djVtkPoint> seedPoints = new HashSet<djVtkPoint>();
        Set<djVtkCell> selectedCells = new HashSet<djVtkCell>();
        selectedPoints.add(this.getPoint(seedPoint));

        for (int i = 0; i < ringNum; i++) {
            seedPoints.clear();
            seedPoints.addAll(selectedPoints);
            Iterator iterSeedPoints = seedPoints.iterator();
            while (iterSeedPoints.hasNext()) {
                //selectedCells.clear();//for outputcell
                selectedCells.addAll(((djVtkPoint) iterSeedPoints.next()).cellsList);
                Iterator iterSelectedCells = selectedCells.iterator();
                while (iterSelectedCells.hasNext()) {
                    selectedPoints.addAll(((djVtkCell) iterSelectedCells.next()).pointsList);
                }
            }
        }

        Iterator itSelectPt = selectedPoints.iterator();
        while (itSelectPt.hasNext()) {
            djVtkPoint tmpPoint = (djVtkPoint) itSelectPt.next();
            selectedPtIDs.add(tmpPoint.pointId);
        }
        return selectedPtIDs;
    }

    public Map<String, List<djVtkObj>> getNeighbours(int seedPoint, int ringNum) {
        List<djVtkObj> pointsList = new ArrayList<djVtkObj>();
        List<djVtkObj> cellsList = new ArrayList<djVtkObj>();
        Map<String, List<djVtkObj>> neighbours = new HashMap<String, List<djVtkObj>>();
        Set<djVtkPoint> selectedPoints = new HashSet<djVtkPoint>();
        Set<djVtkPoint> seedPoints = new HashSet<djVtkPoint>();
        Set<djVtkCell> selectedCells = new HashSet<djVtkCell>();
        selectedPoints.add(this.getPoint(seedPoint));

        for (int i = 0; i < ringNum; i++) {
            seedPoints.clear();
            seedPoints.addAll(selectedPoints);
            Iterator iterSeedPoints = seedPoints.iterator();
            while (iterSeedPoints.hasNext()) {
                //selectedCells.clear();//for outputcell
                selectedCells.addAll(((djVtkPoint) iterSeedPoints.next()).cellsList);
                Iterator iterSelectedCells = selectedCells.iterator();
                while (iterSelectedCells.hasNext()) {
                    selectedPoints.addAll(((djVtkCell) iterSelectedCells.next()).pointsList);
                }
            }
        }
        pointsList.addAll(selectedPoints);
        cellsList.addAll(selectedCells);
        neighbours.put("pointsList", pointsList);
        neighbours.put("cellsList", cellsList);
        return null;
    }
}
