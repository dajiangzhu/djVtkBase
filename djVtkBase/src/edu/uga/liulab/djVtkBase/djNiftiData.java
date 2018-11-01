/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

import com.xinapse.multisliceimage.Analyze.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author dj
 */
public class djNiftiData {
	// test add

	public NIFTIImage rawNiftiData = null;
	public int nDims = -1;
	public int xSize = -1;
	public int ySize = -1;
	public int zSize = -1;
	public int tSize = 1;
	public float[] Spacing;
	public float[] offset;
	public float[][] tranMatrix;

	public djNiftiData(String niftiFileName) {
		try {
			this.rawNiftiData = new NIFTIImage(niftiFileName, "rw");
			this.nDims = this.rawNiftiData.getNDim();
			this.xSize = this.rawNiftiData.getDims()[nDims - 1];
			this.ySize = this.rawNiftiData.getDims()[nDims - 2];
			this.zSize = this.rawNiftiData.getDims()[nDims - 3];
			if (nDims == 4) {
				this.tSize = this.rawNiftiData.getDims()[nDims - 4];
			}
			Spacing = new float[3];
			Spacing[0] = this.rawNiftiData.getPixelXSize();
			Spacing[1] = this.rawNiftiData.getPixelYSize();
			Spacing[2] = this.rawNiftiData.getPixelZSize();
			offset = this.rawNiftiData.getImagePositionPatient();
			// tranMatrix = this.rawNiftiData.getImageOrientationPatient();
		} catch (Exception ex) {
			// Logger.getLogger(djNiftiData.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public djNiftiData(String niftiFileName, String mhdFileName) {
		try {
			System.out.println("load nifti file:" + niftiFileName);
			System.out.println("load mhd file:" + mhdFileName);
			this.rawNiftiData = new NIFTIImage(niftiFileName, "rw");
			this.nDims = this.rawNiftiData.getNDim();
			this.xSize = this.rawNiftiData.getDims()[nDims - 1];
			this.ySize = this.rawNiftiData.getDims()[nDims - 2];
			this.zSize = this.rawNiftiData.getDims()[nDims - 3];
			if (nDims == 4) {
				this.tSize = this.rawNiftiData.getDims()[nDims - 4];
			}

			Spacing = new float[3];
			offset = new float[3];

			FileInputStream fstream = new FileInputStream(mhdFileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String[] tmpStringArray;
			while ((strLine = br.readLine()) != null) {
				if (strLine.startsWith("Offset")) {
					tmpStringArray = strLine.split(" ");
					offset[0] = Float.valueOf(tmpStringArray[tmpStringArray.length - 3]);
					offset[1] = Float.valueOf(tmpStringArray[tmpStringArray.length - 2]);
					offset[2] = Float.valueOf(tmpStringArray[tmpStringArray.length - 1]);
				}
				if (strLine.startsWith("ElementSpacing")) {
					tmpStringArray = strLine.split(" ");
					Spacing[0] = Float.valueOf(tmpStringArray[tmpStringArray.length - 3]);
					Spacing[1] = Float.valueOf(tmpStringArray[tmpStringArray.length - 2]);
					Spacing[2] = Float.valueOf(tmpStringArray[tmpStringArray.length - 1]);
				}
			}
			br.close();
			in.close();
			fstream.close();
		} catch (Exception ex) {
			// Logger.getLogger(djNiftiData.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public float[] convertFromVolumeToPhysical(int[] volumeCoord) {
		float[] physicalCoord = new float[3];
		for (int i = 0; i < 3; i++) {
			physicalCoord[i] = volumeCoord[i] * this.Spacing[i] + this.offset[i];
		}
		return physicalCoord;
	}

	public int[] convertFromPhysicalToVolume(float[] physicalCoord) {
		int[] volumeCoord = new int[3];
		for (int i = 0; i < 3; i++) {
			volumeCoord[i] = (int) ((physicalCoord[i] - this.offset[i]) / this.Spacing[i]);
		}
		return volumeCoord;
	}

	public float getValueBasedOnVolumeCoordinate(int x, int y, int z, int t) {
		float result = 0.0f;
		try {
			int[] tmpCoord;
			int tmpCount = 0;
			if (this.nDims == 4) {
				tmpCoord = new int[4];
				tmpCoord[tmpCount++] = t;
			} else {
				tmpCoord = new int[3];
			}
			tmpCoord[tmpCount++] = z;
			tmpCoord[tmpCount++] = y;
			tmpCoord[tmpCount++] = x;
			result = Float.valueOf(String.valueOf(this.rawNiftiData.getPix(tmpCoord)));
		} catch (Exception ex) {
			// Logger.getLogger(djNiftiData.class.getName()).log(Level.SEVERE, null, ex);
		}
		return result;
	}

	public float getValueBasedOnPhysicalCoordinate(float x, float y, float z, int t) {
		int[] volumeCoord;
		float[] physicalCoord = new float[3];
		physicalCoord[0] = x;
		physicalCoord[1] = y;
		physicalCoord[2] = z;
		volumeCoord = this.convertFromPhysicalToVolume(physicalCoord);
		for (int i = 0; i < 3; i++)
			if (volumeCoord[i] < 0)
				volumeCoord[i] = 0;
		if (volumeCoord[0] >= this.xSize)
			volumeCoord[0] = this.xSize - 1;
		if (volumeCoord[1] >= this.ySize)
			volumeCoord[1] = this.ySize - 1;
		if (volumeCoord[2] >= this.zSize)
			volumeCoord[2] = this.zSize - 1;
		return this.getValueBasedOnVolumeCoordinate(volumeCoord[0], volumeCoord[1], volumeCoord[2], t);
	}

	private boolean hasSig(int x, int y, int z) {
		int count = 0;
		for (int t = 0; t < this.tSize; t++)
			if (Math.abs(this.getValueBasedOnVolumeCoordinate(x, y, z, t)) < 0.01)
				count++;
		if (count > (this.tSize / 2))
			return false;
		else
			return true;
	}

	public float getValueBasedOnPhysicalCoordinateRange(float x, float y, float z, int t) {
		int[] volumeCoord;
		float[] physicalCoord = new float[3];
		physicalCoord[0] = x;
		physicalCoord[1] = y;
		physicalCoord[2] = z;
		volumeCoord = this.convertFromPhysicalToVolume(physicalCoord);
		float result = 0.0f;
		int count = 0;
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				for (int k = -1; k < 2; k++) {
					int currentX = volumeCoord[0] + i;
					int currentY = volumeCoord[1] + j;
					int currentZ = volumeCoord[2] + k;
					if (currentX > 0 && currentX < xSize && currentY > 0 && currentY < ySize && currentZ > 0
							&& currentZ < zSize && this.hasSig(currentX, currentY, currentZ)) {
						result = result + this.getValueBasedOnVolumeCoordinate(currentX, currentY, currentZ, t);
						count++;
					}
				}
		return result / count;
	}
	
//	public float[] getSigBasedOnPhysicalCoordinateRange(float x, float y, float z) {
//		int[] volumeCoord;
//		float[] physicalCoord = new float[3];
//		physicalCoord[0] = x;
//		physicalCoord[1] = y;
//		physicalCoord[2] = z;
//		volumeCoord = this.convertFromPhysicalToVolume(physicalCoord);
//		float result = 0.0f;
//		int count = 0;
//		for (int i = -1; i < 2; i++)
//			for (int j = -1; j < 2; j++)
//				for (int k = -1; k < 2; k++) {
//					int currentX = volumeCoord[0] + i;
//					int currentY = volumeCoord[1] + j;
//					int currentZ = volumeCoord[2] + k;
//					if (currentX > 0 && currentX < xSize && currentY > 0 && currentY < ySize && currentZ > 0
//							&& currentZ < zSize && this.hasSig(currentX, currentY, currentZ)) {
//						result = result + this.getValueBasedOnVolumeCoordinate(currentX, currentY, currentZ, t);
//						count++;
//					}
//				}
//		return result / count;
//	}

}
