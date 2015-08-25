/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Math;

/**
 * For dealing with combination of surface and fibers
 * 
 * @author dj
 */
public class djVtkHybridData {

	private djVtkSurData surfaceData;
	private djVtkFiberData fiberData;
	private djNiftiData fmriData;
	public Map<Integer, Map<Integer, Map<Integer, djVtkBox>>> mapBox = new HashMap<Integer, Map<Integer, Map<Integer, djVtkBox>>>();

	public djVtkSurData getSurfaceData() {
		return surfaceData;
	}

	public djVtkFiberData getFiberData() {
		return fiberData;
	}

	public void setSurfaceData(djVtkSurData surfaceData) {
		this.surfaceData = surfaceData;
	}

	public void setFiberData(djVtkFiberData fiberData) {
		this.fiberData = fiberData;
	}

	public djVtkHybridData(djVtkSurData surface, djVtkFiberData fiber) {
		this.surfaceData = surface;
		this.fiberData = fiber;
	}

	public djVtkHybridData(djVtkFiberData fiber, djNiftiData fmriData) {
		this.fiberData = fiber;
		this.fmriData = fmriData;
	}

	// modify if you need different limit
	public void mapFiberToBox() {
		System.out.println("Begin to map the fiber data to the boxes...");
		for (int i = 0; i < this.fiberData.cells.size(); i++) {
			if (this.fiberData.cells.get(i).pointsList.size() > 20) {
				this.mapToBox(this.fiberData.cells.get(i).pointsList, 0,
						this.fiberData.cells.get(i).pointsList.size() / 2, 1,// this.mapToBox(this.fiberData.cells.get(i).pointsList,
																				// 0,
																				// 15,
																				// 1,
						djVtkDataDictionary.VTK_DATATYPE_FIBERS);
				this.mapToBox(this.fiberData.cells.get(i).pointsList,
						this.fiberData.cells.get(i).pointsList.size() - 1,
						this.fiberData.cells.get(i).pointsList.size() / 2, -1, djVtkDataDictionary.VTK_DATATYPE_FIBERS);
			}
		}
	}

	public void mapAllFiberToBox() {
		System.out.println("Begin to map all fiber data to the boxes...");
		for (int i = 0; i < this.fiberData.cells.size(); i++) {
			this.mapToBox(this.fiberData.cells.get(i).pointsList, 0, this.fiberData.cells.get(i).pointsList.size(), 1,
					djVtkDataDictionary.VTK_DATATYPE_FIBERS);
		}
	}

	private void mapToBox(List<djVtkPoint> pointList, int startIndex, int limit, int increment, int dataType) {
		int pointID;
		int bX, bX1, bX2;
		int bY, bY1, bY2;
		int bZ, bZ1, bZ2;
		int count = 0;
		boolean fiberMapSuccess = false;

		for (int i = startIndex; count < limit; i = i + increment) {
			count++;
			pointID = pointList.get(i).pointId;

			Set<Integer> bXSet = new HashSet<Integer>();
			Set<Integer> bYSet = new HashSet<Integer>();
			Set<Integer> bZSet = new HashSet<Integer>();

			bXSet.add((int) (pointList.get(i).x - 0.5));
			bXSet.add((int) (pointList.get(i).x + 0.5));
			bYSet.add((int) (pointList.get(i).y - 0.5));
			bYSet.add((int) (pointList.get(i).y + 0.5));
			bZSet.add((int) (pointList.get(i).z - 0.5));
			bZSet.add((int) (pointList.get(i).z + 0.5));

			Object[] bXArray = bXSet.toArray();
			Object[] bYArray = bYSet.toArray();
			Object[] bZArray = bZSet.toArray();

			for (int x = 0; x < bXSet.size(); x++) {
				bX = (Integer) bXArray[x];
				for (int y = 0; y < bYSet.size(); y++) {
					bY = (Integer) bYArray[y];
					for (int z = 0; z < bZSet.size(); z++) {
						bZ = (Integer) bZArray[z];
						if (mapBox.get(bX) == null) {
							if (dataType == djVtkDataDictionary.VTK_DATATYPE_SURFACE) {
								Map<Integer, Map<Integer, djVtkBox>> m_1 = new HashMap<Integer, Map<Integer, djVtkBox>>();
								Map<Integer, djVtkBox> m_2 = new HashMap<Integer, djVtkBox>();
								djVtkBox newBox = new djVtkBox();
								newBox.surPtList.add(pointID);
								m_2.put(bZ, newBox);
								m_1.put(bY, m_2);
								mapBox.put(bX, m_1);
							} else if (dataType == djVtkDataDictionary.VTK_DATATYPE_FIBERS) {
								continue;
							}
						} else if (mapBox.get(bX).get(bY) == null) {
							if (dataType == djVtkDataDictionary.VTK_DATATYPE_SURFACE) {
								Map<Integer, djVtkBox> m_2 = new HashMap<Integer, djVtkBox>();
								djVtkBox newBox = new djVtkBox();
								newBox.surPtList.add(pointID);
								m_2.put(bZ, newBox);
								mapBox.get(bX).put(bY, m_2);
							} else if (dataType == djVtkDataDictionary.VTK_DATATYPE_FIBERS) {
								continue;
							}
						} else if (mapBox.get(bX).get(bY).get(bZ) == null) {
							if (dataType == djVtkDataDictionary.VTK_DATATYPE_SURFACE) {
								djVtkBox newBox = new djVtkBox();
								newBox.surPtList.add(pointID);
								mapBox.get(bX).get(bY).put(bZ, newBox);
							} else if (dataType == djVtkDataDictionary.VTK_DATATYPE_FIBERS) {
								continue;
							}
						} else {
							if (dataType == djVtkDataDictionary.VTK_DATATYPE_SURFACE) {
								mapBox.get(bX).get(bY).get(bZ).surPtList.add(pointID);
							} else if (dataType == djVtkDataDictionary.VTK_DATATYPE_FIBERS) {
								for (int k = 0; k < pointList.get(i).cellsList.size(); k++) {
									mapBox.get(bX).get(bY).get(bZ).fiberList
											.add(pointList.get(i).cellsList.get(k).cellId);// add
																							// this
									// fiber to
									// the box
									fiberMapSuccess = true;
									if (increment < 0) {
										pointList.get(i).cellsList.get(k).connSurPnts.get(0).addAll(
												mapBox.get(bX).get(bY).get(bZ).surPtList);
									} else {
										pointList.get(i).cellsList.get(k).connSurPnts.get(1).addAll(
												mapBox.get(bX).get(bY).get(bZ).surPtList);
									}
								}
								break;
							}
						} // else
					}

				}
			}

		} // for

		// if this fiber maps fail, try to enlarge the search space:
		if (fiberMapSuccess == false && dataType == djVtkDataDictionary.VTK_DATATYPE_FIBERS) {
			float distance = 0.5f;
			count = 0;
			for (int i = startIndex; count < limit; i = i + increment) {
				count++;
				pointID = pointList.get(i).pointId;
				bX = java.lang.Math.round(pointList.get(i).x);
				bY = java.lang.Math.round(pointList.get(i).y);
				bZ = java.lang.Math.round(pointList.get(i).z);
				int sx = bX - 1;
				int sy = bY - 1;
				int sz = bZ - 1;
				for (int l = sx; l < sx + 3; l++) {
					for (int m = sy; m < sy + 3; m++) {
						for (int n = sz; n < sz + 3; n++) {
							if (mapBox.get(l) != null) {
								if (mapBox.get(l).get(m) != null) {
									if (mapBox.get(l).get(m).get(n) != null) {
										for (int k = 0; k < pointList.get(i).cellsList.size(); k++) {
											mapBox.get(l).get(m).get(n).fiberList
													.add(pointList.get(i).cellsList.get(k).cellId);// add
																									// this
																									// fiber
																									// to
																									// the
																									// box
											fiberMapSuccess = true;
											if (increment < 0) {
												pointList.get(i).cellsList.get(k).connSurPnts.get(0).addAll(
														mapBox.get(l).get(m).get(n).surPtList);
											} else {
												pointList.get(i).cellsList.get(k).connSurPnts.get(1).addAll(
														mapBox.get(l).get(m).get(n).surPtList);
											}
										}// for k
										break;
									}
								}
							}
						}// for n
					}// for m
				}// for l

			}// for i

		}// if (fiberMapSuccess == false && dataType ==
			// djVtkDataDictionary.VTK_DATATYPE_FIBERS)

	}

	public void mapSurfaceToBox() {
		System.out.println("Begin to map the surface data to the boxes...");
		this.mapToBox(this.surfaceData.points, 0, this.surfaceData.points.size(), 1,
				djVtkDataDictionary.VTK_DATATYPE_SURFACE);
	}

	public djVtkFiberData getFibersConnectToSurface() {
		System.out.println("Begin to get fibers which connect to the surface...");
		this.mapSurfaceToBox();
		this.mapFiberToBox();

		System.out.println("Begin to build the relation of surface data and fiber data...");
		int bX;
		int bY;
		int bZ;
		Set<Integer> fibersSet = new HashSet<Integer>();
		Set<Integer> tmpSet = new HashSet<Integer>();
		for (int i = 0; i < this.surfaceData.points.size(); i++) {
			// System.out.println("now i is :" + i);

			Set<Integer> bXSet = new HashSet<Integer>();
			Set<Integer> bYSet = new HashSet<Integer>();
			Set<Integer> bZSet = new HashSet<Integer>();

			bXSet.add((int) (this.surfaceData.points.get(i).x - 0.5));
			bXSet.add((int) (this.surfaceData.points.get(i).x + 0.5));
			bYSet.add((int) (this.surfaceData.points.get(i).y - 0.5));
			bYSet.add((int) (this.surfaceData.points.get(i).y + 0.5));
			bZSet.add((int) (this.surfaceData.points.get(i).z - 0.5));
			bZSet.add((int) (this.surfaceData.points.get(i).z + 0.5));

			Object[] bXArray = bXSet.toArray();
			Object[] bYArray = bYSet.toArray();
			Object[] bZArray = bZSet.toArray();

			for (int x = 0; x < bXSet.size(); x++) {
				bX = (Integer) bXArray[x];
				for (int y = 0; y < bYSet.size(); y++) {
					bY = (Integer) bYArray[y];
					for (int z = 0; z < bZSet.size(); z++) {
						bZ = (Integer) bZArray[z];
						tmpSet = this.mapBox.get(bX).get(bY).get(bZ).fiberList;
						fibersSet.addAll(tmpSet);
					} // for
				} // for
			} // for
		} // while
		Iterator fibersSetIter = fibersSet.iterator();
		while (fibersSetIter.hasNext()) {
			this.fiberData.cellsOutput.add(this.fiberData.getcell((Integer) fibersSetIter.next()));
		}
		return this.fiberData;
	}

	public djVtkFiberData getFibersConnectToPointsSet(Set<djVtkPoint> pointsSet) {
		// System.out.println("Begin to get fibers which connect to the points set...");
		// System.out.println("the points set size is :" + pointsSet.size());
		// this.mapSurfaceToBox();
		// this.mapFiberToBox();

		int count = 0;
		int bX;
		int bY;
		int bZ;
		Set<Integer> fibersSet = new HashSet<Integer>();
		Set<Integer> tmpSet = new HashSet<Integer>();
		this.fiberData.cellsOutput.clear();

		Iterator itPointsSet = pointsSet.iterator();
		while (itPointsSet.hasNext()) {
			djVtkPoint tmpPoint = (djVtkPoint) itPointsSet.next();

			// System.out.println("now getting the fiber of the " + count++ +
			// "th point...");

			Set<Integer> bXSet = new HashSet<Integer>();
			Set<Integer> bYSet = new HashSet<Integer>();
			Set<Integer> bZSet = new HashSet<Integer>();

			bXSet.add((int) (tmpPoint.x - 0.5));
			bXSet.add((int) (tmpPoint.x + 0.5));
			bYSet.add((int) (tmpPoint.y - 0.5));
			bYSet.add((int) (tmpPoint.y + 0.5));
			bZSet.add((int) (tmpPoint.z - 0.5));
			bZSet.add((int) (tmpPoint.z + 0.5));

			Object[] bXArray = bXSet.toArray();
			Object[] bYArray = bYSet.toArray();
			Object[] bZArray = bZSet.toArray();

			for (int x = 0; x < bXSet.size(); x++) {
				bX = (Integer) bXArray[x];
				for (int y = 0; y < bYSet.size(); y++) {
					bY = (Integer) bYArray[y];
					for (int z = 0; z < bZSet.size(); z++) {
						bZ = (Integer) bZArray[z];
						tmpSet = this.mapBox.get(bX).get(bY).get(bZ).fiberList;
						fibersSet.addAll(tmpSet);
					} // for
				} // for
			} // for
		} // while
		Iterator fibersSetIter = fibersSet.iterator();
		while (fibersSetIter.hasNext()) {
			this.fiberData.cellsOutput.add(this.fiberData.getcell((Integer) fibersSetIter.next()));
		}
		return this.fiberData;
	}

	public djVtkAttriDistribution getAttriDistribution(String attriName, int ringNum) {

		System.out.println("Begin to getAttriDistribution...");
		int pntNum = this.surfaceData.nPointNum;
		int cellNum = this.fiberData.cells.size();
		djVtkAttriDistribution attriDistri = new djVtkAttriDistribution(pntNum);

		System.out.println("Initial attriDistri(djVtkAttriDistribution) ...");
		int count = 0;
		for (int i = 0; i < cellNum; i++) {
			if (attriDistri.attriIndexMap.get(this.fiberData.cellsScalarData.get(attriName).get(i)) == null) {
				attriDistri.attriIndexMap.put(this.fiberData.cellsScalarData.get(attriName).get(i), count++);
			}
		}
		attriDistri.Initial();

		System.out.println("Begin to map to box...");
		this.mapSurfaceToBox();
		this.mapFiberToBox();

		System.out.println("Begin to calculate the distribution...");
		int bX;
		int bY;
		int bZ;
		Set<djVtkPoint> neighbourPoints;
		Set<Integer> tmpFibersSet = new HashSet<Integer>();
		for (int i = 0; i < pntNum; i++) {
			System.out.println("Dealing with the " + i + "th point...");
			neighbourPoints = this.surfaceData.getNeighbourPoints(i, ringNum);
			Iterator neighourePointsIter = neighbourPoints.iterator();

			while (neighourePointsIter.hasNext()) {
				djVtkPoint tmpPoint = (djVtkPoint) neighourePointsIter.next();
				bX = java.lang.Math.round(tmpPoint.x);
				bY = java.lang.Math.round(tmpPoint.y);
				bZ = java.lang.Math.round(tmpPoint.z);
				tmpFibersSet = this.mapBox.get(bX).get(bY).get(bZ).fiberList;
				Iterator tmpFibersSetIter = tmpFibersSet.iterator();
				while (tmpFibersSetIter.hasNext()) {
					int fiberID = (Integer) tmpFibersSetIter.next();
					String fiberAttri = this.fiberData.cellsScalarData.get(attriName).get(fiberID);
					int fiberIndexInTheMap = attriDistri.attriIndexMap.get(fiberAttri);
					System.out.println("Assign the " + fiberID + "th fiber which connect to " + fiberAttri
							+ " with index is: " + fiberIndexInTheMap);
					attriDistri.attriDistribution[i][fiberIndexInTheMap] += 1;
				}
			}
		}
		System.out.println("Begin to normalize the distribution...");
		attriDistri.normlizeDistribution();
		return attriDistri;
	}

	public void calAttriDistribution(String attriName, int ringNum, String outPutFileName) {
		System.out.println("Begin to calAttriDistribution...");
		this.mapSurfaceToBox();
		this.mapFiberToBox();
		int surPtNum = this.surfaceData.points.size();
		int[] newLabelForPoints = new int[surPtNum];
		int[][] attriDistributionMap = new int[surPtNum][4];// only consider 4
		// types:1,2,4,5
		int[][] attriDistributionPattern = new int[surPtNum][3];
		float[] simiInfoForPoints = new float[surPtNum];

		System.out.println("Begin to analize the data...");
		int bX;
		int bY;
		int bZ;
		Set<djVtkPoint> neighbourPoints;
		Set<Integer> tmpFibersSet = new HashSet<Integer>();

		for (int i = 0; i < surPtNum; i++) {
			System.out.println("Dealing with the " + i + "th point...");
			int[] tmpAttriDistributionMap = new int[] { 0, 0, 0, 0, 0, 0, 0 };
			neighbourPoints = this.surfaceData.getNeighbourPoints(i, ringNum);
			Iterator neighourePointsIter = neighbourPoints.iterator();

			while (neighourePointsIter.hasNext()) {
				djVtkPoint tmpPoint = (djVtkPoint) neighourePointsIter.next();
				bX = java.lang.Math.round(tmpPoint.x);
				bY = java.lang.Math.round(tmpPoint.y);
				bZ = java.lang.Math.round(tmpPoint.z);
				tmpFibersSet = this.mapBox.get(bX).get(bY).get(bZ).fiberList;
				Iterator tmpFibersSetIter = tmpFibersSet.iterator();
				while (tmpFibersSetIter.hasNext()) {
					tmpAttriDistributionMap[Integer.valueOf(this.fiberData.cellsScalarData.get(attriName).get(
							(Integer) tmpFibersSetIter.next()))]++;
				}
			}
			attriDistributionMap[i][0] = tmpAttriDistributionMap[1];
			attriDistributionMap[i][1] = tmpAttriDistributionMap[2];
			attriDistributionMap[i][2] = tmpAttriDistributionMap[4];
			attriDistributionMap[i][3] = tmpAttriDistributionMap[5];

			for (int j = 1; j < 4; j++) {
				if ((attriDistributionMap[i][j] - attriDistributionMap[i][j - 1]) > 0) {
					attriDistributionPattern[i][j - 1] = 2;
				} else if ((attriDistributionMap[i][j] - attriDistributionMap[i][j - 1]) < 0) {
					attriDistributionPattern[i][j - 1] = 0;
				} else {
					attriDistributionPattern[i][j - 1] = 1;
				}
			}
			newLabelForPoints[i] = attriDistributionPattern[i][2] + (attriDistributionPattern[i][2] * 3)
					+ (attriDistributionPattern[i][2] * 3 * 3);
		}

		System.out.println("Begin to calculate similarity degree for each point...");
		ringNum = 3;
		float tmpCount = 0.0f;
		for (int i = 0; i < surPtNum; i++) {
			System.out.println("Dealing with the " + i + "th point...");
			neighbourPoints = this.surfaceData.getNeighbourPoints(i, ringNum);
			Iterator neighourePointsIter = neighbourPoints.iterator();

			tmpCount = 0.0f;
			while (neighourePointsIter.hasNext()) {
				djVtkPoint tmpPoint = (djVtkPoint) neighourePointsIter.next();
				for (int j = 0; j < 3; j++) {
					tmpCount += java.lang.Math.abs(attriDistributionPattern[i][j]
							- attriDistributionPattern[tmpPoint.pointId][j]);
				}
			}
			simiInfoForPoints[i] = tmpCount / neighbourPoints.size();
		}

		// output the result anyway
		System.out.println("Begin to write file:" + outPutFileName + "...");
		FileWriter fw = null;
		try {
			fw = new FileWriter(outPutFileName);
			for (int i = 0; i < surPtNum; i++) {
				fw.write(simiInfoForPoints[i] + "\r\n");
				// for (int j = 0; j < 3; j++) {
				// fw.write(attriDistributionPattern[i][j] + " ");
				// }
				// fw.write("\r\n");
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

	public djVtkFiberData getFiberFromVlumeBox(int x, int y, int z, int boxRadius) {
		int[] volLimitMin = new int[3];
		int[] volLimitMax = new int[3];
		float[] phyLimitMin;
		float[] phyLimitMax;

		volLimitMin[0] = x - 1;
		volLimitMin[1] = y - 1;
		volLimitMin[2] = z - 1;
		volLimitMax[0] = x + 1;
		volLimitMax[1] = y + 1;
		volLimitMax[2] = z + 1;

		phyLimitMin = this.fmriData.convertFromVolumeToPhysical(volLimitMin);
		phyLimitMax = this.fmriData.convertFromVolumeToPhysical(volLimitMax);

		this.fiberData.cellsOutput.clear();
		for (int i = 0; i < this.fiberData.cells.size(); i++) {
			for (int j = 0; j < this.fiberData.getcell(i).pointsList.size(); j++) {
				if (this.fiberData.getcell(i).pointsList.get(j).x <= phyLimitMax[0]
						&& this.fiberData.getcell(i).pointsList.get(j).x >= phyLimitMin[0]) {
					if (this.fiberData.getcell(i).pointsList.get(j).y <= phyLimitMax[1]
							&& this.fiberData.getcell(i).pointsList.get(j).y >= phyLimitMin[1]) {
						if (this.fiberData.getcell(i).pointsList.get(j).z <= phyLimitMax[2]
								&& this.fiberData.getcell(i).pointsList.get(j).z >= phyLimitMin[2]) {
							this.fiberData.cellsOutput.add(this.fiberData.getcell(i));
							continue;
						}
					}
				}
			}
		}
		return this.fiberData;
	}

	public void generateBachFiberFiles(int ringNum, int fiberNumThresholdMin, int fiberNumThresholdMax,
			String outPutFileNamePrefix) {
		this.mapSurfaceToBox();
		this.mapFiberToBox();
		int surPtNum = this.surfaceData.points.size();
		int esitmateMaxFiberNum = 1000;
		int[] fiberDenDis = new int[esitmateMaxFiberNum];
		for (int i = 0; i < esitmateMaxFiberNum; i++) {
			fiberDenDis[i] = 0;
		}

		System.out.println("Begin to analize the data...");
		int bX;
		int bY;
		int bZ;
		Set<djVtkPoint> neighbourPoints;
		Set<Integer> fibersOfCurrentPoint = new HashSet<Integer>();
		Set<Integer> tmpFibersSet = new HashSet<Integer>();

		System.out.println("Begin to calulate the cell boundary...");
		for (int i = 0; i < this.fiberData.nCellNum; i++) {
			this.fiberData.getcell(i).calBoundOfCell();
		}

		System.out.println("Begin to iterate the surface...");
		// ///////////////////////////////need modify
		for (int i = 0; i < surPtNum; i++) {// !!!!!!!!!!!!!!!!!!!need modify
			if (i % 1000 == 0) {
				System.out.println("Dealing with the " + i + "th point...");
			}
			fibersOfCurrentPoint.clear();

			neighbourPoints = this.surfaceData.getNeighbourPoints(i, ringNum);
			Iterator neighourePointsIter = neighbourPoints.iterator();

			while (neighourePointsIter.hasNext()) {
				djVtkPoint tmpPoint = (djVtkPoint) neighourePointsIter.next();
				bX = java.lang.Math.round(tmpPoint.x);
				bY = java.lang.Math.round(tmpPoint.y);
				bZ = java.lang.Math.round(tmpPoint.z);
				tmpFibersSet = this.mapBox.get(bX).get(bY).get(bZ).fiberList;
				fibersOfCurrentPoint.addAll(tmpFibersSet);
			}

			fiberDenDis[fibersOfCurrentPoint.size() / 10]++;

			if (fibersOfCurrentPoint.size() >= fiberNumThresholdMin
					&& fibersOfCurrentPoint.size() <= fiberNumThresholdMax) {
				String tmpPrefix = fibersOfCurrentPoint.size() + outPutFileNamePrefix;
				this.fiberData.cellsOutput.clear();
				ArrayList<djVtkCell> fibersChoosen = new ArrayList<djVtkCell>();
				Iterator fibersIter = fibersOfCurrentPoint.iterator();
				while (fibersIter.hasNext()) {
					fibersChoosen.add(this.fiberData.getcell((Integer) (fibersIter.next())));
				}
				this.fiberData.cellsOutput.addAll(fibersChoosen);
				System.out.println("Begin to write file of point:" + i);
				this.fiberData.writeToVtkFile(tmpPrefix + "_" + i + "_fibers.vtk");
			}
		}

		// output the result anyway
		System.out.println("Begin to write fiber density distribution...");
		FileWriter fw = null;
		try {
			fw = new FileWriter("fiber_density_distribution.txt");
			for (int i = 0; i < esitmateMaxFiberNum; i++) {
				fw.write(fiberDenDis[i] + "\r\n");
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

	public void calFiberBundleFeature(int ringNum, int fiberNumThreshold, String outPutFileName) {
		System.out.println("Begin to calFiberBundleFeature...");
		this.mapSurfaceToBox();
		this.mapFiberToBox();
		int surPtNum = this.surfaceData.points.size();
		float[][] fiberBundleLamda = new float[surPtNum][3];

		System.out.println("Begin to analize the data...");
		int bX;
		int bY;
		int bZ;
		Set<djVtkPoint> neighbourPoints;
		Set<Integer> fibersOfCurrentPoint = new HashSet<Integer>();
		Set<Integer> tmpFibersSet = new HashSet<Integer>();

		System.out.println("Begin to calulate the cell boundary...");
		for (int i = 0; i < this.fiberData.nCellNum; i++) {
			this.fiberData.getcell(i).calBoundOfCell();
		}

		System.out.println("Begin to iterate the surface...");
		for (int i = 0; i < surPtNum; i++) {
			System.out.println("Dealing with the " + i + "th point...");
			fibersOfCurrentPoint.clear();

			neighbourPoints = this.surfaceData.getNeighbourPoints(i, ringNum);
			Iterator neighourePointsIter = neighbourPoints.iterator();

			while (neighourePointsIter.hasNext()) {
				djVtkPoint tmpPoint = (djVtkPoint) neighourePointsIter.next();
				bX = java.lang.Math.round(tmpPoint.x);
				bY = java.lang.Math.round(tmpPoint.y);
				bZ = java.lang.Math.round(tmpPoint.z);
				tmpFibersSet = this.mapBox.get(bX).get(bY).get(bZ).fiberList;
				fibersOfCurrentPoint.addAll(tmpFibersSet);
			}

			if (fibersOfCurrentPoint.size() >= fiberNumThreshold) {
				Iterator fibersIter = fibersOfCurrentPoint.iterator();
				float xTmp = 0.0f;
				float yTmp = 0.0f;
				float zTmp = 0.0f;
				while (fibersIter.hasNext()) {
					djVtkCell tmpFiberCell = this.fiberData.getcell((Integer) (fibersIter.next()));
					xTmp += java.lang.Math.abs(tmpFiberCell.xBound_max - tmpFiberCell.xBound_min);
					yTmp += java.lang.Math.abs(tmpFiberCell.yBound_max - tmpFiberCell.yBound_min);
					zTmp += java.lang.Math.abs(tmpFiberCell.zBound_max - tmpFiberCell.zBound_min);
				}
				ArrayList<Float> tmpBeforeNorm = new ArrayList<Float>();
				tmpBeforeNorm.add(xTmp / fibersOfCurrentPoint.size());
				tmpBeforeNorm.add(yTmp / fibersOfCurrentPoint.size());
				tmpBeforeNorm.add(zTmp / fibersOfCurrentPoint.size());
				ArrayList<Float> tmpAfterNorm = djVtkUtil.Normalize(tmpBeforeNorm);
				fiberBundleLamda[i][0] = tmpAfterNorm.get(0);
				fiberBundleLamda[i][1] = tmpAfterNorm.get(1);
				fiberBundleLamda[i][2] = tmpAfterNorm.get(2);

			} else {
				fiberBundleLamda[i][0] = 0.0f;
				fiberBundleLamda[i][1] = 0.0f;
				fiberBundleLamda[i][2] = 0.0f;

			}

		}

		// output the result anyway
		System.out.println("Begin to write file:" + outPutFileName + "...");
		FileWriter fw = null;
		try {
			fw = new FileWriter(outPutFileName);
			for (int i = 0; i < surPtNum; i++) {
				// fw.write(simiInfoForPoints[i] + "\r\n");
				for (int j = 0; j < 3; j++) {
					fw.write(fiberBundleLamda[i][j] + " ");
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
	}
}
