/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

/**
 *
 * @author dajiang
 */
public class djVtkFiberData extends djVtkData {

    public djVtkFiberData(String strSurFile) {
        super(djVtkDataDictionary.VTK_DATATYPE_FIBERS, strSurFile);
    }

    public djVtkFiberData() {
    }
}
