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
public class djVtkAttriDistribution {

    public int itemNum = -1;
    public int attriLength = -1;
    public float[][] attriDistribution;
    public float[][] attriDistributionNormlize;
    public Map<String, Integer> attriIndexMap;

    public djVtkAttriDistribution(int ItemNum) {
        this.itemNum = ItemNum;
        attriIndexMap = new HashMap<String, Integer>();
    }

    public void Initial() {
        this.attriLength = attriIndexMap.size();
        attriDistribution = new float[itemNum][attriLength];
        attriDistributionNormlize = new float[itemNum][attriLength];
        for (int i = 0; i < itemNum; i++) {
            for (int j = 0; j < attriLength; j++) {
                attriDistribution[i][j] = 0;
                attriDistributionNormlize[i][j] = 0.0f;
            }
        }
    }

    public void normlizeDistribution() {
        float tmpSum = 0.0f;
        for (int i = 0; i < itemNum; i++) {
            tmpSum = 0;
            for (int j = 0; j < attriLength; j++) {
                tmpSum = tmpSum + attriDistribution[i][j];
            }
            for (int j = 0; j < attriLength; j++) {
                if (tmpSum != 0) {
                    attriDistributionNormlize[i][j] = attriDistribution[i][j] / tmpSum;
                }
            }
        }
    }
}
