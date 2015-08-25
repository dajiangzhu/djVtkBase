/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dj
 */
public class djVtkSurCell extends djVtkCell {
    public float cellArea=0.0f;

    public djVtkSurCell(int cellIndex) {
        this.cellId = cellIndex;
    }
}
