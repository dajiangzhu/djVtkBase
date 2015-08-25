/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

/**
 *dd
 * @author dj
 */
public class djSurService {

    /**
     *
     * @param sur
     * @param seedPoint
     * @param ringNum
     * @param outFileName
     */
    public void getSurPatch(djVtkSurData sur, int seedPoint, int ringNum, String outFileName) {
        sur.getNeighbourPoints(seedPoint, ringNum);
        sur.writeToVtkFile(outFileName);
    }

    /**
     * get the subduct result from sur1, sur2(sur1-sur2)
     * NOTE:Now the points info of sur1 and sur2 is same, the only difference is cell info
     * @param sur1:large surface
     * @param sur2:small surface included by sur1
     */
    public void surfaceSubduction(djVtkSurData sur1, djVtkSurData sur2, String outFileName) {
        for (int m = 0; m < sur1.getAllCells().size(); m++) {
            for (int n = 0; n < sur2.getAllCells().size(); n++) {
                if (sur1.getcell(m).pointsList.get(0).pointId == sur2.getcell(n).pointsList.get(0).pointId) {
                    if (sur1.getcell(m).pointsList.get(1).pointId == sur2.getcell(n).pointsList.get(1).pointId) {
                        if (sur1.getcell(m).pointsList.get(2).pointId == sur2.getcell(n).pointsList.get(2).pointId) {
                            sur1.getcell(m).flag = 1;
                        }
                    }
                }
            }
        }

        sur1.cellsOutput.clear();
        for (int i = 0; i < sur1.getAllCells().size(); i++) {
            if (sur1.getcell(i).flag < 0) {
                sur1.cellsOutput.add(sur1.getcell(i));
            }
        }
        sur1.writeToVtkFile(outFileName);
    }
}
