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


/**againnnnn
 *what
 * @author dj add to githubbbbb   dfadfa
 */
public class test1 {

    public List<djVtkPoint> pointList = new ArrayList<djVtkPoint>();
    public ArrayList cellList = new ArrayList();
    public djVtkPoint tmpPt;
    public djVtkSurCell tmpCell;



    public test1() {

//        tmpPt = new djVtkPoint(0,0.1f,0.2f,0.3f);
//        pointList.add(tmpPt);
//
//        tmpCell = new djVtkSurCell(1);
//        //tmpCell.oPt1=tmpPt;
//        cellList.add(tmpCell);
        djVtkPoint p1 = new djVtkPoint(0, 0.5f, 137.7f, -2.6f);
        djVtkPoint p2 = new djVtkPoint(1, 0.6f, 137.3f, -1.6f);
        djVtkPoint p3 = new djVtkPoint(2, 1.8f, 131.7f, -1.6f);

        List<Integer> l_1 = new ArrayList<Integer>();
        List<Integer> l_2 = new ArrayList<Integer>();
        List<Integer> l_3 = new ArrayList<Integer>();
        l_1.add(1);
        l_2.add(2);
        l_2.add(3);

        Map<Integer, Map<Integer, Map<Integer, List<Integer>>>> m = new HashMap<Integer, Map<Integer, Map<Integer, List<Integer>>>>();
        pointList.add(p1);
        pointList.add(p2);
        pointList.add(p3);

        for (int i = 0; i < pointList.size(); i++) {
            int pointID = pointList.get(i).pointId;
            int bX = (int) pointList.get(i).x;
            int bY = (int) pointList.get(i).y;
            int bZ = (int) pointList.get(i).z;

            if (m.get(bX) == null) {
                Map<Integer, Map<Integer, List<Integer>>> m_1 = new HashMap<Integer, Map<Integer, List<Integer>>>();
                Map<Integer, List<Integer>> m_2 = new HashMap<Integer, List<Integer>>();
                List<Integer> li = new ArrayList<Integer>();
                li.add(pointID);
                m_2.put(bZ, li);
                m_1.put(bY, m_2);
                m.put(bX, m_1);
            } else {
                if (m.get(bX).get(bY) == null) {
                    Map<Integer, List<Integer>> m_2 = new HashMap<Integer, List<Integer>>();
                    List<Integer> li = new ArrayList<Integer>();
                    li.add(pointID);
                    m_2.put(bZ, li);
                    m.get(bX).put(bY, m_2);
                } else {
                    if (m.get(bX).get(bY).get(bZ) == null) {
                        List<Integer> li = new ArrayList<Integer>();
                        li.add(pointID);
                        m.get(bX).get(bY).put(bZ, li);
                    } else {
                        m.get(bX).get(bY).get(bZ).add(pointID);
                    }
                }
            }
        }

        System.out.println("tttest..");

    }

    public void testWrite() {
        FileWriter fw = null;
        try {
            fw = new FileWriter("dj_output_fibers.vtk");
            for (int i = 0; i < 10; i++) {
                fw.write("test" + i + "\r\n");
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

    }
}
