//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MiriadLobarSeg;
import java.awt.*;

class SmallLabel extends java.awt.Label{
    Dimension d;
    SmallLabel(int w, int h){
        super();
        d = new Dimension(w,h);
    }
    
    public void setSize(int w, int h){
        super.setSize(w,h);
        d.setSize(w,h);
    } 
    
    public Dimension getPreferredSize(){
        Dimension s = super.getPreferredSize();
        //return s;
        //System.out.println("Small label preferred size: " + d);
        return d;
    }
    
    
    public void update(Graphics g)
    {
        paint(g);
    }
    
    
}
