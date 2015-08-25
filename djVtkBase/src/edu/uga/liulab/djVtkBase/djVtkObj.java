/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uga.liulab.djVtkBase;

/**
 *
 * @author dj
 */
public class djVtkObj implements Cloneable {
     @Override
    protected Object clone()  {
         djVtkObj o = null;
         try {
             o = (djVtkObj) super.clone();
         } catch (CloneNotSupportedException e) {
             e.printStackTrace();
         }
        return o;
    }

}
