//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MiriadEmSeg;
import ij.gui.*;
import java.awt.*;
import java.awt.event.*;
import org.nrg.plexiviewer.lite.manager.PlexiManager;
import org.nrg.plexiviewer.lite.ui.PlexiMenuBar;
import org.nrg.plexiviewer.lite.utils.*;

public class SegStackWindow extends StackWindow{
	SegCanvas segCanvas;
	//private static final int TEXT_GAP = 0;
    public SegImage _image;
	//private String messageStr = "";
	//private String descripStr = "";
    ToolPanel tp;
	private PlexiMenuBar pMenuBar;
	
	
    public SegStackWindow(SegImage image, SegCanvas canvas) {
        super(image.getStrImage(), canvas);
		segCanvas=canvas;
        _image = image;
        if (_image.getDisplay().equalsIgnoreCase("MONTAGE"))
        	sliceSelector.setVisible(false);	
        setBackground(Color.lightGray);
        tp = new ToolPanel(); 
        if (_image.getDisplay().equalsIgnoreCase("MONTAGE"))
        	tp.setVolumeCntVisible(false);
        add(tp);
		tp.setRegionName(((Integer)PlexiManager.getSetting("SegImage.regionID")).intValue());
		System.out.println("Seg::SegStackWindow Selected Index " + tp.displayChoice.getSelectedIndex() + "   " +  tp.displayChoice.getSelectedItem());
		pMenuBar = new PlexiMenuBar((ImageWindow)this,HTTPDetails.getHost(),  _image.getUserSelection());
		this.setMenuBar(pMenuBar.getMenuBar());
		pack();
    }
     
     
	public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
		super.adjustmentValueChanged(e);
		if (_image != null){
			_image.setCoordBySlice();
			//mrimage.printCoords();
		}
	}  
    
	/** Updates the stack scrollbar. */
		public void updateSliceSelector() {
        
			//if the current view is transverse, then we need to handle it so that we move
			//from last slice to first (top of head to bottom). For other views, we want to
			//move from first to last (default for ImageJ)
        
			if (_image == null){
				//System.out.println("MRStackWindow: mrimage null!");
				return;
			}
			super.updateSliceSelector();
			if (_image != null){
				_image.setCoordBySlice();
			}
			else
				System.out.println("Seg StackWindow: mrimage is null");
        
		}
     
	/** Displays the specified slice and updates the stack scrollbar. */
	  public void showSlice(int index) {
		  if (index>=1 && index<=_image.getStrImage().getStackSize()) {
			_image.getStrImage().setSlice(index);
			_image.getAsegImage().setSlice(index);
		  }
	  }
 
	public void setVolumeCount(String i){
			tp.setVolumeCount(i);
	}
     
    public void setRegion(int i){
         tp.setRegion(i);
    }
        
    public void setCoords(int x, int y, int z){
         tp.setCoords(x,y,z);
    }
    
	public void focusGained(FocusEvent e) {
	}

    
    public SegStackWindow getWindow(){
        
        return this;
    }
    
    public void doRescale(int s){
        _image.rescale(s);
    }
    
   
	public void setMessage(String messageStr){
	  if (tp!=null)
		  tp.setMessage(messageStr);
  	}
	 
	
     
	public void setSelectorToSlice() {
		sliceSelector.setValue(_image.getStrImage().getCurrentSlice());
		if (_image.getOrientation().equalsIgnoreCase("transverse")) {
			int selected = sliceSelector.getValue();
			slice = sliceSelector.getMaximum() - selected;
			sliceSelector.setValue(slice);
		}
		//notify();				
	}

	public void updateCrosshairs(int x, int y){
		segCanvas.setCrosshairPosition(x,y);
		segCanvas.repaint();
	}
	

	public void stopThread() {
		synchronized(this) {
			done = true;
			notify();
		}
	}

	public boolean close() {
		//by locking the image, it won't be deleted in the close
		segCanvas.setVolumeCount(null);
		tp.flush();
		super.close();
		pMenuBar=null;
		_image.getImagePlus().flush();
		_image.getAsegImage().flush();
		return true;
	}	
		
	public void windowClosing(WindowEvent e) {
		pMenuBar.clear();
		close();
		_image.windowClosing();
	}
	

	public void windowActivated(WindowEvent e) {
		ImageWindow win = (ImageWindow)this;
		win.getCanvas().setMagnification(_image.getScale());
	}	
    
    public SegImage getSegImage() {
    	return _image;
    }
 
	/** Override ImageWindow getInsets() so that no room is added for text */
	public Insets getInsets() {
		Insets insets = super.getInsets();
		int TEXT_GAP = 10; //this is from the ImageJ sourcecode.
		return new Insets(insets.top-TEXT_GAP, insets.left, insets.bottom, insets.right);
	}
	
	public void drawInfo(Graphics g){
	}
	
	public boolean showCrossHair() {
			boolean rtn=true;
			if (pMenuBar!=null)
			   rtn= pMenuBar.showCrossHair();
			return rtn;   
	}

	public boolean showRuler() {
		boolean rtn=true;
		if (_image.getLayout()!=null && !_image.getLayout().getName().equalsIgnoreCase("native")) {
			if (pMenuBar!=null) rtn=pMenuBar.showRuler();
		}else {
			rtn=false;
		}
		
		
		return rtn;
	}

	public boolean markRight() {
		boolean rtn=true;
		if (pMenuBar!=null) rtn=pMenuBar.markRight();
		return rtn;
	}
 	
}
