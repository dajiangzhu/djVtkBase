/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dj
 */
public class djVtkUtil {

    public static float calDistanceOfPoints(djVtkPoint pt1, djVtkPoint pt2) {
        return (float) java.lang.Math.sqrt(java.lang.Math.pow((pt1.x - pt2.x), 2) + java.lang.Math.pow((pt1.y - pt2.y), 2) + java.lang.Math.pow((pt1.z - pt2.z), 2));
    }

    public static ArrayList Normalize(ArrayList<Float> dataList) {
        float tmpMax = 0.0f;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i) > tmpMax) {
                tmpMax = dataList.get(i);
            }
        }
        for (int i = 0; i < dataList.size(); i++) {
            dataList.set(i, dataList.get(i) / tmpMax);
        }
        return dataList;
    }

    public static void writeArrayListToFile(List<String> dataList, String fileName) {
        try {
            System.out.println("Begin to write file:" + fileName + "...");
            FileWriter fw = null;
            fw = new FileWriter(fileName);
            for (int i = 0; i < dataList.size(); i++) {
                fw.write(dataList.get(i) + "\r\n");
            }
            fw.close();
            System.out.println("Write file done!");
        } catch (IOException ex) {
            Logger.getLogger(djVtkUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeDoubleArrayListToFile(List<List<Double>> dataList, String strSeperate, int secondDim, String fileName) {
        try {
            System.out.println("Begin to write file:" + fileName + "...");
            FileWriter fw = null;
            fw = new FileWriter(fileName);
            for (int i = 0; i < dataList.size(); i++) {
                fw.write(String.valueOf(dataList.get(i).get(0)));
                for (int j = 1; j < secondDim; j++) {
                    fw.write(strSeperate + dataList.get(i).get(j));
                }
                fw.write("\r\n");

            }
            fw.close();
            System.out.println("Write file done!");
        } catch (IOException ex) {
            Logger.getLogger(djVtkUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeArrayListToFile(List<List<Float>> dataList, String strSeperate, int secondDim, String fileName) {
        try {
            System.out.println("Begin to write file:" + fileName + "...");
            FileWriter fw = null;
            fw = new FileWriter(fileName);
            for (int i = 0; i < dataList.size(); i++) {
                fw.write(String.valueOf(dataList.get(i).get(0)));
                for (int j = 1; j < secondDim; j++) {
                    fw.write(strSeperate + dataList.get(i).get(j));
                }
                fw.write("\r\n");

            }
            fw.close();
            System.out.println("Write file done!");
        } catch (IOException ex) {
            Logger.getLogger(djVtkUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeStringArrayListToFile(List<List<String>> dataList,String strSeperate, int secondDim, String fileName) {
        try {
            System.out.println("begin to write to file:" + fileName);
            System.out.println("Begin to write file:" + fileName + "...");
            FileWriter fw = null;
            fw = new FileWriter(fileName);
            for (int i = 0; i < dataList.size(); i++) {
                fw.write(String.valueOf(dataList.get(i).get(0)));
                for (int j = 1; j < secondDim; j++) {
                    fw.write(strSeperate + dataList.get(i).get(j));
                }
                fw.write("\r\n");

            }
            fw.close();
            System.out.println("Write file done!");
            System.out.println("That is all!");
        } catch (IOException ex) {
            Logger.getLogger(djVtkUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<String> loadFileToArrayList(String fileName) {
        List<String> resultList = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                //System.out.println(strLine);
                resultList.add(strLine);
            }//while
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return resultList;
    }

    public static float calTrangleArea(djVtkPoint pt1, djVtkPoint pt2, djVtkPoint pt3) {
        float s12, s13, s23, s;
        s12 = djVtkUtil.calDistanceOfPoints(pt1, pt2);
        s13 = djVtkUtil.calDistanceOfPoints(pt1, pt3);
        s23 = djVtkUtil.calDistanceOfPoints(pt2, pt3);
        s = 0.5f * (s12 + s13 + s23);
        return (float) java.lang.Math.sqrt(s * (s - s12) * (s - s13) * (s - s23));
    }
    
    public static void writeToPointsVtkFile(String fileName, List<djVtkPoint> ptList) {
        System.out.println("Begin to write file:" + fileName + "...");
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName);
            fw.write("# vtk DataFile Version 3.0\r\n");
            fw.write("vtk output\r\n");
            fw.write("ASCII\r\n");
            fw.write("DATASET POLYDATA\r\n");
            // print points info
            fw.write("POINTS " + ptList.size() + " float\r\n");
            for (int j = 0; j < ptList.size(); j++) {
                fw.write(ptList.get(j).x + " "
                        + ptList.get(j).y + " "
                        + ptList.get(j).z + "\r\n");
            }
            // print VERTICES info
            fw.write("VERTICES " + ptList.size() + " " + ptList.size() * 2 + " \r\n");
            for (int i = 0; i < ptList.size(); i++) {
                fw.write("1 " + i + "\r\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(djVtkData.class.getName()).log(Level.SEVERE, null,
                    ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(djVtkData.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        System.out.println("Write file done!");
        System.out.println("That is all!");
    }
    
    public static void writeVtkMatrix(double[][] mat, int dimRow, int dimColumn, String fileName)
	{
		double blockwidth=4.0;
		double blockheight=4.0;
		double interval=1.0;
		int numpoint=4*dimRow*dimColumn;
		int numcell = dimRow*dimColumn;
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName);
			fw.write("# vtk DataFile Version 3.0 \r\n");
			fw.write("vtk output \r\n");
			fw.write("ASCII \r\n");
			fw.write("DATASET POLYDATA \r\n");
			fw.write("POINTS "+numpoint+" float \r\n");
			
			for (int x = 0; x < dimRow; x++) {
				for (int y = 0; y < dimColumn; y++) {
					double rx, ry;
					rx = (blockwidth+interval)*dimColumn/2 + (blockwidth+interval)*y + interval;
					ry = (blockheight+interval)*dimRow/2 - (blockheight+interval)*x + interval;
					fw.write(rx+" "+ry+" 0.0 \r\n" );
					fw.write((rx+blockwidth)+" "+ry+" 0.0\r\n");
					fw.write(rx+" "+(ry+blockheight)+" 0.0\r\n");
					fw.write((rx+blockwidth)+" "+(ry+blockheight)+" 0.0\r\n");
				}
			} //for x
			fw.write("POLYGONS "+numcell+" "+5*numcell+"\r\n");
			for (int icell = 0; icell < numcell; icell++) {
				fw.write("4 "+(4*icell)+" "+(4*icell+1)+" "+(4*icell+3)+" "+(4*icell+2)+"\r\n");
			}
			fw.write("POINT_DATA "+numpoint+"\r\n");
			fw.write("SCALARS color float \r\n");
			fw.write("LOOKUP_TABLE default \r\n");
			
			for (int x = 0; x < dimRow; x++) {
				for (int y = 0; y < dimColumn; y++) {
					float val = (float)mat[x][y];
					fw.write(val+" "+val+" "+val+" "+val+"\r\n");
				}
			}
			fw.close();	
			System.out.println("Write file: "+fileName+" done...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
