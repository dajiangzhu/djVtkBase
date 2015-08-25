/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.liulab.djVtkBase;

/**
 *
 * @author dj
 */
public class djVtkCube {
    public int ptNum;
    public float[][] points;
    public float[][] cubePoints;
    public float[] cubeScalar;
    public int[][] cubeSur;
    public int index;

    public void initial()
    {
        index = 0;
        points = new float[ptNum][3];
        cubePoints = new float[ptNum*8][3];
        cubeScalar = new float[ptNum*6];
        cubeSur = new int[ptNum*6][4];
    }

    public void createCubeData(float[] centralPoint)
    {
        points[index] = centralPoint;
        


    }



}
