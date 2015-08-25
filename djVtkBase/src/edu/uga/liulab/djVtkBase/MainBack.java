/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

import com.xinapse.loadableimage.InvalidImageException;
import com.xinapse.loadableimage.ParameterNotSetException;
import com.xinapse.multisliceimage.MultiSliceImageException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dj
 */
public class MainBack {

    public void testForSyntheticData(djVtkSurData sur, String fileName) {
        int numOfPoints = sur.points.size();
        int numOfCells = sur.cells.size();
        int numOfFiberTypes = 3;// this means there are 3 types of fibers
        float fiberDesity = 6.0f;// this means : 3/cell
        int ringNum = 5;

        float[][] cellData = new float[numOfCells][numOfFiberTypes];
        float[][] pointData = new float[numOfPoints][numOfFiberTypes];
        for (int i = 0; i < numOfPoints; i++) {
            for (int j = 0; j < numOfFiberTypes; j++) {
                pointData[i][j] = 0;
            }
        }

        //initial cellData
        for (int i = 0; i < numOfCells; i++) {
            if (sur.cellsScalarData.get("SyntheticBoundary").get(i).trim().equals("0")) {
                cellData[i][0] = fiberDesity / 2;
                cellData[i][1] = fiberDesity / 2;
                cellData[i][2] = 0;
            }
            if (sur.cellsScalarData.get("SyntheticBoundary").get(i).trim().equals("1")) {
                cellData[i][0] = 0;
                cellData[i][1] = fiberDesity;
                cellData[i][2] = 0;
            }
            if (sur.cellsScalarData.get("SyntheticBoundary").get(i).trim().equals("2")) {
                cellData[i][0] = 0;
                cellData[i][1] = 0;
                cellData[i][2] = fiberDesity;
            }
        }
        //calculate pointData
        float[] tmpSumCellData = new float[numOfFiberTypes];
        float tmpSum = 0;
        for (int i = 0; i < numOfPoints; i++) {
            tmpSum = 0;
            for (int k = 0; k < numOfFiberTypes; k++) {
                tmpSumCellData[k] = 0;
            }
            sur.getNeighbourPoints(i, ringNum);
            for (int j = 0; j < sur.cellsOutput.size(); j++) {
                for (int k = 0; k < numOfFiberTypes; k++) {
                    tmpSumCellData[k] += cellData[sur.cellsOutput.get(j).cellId][k];
                    tmpSum += cellData[sur.cellsOutput.get(j).cellId][k];
                }
            }
            for (int k = 0; k < numOfFiberTypes; k++) {
                pointData[i][k] = tmpSumCellData[k] / tmpSum;
            }
        }

        System.out.println("Begin to write file:" + fileName + "...");
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName);
            for (int i = 0; i < numOfPoints; i++) {
                for (int k = 0; k < numOfFiberTypes; k++) {
                    fw.write(String.valueOf(pointData[i][k]) + " ");
                }
                fw.write("\r\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(djVtkData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(djVtkData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Write file done!");

        int i = 0;


    }

    /**
     * map patches(patchList) to the sur
     * @param sur
     * @param patchList
     */
    public void devideSurByPatches(djVtkSurData sur, List<djVtkSurData> patchList) {
        List<String> attriList = new ArrayList<String>();
        djVtkSurData tmpPatch = null;
        for (int k = 0; k < sur.cells.size(); k++) {
            nextCell:
            for (int i = 0; i < patchList.size(); i++) {
                tmpPatch = patchList.get(i);
                for (int j = 0; j < tmpPatch.cells.size(); j++) {
                    if (sur.getcell(k).pointsList.get(0).pointId == tmpPatch.getcell(j).pointsList.get(0).pointId) {
                        if (sur.getcell(k).pointsList.get(1).pointId == tmpPatch.getcell(j).pointsList.get(1).pointId) {
                            if (sur.getcell(k).pointsList.get(2).pointId == tmpPatch.getcell(j).pointsList.get(2).pointId) {
                                attriList.add(String.valueOf(i));
                                break nextCell;
                            }
                        }
                    }
                }
            }
        }
        sur.cellsOutput.clear();
        sur.cellsOutput.addAll(sur.cells);
        sur.cellsScalarData.put("SyntheticBoundary", attriList);
        sur.writeToVtkFile("0722_devideSub1L.vtk");

    }

    public void calAttriDistribution(djVtkFiberData fiberData, String attriName, String outPutFileName) {
        int nAttriType = 7;
        int[] fiberAttriDistribution = new int[] {0,0,0,0,0,0,0};
        for (int i = 0; i < fiberData.cellsScalarData.get(attriName).size(); i++) {
            fiberAttriDistribution[Integer.valueOf(fiberData.cellsScalarData.get(attriName).get(i))]++;
        }

        System.out.println("Begin to write file:" + outPutFileName + "...");
        FileWriter fw = null;
        try {
            fw = new FileWriter(outPutFileName);
            for (int i = 0; i < nAttriType; i++) {
                fw.write(fiberAttriDistribution[i] + " ");
            }
        } catch (IOException ex) {
            Logger.getLogger(djVtkData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(djVtkData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Write file done!");
    }

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) throws MultiSliceImageException, IOException, ParameterNotSetException, InvalidImageException {
//
//
//        /**Test of structure file**/
////        long begin = System.currentTimeMillis();
//        djVtkSurData surData = new djVtkSurData("sub1_PCG_L_HLabel.vtk");
//        //djSurService surService = new djSurService();
////        surService.getSurPatch(surData, 136, 15, "0721_patch1.vtk");
////        djVtkSurData surData1 = new djVtkSurData("0721_patch1.vtk");
////        djVtkSurData surData2 = new djVtkSurData("0721_patch4.vtk");
////        djVtkSurData surData3 = new djVtkSurData("0721_patch3.vtk");
////        List<djVtkSurData> patches = new ArrayList<djVtkSurData>();
////        patches.add(surData1);
////        patches.add(surData2);
////        patches.add(surData3);
////        Main handle = new Main();
////        djVtkSurData surData = new djVtkSurData("0722_devideSub1L.vtk");
////        handle.testForSyntheticData(surData, "0723_devideSub1L.vtk_fd6_ft3_rn5_secondcase.txt");
////        handle.devideSurByPatches(surData, patches);
//        //surService.getSurPatch(surData2, 776, 15, "0721_patch3.vtk");
//        //surService.surfaceSubduction(surData1, surData2, "0721_patch4.vtk");
//        djVtkFiberData fiberData = new djVtkFiberData("fiberReady1_label.vtk");
////        handle.calAttriDistribution(fiberData, "LookupTable", "fiberReady8_label_shapeDistribution.txt");
//        djVtkHybridData hybridData = new djVtkHybridData(surData,fiberData);
//        //hybridData.calAttriDistribution("LookupTable", 1, "surfReady1_shapeSimi.txt");
//        djVtkFiberData newFiberData = hybridData.getFibersConnectToSurface();
//        newFiberData.writeToVtkFile("sub1_djFiber_HLabel.vtk");
////        long end = System.currentTimeMillis();
////        System.out.println( "Total costs:"  + (end - begin) +  " ms" );
//        /**End of test of structure file**/
//        /**Test for Nifti file**/
////        long heapMaxSize = Runtime.getRuntime().maxMemory();
////        //NIFTIImage testNifiti = new NIFTIImage("fmriReadyRetrend_1", "r");
////        // NIFTIImage testNifiti = new NIFTIImage("gm_dti0_1", "r");
////        djNiftiData newVolumeData = new djNiftiData("fmriReadyRetrend_1");
////        int nDim = newVolumeData.nDims;
////
////        for (int t = 0; t < newVolumeData.tSize; t++) {
////            System.out.println(newVolumeData.getValueBasedOnVolumeCoordinate(25, 52, 24, t));
////        }
//        /**End of test for Nifti file**/
////        djVtkSurData surData = new djVtkSurData("surfReady.vtk");
////        djNiftiData testNifiti = new djNiftiData("gm_dti0_1");
////        float[] pointCoord = new float[3];
////        pointCoord[0] = surData.getPoint(145).x;
////        pointCoord[1] = surData.getPoint(145).y;
////        pointCoord[2] = surData.getPoint(145).z;
////        int[] volumeCoord = testNifiti.convertFromPhysicalToVolume(pointCoord);
//        // surData.getNeighbourPoints(24726, 2);
////        djVtkFiberData fiberData = new djVtkFiberData("fiberReady1_label.vtk");
////        djNiftiData newVolumeData = new djNiftiData("fmriReadyRetrend_1");
////        djVtkHybridData hybridData = new djVtkHybridData(fiberData,newVolumeData);
////        hybridData.getFiberFromVlumeBox(37, 69, 46, 1).writeToVtkFile("sub1_tongueR_VolFibers.vtk");
//        int i = 0;
//
//
//
////        test1 tt = new test1();
////        tt.testWrite();
//    }
}
