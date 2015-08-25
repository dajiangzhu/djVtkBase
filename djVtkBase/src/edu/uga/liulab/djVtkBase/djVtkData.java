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
public class djVtkData extends djVtkObj {

	public int nVtkDataType = -1;
	public String cell_alias = "";
	public int nPointNum = -1;
	public int nCellNum = -1;
	public List<djVtkPoint> points = new ArrayList<djVtkPoint>();
	public List<djVtkCell> cells = new ArrayList<djVtkCell>();
	public List<djVtkCell> cellsOutput = new ArrayList<djVtkCell>();
	public Map<String, List<String>> pointsScalarData = new HashMap<String, List<String>>();
	public Map<String, List<String>> cellsScalarData = new HashMap<String, List<String>>();
	public float[][] surBound = new float[3][2];

	public djVtkData() {
	}

	public djVtkData(int dataType, String fileName) {
		this.nVtkDataType = dataType;
		switch (this.nVtkDataType) {
		case djVtkDataDictionary.VTK_DATATYPE_SURFACE:
			this.cell_alias = djVtkDataDictionary.VTK_FIELDNAME_SURFACE_CELL;
			System.out.println("Begin to load Surface Data from :" + fileName + "...");
			break;
		case djVtkDataDictionary.VTK_DATATYPE_FIBERS:
			this.cell_alias = djVtkDataDictionary.VTK_FIELDNAME_FIBER_CELL;
			System.out.println("Begin to load Fiber Data from :" + fileName + "...");
			break;
		default:
			System.out.println("DataType is Unknown!!!");
			break;
		}
		this.initialData(fileName);
	}

	private void initialData(String fileName) {
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String[] tmpStringArray;
			String[] tmpStringArray1;
			String[] tmpStringArray2;

			while ((strLine = br.readLine()) != null) {
				// System.out.println(strLine);
				tmpStringArray = strLine.split(" ");
				if (tmpStringArray.length > 0) {
					if (tmpStringArray[0].equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_POINTS))// points data
					{
						this.nPointNum = Integer.valueOf(tmpStringArray[1]);
						for (int i = 0; i < this.nPointNum;) {
							do {
								strLine = br.readLine();
							} while (strLine.trim().length() == 0);
							tmpStringArray1 = strLine.split("\\s+");
							int pointNumWithinLine = tmpStringArray1.length / 3;// of course, make sure all coordinates of one point in one line
							for (int j = 0; j < pointNumWithinLine; j++) {
								djVtkPoint newPoint = new djVtkPoint(i, Float.valueOf(tmpStringArray1[j * 3]),
										Float.valueOf(tmpStringArray1[j * 3 + 1]), Float.valueOf(tmpStringArray1[j * 3 + 2]));
								this.points.add(newPoint);
								i++;
							}
						}
					}// END OF POINT

					if (tmpStringArray[0].equalsIgnoreCase(this.cell_alias))// cell data
					{
						this.nCellNum = Integer.valueOf(tmpStringArray[1]);
						for (int i = 0; i < this.nCellNum; i++) {
							do {
								strLine = br.readLine();
							} while (strLine.trim().length() == 0);
							tmpStringArray1 = strLine.split("\\s+");
							djVtkCell newCell = new djVtkCell(i);
							for (int j = 1; j < tmpStringArray1.length; j++) {
								djVtkPoint tmpPoint = this.points.get(Integer.valueOf(tmpStringArray1[j]));
								newCell.pointsList.add(tmpPoint);
								tmpPoint.cellsList.add(newCell);
							}
							this.cells.add(newCell);
						}
					}// END OF CELL

					if (tmpStringArray[0].equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_POINTDATA))// point attribute
					{
						do {
							strLine = br.readLine();
						} while (strLine.trim().length() == 0);
						tmpStringArray1 = strLine.split("\\s+");
						while (tmpStringArray1.length > 0
								&& (tmpStringArray1[0].equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_SCALAR) || tmpStringArray1[0]
										.equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_VECTORS))) {
							List<String> newScalarList = new ArrayList<String>();
							String scalarName = tmpStringArray1[1];
							if (tmpStringArray1[0].equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_SCALAR)) {
								do {
									strLine = br.readLine();
								} while (strLine.trim().length() == 0);// lookup_table

							}

							for (int i = 0; i < this.nPointNum; i++) {
								do {
									strLine = br.readLine();
								} while (strLine.trim().length() == 0);
								tmpStringArray2 = strLine.split("\\s+");
								newScalarList.addAll(Arrays.asList(tmpStringArray2));
							}

							// Note!!! Currently only load Scalars. Do NOT support vecor or color_scalars
							if (tmpStringArray1[0].equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_SCALAR)) {
								this.pointsScalarData.put(scalarName, newScalarList);
							}
							do {
								strLine = br.readLine();
							} while (strLine != null && strLine.trim().length() == 0);

							if (strLine != null) {
								tmpStringArray1 = strLine.split("\\s+");
							} else {
								break;
							}
						}

						System.out.println("~~~~~~~~~~~~~"+strLine+" and "+tmpStringArray1[0]+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
						while (tmpStringArray1[0].equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_COLOR_SCALARS)) {
							List<String> newScalarList = new ArrayList<String>();
							String scalarName = tmpStringArray1[1];
							int compNum = Integer.valueOf(tmpStringArray1[2]);
							System.out.println("compNum="+compNum);

							for (int i = 0; i < this.nPointNum;) {
								do {
									strLine = br.readLine();
								} while (strLine.trim().length() == 0);
								tmpStringArray2 = strLine.split(" ");
//								System.out.println("this line is : "+ strLine);
								int attriNumWithinLine = tmpStringArray2.length / compNum;// of course, make sure all coordinates of one point in one

								for (int j = 0; j < attriNumWithinLine; j++) {
									String currentAttri = "";// line
									for (int k = 0; k < compNum; k++)
										currentAttri = currentAttri + tmpStringArray2[j * compNum + k] + " ";
									newScalarList.add(currentAttri);
									i++;
								}
							} // for all points

							// Note!!! Currently only load Scalars. Do NOT support vecor or color_scalars
							System.out.println("before add: this.pointsScalarData.size = "+this.pointsScalarData.size());
							this.pointsScalarData.put(scalarName, newScalarList);
							System.out.println("after add: this.pointsScalarData.size = "+this.pointsScalarData.size());

							do {
								strLine = br.readLine();
							} while (strLine != null && strLine.trim().length() == 0);

							if (strLine != null) {
								tmpStringArray1 = strLine.split(" ");
							} else {
								break;
							}
						}

					}// END OF POINT_DATA

					if (tmpStringArray[0].equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_CELLDATA))// cell attribute
					{
						do {
							strLine = br.readLine();
						} while (strLine.trim().length() == 0);
						tmpStringArray1 = strLine.split("\\s+");
						while (tmpStringArray1.length > 0 && tmpStringArray1[0].equalsIgnoreCase(djVtkDataDictionary.VTK_FIELDNAME_SCALAR)) {
							List<String> newScalarList = new ArrayList<String>();
							String scalarName = tmpStringArray1[1];
							do {
								strLine = br.readLine();
							} while (strLine.trim().length() == 0);// lookup_table
							for (int i = 0; i < this.nCellNum; i++) {
								do {
									strLine = br.readLine();
								} while (strLine.trim().length() == 0);
								tmpStringArray2 = strLine.split("\\s+");
								newScalarList.addAll(Arrays.asList(tmpStringArray2));
							}
							this.cellsScalarData.put(scalarName, newScalarList);

							do {
								strLine = br.readLine();
							} while (strLine != null && strLine.trim().length() == 0);

							if (strLine != null) {
								tmpStringArray1 = strLine.split("\\s+");
							} else {
								break;
							}

						}
					}// END OF CELL_DATA
				}// if(tmpStringArray.length > 0)
			}// while
			br.close();
			in.close();
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void calSurDataBox() {
		for (int i = 0; i < 3; i++) {
			surBound[i][0] = 1000.0f;
			surBound[i][1] = -1000.0f;
		}
		for (int ptIndex = 0; ptIndex < this.nPointNum; ptIndex++) {
			djVtkPoint tmpPt = this.getPoint(ptIndex);
			if (tmpPt.x > surBound[0][1]) {
				surBound[0][1] = tmpPt.x;
			}
			if (tmpPt.y > surBound[1][1]) {
				surBound[1][1] = tmpPt.y;
			}
			if (tmpPt.z > surBound[2][1]) {
				surBound[2][1] = tmpPt.z;
			}
			if (tmpPt.x < surBound[0][0]) {
				surBound[0][0] = tmpPt.x;
			}
			if (tmpPt.y < surBound[1][0]) {
				surBound[1][0] = tmpPt.y;
			}
			if (tmpPt.z < surBound[2][0]) {
				surBound[2][0] = tmpPt.z;
			}
		}
	}

	public djVtkData getCompactData() {
		djVtkData newData;
		switch (this.nVtkDataType) {
		case djVtkDataDictionary.VTK_DATATYPE_SURFACE:
			this.cell_alias = djVtkDataDictionary.VTK_FIELDNAME_SURFACE_CELL;
			newData = new djVtkSurData();
			break;
		case djVtkDataDictionary.VTK_DATATYPE_FIBERS:
			this.cell_alias = djVtkDataDictionary.VTK_FIELDNAME_FIBER_CELL;
			newData = new djVtkFiberData();
			break;
		default:
			newData = new djVtkData();
			System.out.println("DataType is Unknown!!!");
			break;
		}

		List<djVtkPoint> outPutPoints = new ArrayList<djVtkPoint>();
		List<djVtkCell> outPutCells = new ArrayList<djVtkCell>();

		Set<String> attriSet = this.pointsScalarData.keySet();
		Iterator keyIter = attriSet.iterator();
		while (keyIter.hasNext()) {
			String attriName = String.valueOf(keyIter.next());
			newData.pointsScalarData.put(attriName, new ArrayList<String>());
		}
		Map<Integer, Integer> pointIndexMap = new HashMap<Integer, Integer>();
		int count = 0;
		djVtkPoint thePoint;
		for (int i = 0; i < this.cellsOutput.size(); i++) {
			djVtkCell theCell = this.cellsOutput.get(i);
			djVtkCell newCell = new djVtkCell();
			for (int j = 0; j < theCell.pointsList.size(); j++) {
				if (!pointIndexMap.containsKey(theCell.pointsList.get(j).pointId)) {
					djVtkPoint newPoint = new djVtkPoint();
					newPoint.pointId = count;
					newPoint.x = theCell.pointsList.get(j).x;
					newPoint.y = theCell.pointsList.get(j).y;
					newPoint.z = theCell.pointsList.get(j).z;
					outPutPoints.add(newPoint);
					pointIndexMap.put(theCell.pointsList.get(j).pointId, count++);
					// initial attributes
					attriSet = this.pointsScalarData.keySet();
					keyIter = attriSet.iterator();
					while (keyIter.hasNext()) {
						String attriName = String.valueOf(keyIter.next());
						List<String> updateAtrriList = newData.pointsScalarData.get(attriName);
						updateAtrriList.add( this.pointsScalarData.get(attriName).get( theCell.pointsList.get(j).pointId ) );
						newData.pointsScalarData.remove(attriName);
						newData.pointsScalarData.put(attriName, updateAtrriList);
					}
				}
				newCell.cellId = j;
				newCell.pointsList.add(outPutPoints.get(pointIndexMap.get(theCell.pointsList.get(j).pointId)));
			}
			outPutCells.add(newCell);
		}
		newData.nPointNum = count;
		newData.points = outPutPoints;
		newData.nCellNum = outPutCells.size();
		newData.cells = outPutCells;
		newData.cellsOutput = outPutCells;
		return newData;
	}

	public void writeToVtkFileCompact(String fileName) {
		List<djVtkPoint> outPutPoints = new ArrayList<djVtkPoint>();
		Map<Integer, Integer> pointIndexMap = new HashMap<Integer, Integer>();
		int count = 0;
		djVtkPoint thePoint;
		System.out.println("Begin to write compact vtkFile...");
		for (int i = 0; i < this.cellsOutput.size(); i++) {
			djVtkCell theCell = this.cellsOutput.get(i);
			for (int j = 0; j < theCell.pointsList.size(); j++) {
				thePoint = theCell.pointsList.get(j);
				if (!pointIndexMap.containsKey(thePoint.pointId)) {
					outPutPoints.add(thePoint);
					pointIndexMap.put(thePoint.pointId, count++);
				}
			}
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName);
			fw.write("# vtk DataFile Version 3.0\r\n");
			fw.write("vtk output\r\n");
			fw.write("ASCII\r\n");
			fw.write("DATASET POLYDATA\r\n");
			// print points info
			fw.write("POINTS " + count + " float\r\n");
			for (int i = 0; i < count; i++) {
				fw.write(outPutPoints.get(i).x + " " + outPutPoints.get(i).y + " " + outPutPoints.get(i).z + "\r\n");
			}
			// print cells info
			int pointsNumInOutputCells = 0;
			for (int i = 0; i < this.cellsOutput.size(); i++) {
				pointsNumInOutputCells = pointsNumInOutputCells + this.cellsOutput.get(i).pointsList.size();
			}
			pointsNumInOutputCells = pointsNumInOutputCells + this.cellsOutput.size();

			fw.write(this.cell_alias + " " + this.cellsOutput.size() + " " + pointsNumInOutputCells + " \r\n");
			for (int i = 0; i < this.cellsOutput.size(); i++) {
				fw.write(this.cellsOutput.get(i).pointsList.size() + " ");
				for (int j = 0; j < this.cellsOutput.get(i).pointsList.size(); j++) {
					fw.write(pointIndexMap.get(this.cellsOutput.get(i).pointsList.get(j).pointId) + " ");
				}
				fw.write("\r\n");
			}
			
			// print Point_Data
			if (this.pointsScalarData.size() > 0) {
				fw.write("POINT_DATA " + this.nPointNum + "\r\n");
				Iterator iterPointData = this.pointsScalarData.keySet().iterator();
				while (iterPointData.hasNext()) {
					String tmpAttriName = (String) iterPointData.next();
					fw.write("COLOR_SCALARS " + tmpAttriName + " 3 \r\n");
					for (int i = 0; i < this.pointsScalarData.get(tmpAttriName).size(); i++) {
						fw.write(this.pointsScalarData.get(tmpAttriName).get(i) + "\r\n");
					}
				}
			}
			
			// print Cell_Data
			if (this.cellsScalarData.size() > 0) {
				fw.write("CELL_DATA " + this.nCellNum + "\r\n");
				Iterator iterPointData = this.cellsScalarData.keySet().iterator();
				while (iterPointData.hasNext()) {
					String tmpAttriName = (String) iterPointData.next();
					fw.write("COLOR_SCALARS " + tmpAttriName + " 3 \r\n");
					for (int i = 0; i < this.cellsScalarData.get(tmpAttriName).size(); i++) {
						fw.write(this.cellsScalarData.get(tmpAttriName).get(i) + "\r\n");
					}
				}
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
		System.out.println("That is all!");

	}

	public void writeToVtkFile(String fileName) {
		System.out.println("Begin to write file:" + fileName + "...");
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName);
			fw.write("# vtk DataFile Version 3.0\r\n");
			fw.write("vtk output\r\n");
			fw.write("ASCII\r\n");
			fw.write("DATASET POLYDATA\r\n");
			// print points info
			fw.write("POINTS " + this.nPointNum + " float\r\n");
			for (int i = 0; i < this.nPointNum; i++) {
				fw.write(this.points.get(i).x + " " + this.points.get(i).y + " " + this.points.get(i).z + "\r\n");
			}
			// print cells info
			int pointsNumInOutputCells = 0;
			for (int i = 0; i < this.cellsOutput.size(); i++) {
				pointsNumInOutputCells = pointsNumInOutputCells + this.cellsOutput.get(i).pointsList.size();
			}
			pointsNumInOutputCells = pointsNumInOutputCells + this.cellsOutput.size();

			fw.write(this.cell_alias + " " + this.cellsOutput.size() + " " + pointsNumInOutputCells + " \r\n");
			for (int i = 0; i < this.cellsOutput.size(); i++) {
				fw.write(this.cellsOutput.get(i).pointsList.size() + " ");
				for (int j = 0; j < this.cellsOutput.get(i).pointsList.size(); j++) {
					fw.write(this.cellsOutput.get(i).pointsList.get(j).pointId + " ");
				}
				fw.write("\r\n");
			}

			// print Point_Data
			if (this.pointsScalarData.size() > 0) {
				fw.write("POINT_DATA " + this.nPointNum + "\r\n");
				Iterator iterPointData = this.pointsScalarData.keySet().iterator();
				while (iterPointData.hasNext()) {
					String tmpAttriName = (String) iterPointData.next();
					//for render COLOR_SCALARS
					fw.write("COLOR_SCALARS " + tmpAttriName + " 3 \r\n");
					//end of for render COLOR_SCALARS
//					fw.write("SCALARS " + tmpAttriName + " float 1 \r\n");
//					fw.write("LOOKUP_TABLE default \r\n");
					for (int i = 0; i < this.pointsScalarData.get(tmpAttriName).size(); i++) {
						fw.write(this.pointsScalarData.get(tmpAttriName).get(i) + "\r\n");
					}
				}
			}

			// print Cell_Data
			if (this.cellsScalarData.size() > 0) {
				fw.write("CELL_DATA " + this.cellsOutput.size() + "\r\n");
				Iterator iterCellData = this.cellsScalarData.keySet().iterator();
				while (iterCellData.hasNext()) {
					String tmpAttriName = (String) iterCellData.next();
					//for render COLOR_SCALARS
					fw.write("COLOR_SCALARS " + tmpAttriName + " 3 \r\n");
					//end of for render COLOR_SCALARS
//					fw.write("SCALARS " + tmpAttriName + " float 1 \r\n");
//					fw.write("LOOKUP_TABLE default \r\n");
					for (int i = 0; i < this.cellsScalarData.get(tmpAttriName).size(); i++) {
						fw.write(this.cellsScalarData.get(tmpAttriName).get(i) + "\r\n");
					}
				}
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
		System.out.println("That is all!");
	}

	public void printInfo() {
		System.out.println("SurfaceData info:#########################");
		System.out.println("This surface has " + this.nPointNum + " pints.");
		System.out.println("This surface has " + this.nCellNum + " cells.");
		System.out.println("Points info: real point number is " + this.points.size() + " -------------------------------");
		// System.out.println(this.points.toString());
		// System.out.println("Points info end.-------------------------------");
		System.out.println("Cells info: real cell number is " + this.cells.size() + " -------------------------------");
		// System.out.println(this.cells.toString());
		// System.out.println("Cells info end.-------------------------------");
		System.out.println("Point Scalar info:-------------------------------");
		System.out.println(this.pointsScalarData.toString());
		System.out.println("Point Scalar end.-------------------------------");
		System.out.println("Cell Scalar info:-------------------------------");
		System.out.println(this.cellsScalarData.toString());
		System.out.println("Cell Scalar end.-------------------------------");
		System.out.println("SurfaceData info End.#########################");
	}

	public djVtkPoint getPoint(int PointID) {
		return this.points.get(PointID);
	}

	public djVtkCell getcell(int CellID) {
		return this.cells.get(CellID);
	}

	public List getPointsOfCell(int CellID) {
		return this.cells.get(CellID).pointsList;
	}

	public List getAllPoints() {
		return this.points;
	}

	public List getAllCells() {
		return this.cells;
	}
	
	public int findCloestPt(djVtkPoint tmpPt)
	{
		int ptIndex=-1;
		float minDis = 1000.0f;
		for (int i = 0; i < this.nPointNum; i++) {
			float tmpDis = djVtkUtil.calDistanceOfPoints(tmpPt, this.getPoint(i));
			if (tmpDis < minDis) {
				minDis = tmpDis;
				ptIndex = i;
			}
		}
		return ptIndex;
	}
}
