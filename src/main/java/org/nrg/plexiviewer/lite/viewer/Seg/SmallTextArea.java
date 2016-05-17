//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.Seg;
import java.awt.*;

public class SmallTextArea extends java.awt.TextArea {
    
    Color bg;
    
    public SmallTextArea(String s, int r, int c, int scrollbars){
        super(s,r,c,scrollbars);
        bg = new Color(0,0,0);
    }
    
    public SmallTextArea(){
        this("",0,0,TextArea.SCROLLBARS_BOTH);
    }
    
    
    public void setBackground(Color col) {
        bg = col;
        super.setBackground(col);
    }
    
    public Color getBackground() {
        return bg;
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(300,80);
    }
}
