//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MiriadMrxSeg;
 
import java.awt.*;

public class BorderPanel extends Panel {
    
    int top, left, bottom, right;

    public BorderPanel() {
        super();
        setBackground(Color.lightGray);
        top = left = bottom = right = 4;
    }
 
    public void setInsets(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        //System.out.println("Border panel preferred size: " + d);
        return d;
    }
    
    public Insets insets() {
        return new Insets(top, left, bottom, right);
    }

    public void paint(Graphics g) {
     
        /*Dimension size = this.size();
        g.setColor(Color.red);
        g.drawRect(left/2, top/2,
                    size.width - left/2 - right/2,
                    size.height - top/2 - bottom/2);
        g.setColor(Color.white);
        g.drawRect(left/2+1, top/2+1,
                   size.width - left/2 - right/2,
                   size.height - top/2 - bottom/2);
         */
    }
}
