/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

/**
 *
 * @author dj
 */
public class djVtkPoint extends djVtkObj {

    public int pointId = -1;
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;
    public List<djVtkCell> cellsList = new ArrayList<djVtkCell>();

    public djVtkPoint() {
    }

    public djVtkPoint(int nIndex, float x, float y, float z) {
        this.pointId = nIndex;
        this.x = x;
        this.y = y;
        this.z = z;
        cellsList = new ArrayList();
    }

    public boolean isEqualTo(djVtkPoint thePoint) {
        if ( Math.abs(this.x - thePoint.x) < 0.01 && Math.abs(this.y - thePoint.y) < 0.01 && Math.abs(this.z - thePoint.z) < 0.01) {
            return true;
        } else {
            return false;
        }
    }
    
    public void normalize()
    {
    	float radius = (float)java.lang.Math.sqrt(x*x+y*y+z*z);
    	this.x = this.x/radius;
    	this.y = this.y/radius;
    	this.z = this.z/radius;
    }
    
}
