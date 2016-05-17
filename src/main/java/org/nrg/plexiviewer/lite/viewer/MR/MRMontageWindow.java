//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MR;


import java.awt.*;
import ij.gui.ImageWindow;
import java.net.*; 
import java.awt.image.*;
import java.awt.event.*;
import org.nrg.plexiviewer.lite.ui.PlexiMenuBar;
import org.nrg.plexiviewer.lite.utils.*;


public class MRMontageWindow extends ij.gui.ImageWindow implements MRWindow, ActionListener{
    
    private static final int TEXT_GAP = 0;
    
    private MRImage mrimage;
    private SmallLabel lab;
    private ToolPanel tp;
    private static MRCanvas mrcanvas;
    private String messageStr = "";
    private String descripStr = "";
	private PlexiMenuBar pMenuBar;
    
    public MRMontageWindow(MRImage image) {
        super(image.getImagePlus(),mrcanvas = new MRCanvas(image));
        tp = new ToolPanel(ToolPanel.MONTAGE_TOOLS);
        add(tp);
		this.mrimage = image;
		//setIcon();
		setBackground(Color.lightGray);
		pMenuBar = new PlexiMenuBar((ImageWindow)this,HTTPDetails.getHost(),  mrimage.getUserSelection());
	    this.setMenuBar(pMenuBar.getMenuBar());
   }
    
    private void setIcon() {
        URL url = this.getClass().getResource("/brain.gif");
        if (url==null)
            return;
        Image img = null;
        try {img = createImage((ImageProducer)url.getContent());}
        catch(Exception e) {}
        if (img!=null)
            setIconImage(img);
    }
      

		   
    public void setMessage(String d,String m) {
        descripStr = d;
        messageStr = m;
    }
    
    public MRMontageWindow getWindow(){
        
        return this;
    }
    
    public void message(){
        if (tp!=null)
        	tp.setMessage(descripStr,messageStr);
    }
    
    public void update(Graphics g) {
       message();
    }
     
    public boolean close() {
        //by locking the image, it won't be deleted in the close
		 super.close();
		dispose();
		mrimage.getImagePlus().flush();
	     mrimage.windowClosing();
         return true;
    }
    
    /** Override ImageWindow getInsets() so that no room is added for text */
    public Insets getInsets() {
        Insets insets = super.getInsets();
        int TEXT_GAP = 10; //this is from the ImageJ sourcecode.
        return new Insets(insets.top-TEXT_GAP, insets.left, insets.bottom, insets.right);
    }

    public void drawInfo(Graphics g){
    }
    
    public int getCurrentTool(){
        return tp.getCurrentSelection();
    }
 
	public void windowClosing(WindowEvent e) {
		pMenuBar.clear();
		close();
	}
	
 
	public void windowActivated(WindowEvent e) {
		super.windowActivated(e);
		ImageWindow win = (ImageWindow)this;
		win.getCanvas().setMagnification(mrimage.getScale());
	}
	
	public void actionPerformed(ActionEvent e) {
	   }

	public boolean showCrossHair() {
		boolean rtn=true;
		if (pMenuBar!=null) rtn=pMenuBar.showCrossHair();
		return rtn;
	}

		public boolean showRuler() {
			boolean rtn=true;
			if (mrimage.getLayout()==null) {
				rtn=false;
			}else {
				if (!mrimage.getLayout().getName().equalsIgnoreCase("native")) {
					if (pMenuBar!=null) rtn=pMenuBar.showRuler();
				}else {
					rtn=false;
				}	
			}
			return rtn;
		}
	
		public boolean markRight() {
			boolean rtn=true;
			if (pMenuBar!=null) rtn=pMenuBar.markRight();
			return rtn;
		}
}

