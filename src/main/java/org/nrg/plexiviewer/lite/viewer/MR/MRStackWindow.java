//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MR;

import ij.gui.ImageWindow;
import ij.gui.StackWindow;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.ui.PlexiMenuBar;
import org.nrg.plexiviewer.lite.utils.HTTPDetails;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.WindowEvent;
import java.awt.image.ImageProducer;
import java.net.URL;


public class MRStackWindow extends StackWindow implements MRWindow{
    
    private static final int TEXT_GAP = 0;
    public ToolPanel tp;
    private String messageStr = "";
    private String descripStr = "";
    public MRImage mrimage;
    public  MRCanvas mrcanvas;
    private PlexiMenuBar pMenuBar;
    
    public MRStackWindow(MRImage image, MRCanvas canvas) {
        super(image.getImagePlus(), canvas);
        mrcanvas = canvas;
        mrimage = image;
        addToolPanel();
        setscrollbar();
        setBackground(Color.lightGray);
        pMenuBar = new PlexiMenuBar((ImageWindow)this,HTTPDetails.getHost(), (UserSelection)mrimage.getUserSelection().clone());
        this.setMenuBar(pMenuBar.getMenuBar());
        pack();
        mrimage.setImageInitialized(true);
    }
    
    public void addToolPanel(){
        tp = new ToolPanel(ToolPanel.STACK_TOOLS);
        add(tp);
    }

    private void setscrollbar(){
        if (mrimage.getImagePlus() != null) {
        	int nSlices = mrimage.getStackSize();
        	if (nSlices > 1) {
    			sliceSelector = new Scrollbar(Scrollbar.HORIZONTAL, 1, 1, 1, nSlices+1);
    			add(sliceSelector);
    			if (ij!=null) sliceSelector.addKeyListener(ij);
    			sliceSelector.addAdjustmentListener(this);
    			sliceSelector.setFocusable(false);
    			int blockIncrement = nSlices/10;
    			if (blockIncrement<1) blockIncrement = 1;
    			sliceSelector.setUnitIncrement(1);
    			sliceSelector.setBlockIncrement(blockIncrement);
        	}
        }
    }

    
    public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
		super.adjustmentValueChanged(e);
        /*if (!mrimage.getOrientation().equalsIgnoreCase("transverse")) {
            super.adjustmentValueChanged(e);
        } else {
            if (!running){
                int selected = sliceSelector.getValue();
                slice = sliceSelector.getMaximum() - selected;
                notify();
            }
        }*/
        
        if (mrimage != null){
            mrimage.setCoordBySlice();
            mrimage.printCoords();
        }
    }
    
	public void stopThread() {
			synchronized(this) {
				done = true;
				notify();
			}
	}
    
    public void setSelectorToSlice() {
		sliceSelector.setValue(mrimage.getImagePlus().getCurrentSlice());
		//notify();				
    }
    
    /** Updates the stack scrollbar. */
    public void updateSliceSelector() {
        if (mrimage == null){
            //System.out.println("MRStackWindow: mrimage null!");
            return;
        }
		super.updateSliceSelector();
		
        if (mrimage != null){
            mrimage.setCoordBySlice();
            mrimage.printCoords();
        }
        else
            System.out.println("MRStackWindow: mrimage is null");
        
    }
    
    private void setIcon() {
        URL url = this .getClass() .getResource("/brain.gif");
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
    
    public MRStackWindow getWindow(){
        return this;
    }
    
    public void message(){
        if (tp!=null)
        	tp.setMessage(descripStr,messageStr);
    }
    
    public void update(Graphics g) {
        super.update(g);
        message();
    }
    
    public void updateCrosshairs(int x, int y){
	   mrcanvas.setCrosshairPosition(x,y);
	   mrcanvas.repaint();
    }
    
    public void rescale(){
        //new ScaleDialog(this).show();
    }
    
    public void doRescale(int s){
       // mrimage.rescale(s);
    }
    
    public boolean close() {
        //by locking the image, it won't be deleted in the close
        super.close();
		mrimage.getImagePlus().flush();
        mrimage.windowClosing();
        pMenuBar=null;
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
		if (win.getCanvas()!=null && mrimage!=null)
			win.getCanvas().setMagnification(mrimage.getScale());
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
