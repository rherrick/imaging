//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MiriadEmSeg;

import ij.*;
import ij.gui.*;
import ij.process.*;
import java.awt.*;
import java.awt.event.*;
import  org.nrg.plexiviewer.lite.xml.*;
import  org.nrg.plexiviewer.lite.*;
import  org.nrg.plexiviewer.lite.tunneler.*;
import  org.nrg.plexiviewer.lite.gui.*;
import org.nrg.plexiviewer.lite.display.*;
import org.nrg.plexiviewer.lite.utils.*;
import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.lite.manager.*;
//import java.util.*;
public class SegImage extends java.lang.Object implements KeyListener, PlexiImageViewerI {
    
	private int x,y,z;
	private float ipValue;
	//private int currentImageIndex;
	private SegStackWindow stackWin;
	private int scaleFactor = 1;
	private boolean selfClose = false;
	private boolean rescaling = false;
	private int nColumns;
	//private int nRows;
	private PlexiCoordinates coords;
	//private String viewableGroupName;
	private String orientation;
	//private String sequenceName;
	private String display;
	private boolean radiologic;

	private MontageDisplay mDisplay;
	private UserSelection uselection;
	//private HiRes hRes;
	private int winWidth, winHeight, slices;
	//private int numberOfImagesToBeFetched;
	private SegCanvas segCanvas;
	int[] volCount ;
	SegVolumeCounter segVolCounter; 
	private ImagePlus asegIP, origIP;
	private PlexiMessagePanel msgPanel;
	/** Creates new MRImage */


	public SegImage(UserSelection u) {
		uselection = u;
		winWidth=-1;
		winHeight=-1;
		init();
		coords = null;
		mDisplay = null;
		x = y = z = 0;
		volCount = null;
	}
	
	public void init() {
		orientation = uselection.getOrientation();
		display = uselection.getDisplay();
		radiologic = uselection.isRadiologic();
	}
    
	/* loads images from server using MRTunneler, an implementation of HTTP tunneling */
	public  void loadByTunnel(int i){
		UserSelection u = (UserSelection)getUserSelection().clone();
		u.setHiResLayerNum(((Integer)u.getHiResLayerNos().elementAt(i)).intValue());
		NoMsgImageTunneler tunnel = new NoMsgImageTunneler(this);
		tunnel.load(u,i);
	}
    
    
	public int loadImages(){
		int rtn =1;
		//String key ="";
		//int vectorIndex;
		//boolean t;
		try{
			
			if (this.getDisplay().equalsIgnoreCase("Montage")) {
				uselection.setHiResLayerNum(((Integer)uselection.getHiResLayerNos().elementAt(0)).intValue());
				MontageImageLoader loader = new MontageImageLoader(this,false);
				loader.load();
				uselection.setHiResLayerNum(-1);
				msgPanel.setMessage("Procuring requested Image ......");
				loadByTunnel(0);
				loadByTunnel(1);
			}else {
				UserSelection u = (UserSelection)getUserSelection().clone();
				u.setHiResLayerNum(((Integer)u.getHiResLayerNos().elementAt(0)).intValue());
				ImageDimLoader loader = new ImageDimLoader(u,HTTPDetails.getHost());
				loader.load();
				if (loader.isError()) {
					/*initIP(202,200,"Error");
					origIP.show();
					Vector msg = new Vector(); msg.addElement("Image couldnt be found");
					LoadErrorDialog led = new LoadErrorDialog(origIP.getWindow(), msg, false);
					led.show();*/ 
					msgPanel.setMessage("Image couldnt be found");
					loader=null;
					u=null;
					System.gc();
					Runtime.getRuntime().gc();
					rtn=0;
				}else {
					segVolCounter = new SegVolumeCounter(uselection.getSessionId(), HTTPDetails.getHost());
					if (volCount==null)
						segVolCounter.setVolumeCounts();
					volCount = segVolCounter.getVolumeCount();
					this.setMontageDisplay((MontageDisplay)loader.getMontageDisplay().clone());
					this.setWindowDimensions((PlexiImageFile)loader.getPlexiImageFile().clone());
					loader=null;
					u=null;
					System.gc();
					Runtime.getRuntime().gc();
					msgPanel.setMessage("Procuring requested Image");
					loadByTunnel(0);
					loadByTunnel(1);
				}
			}
		}catch (OutOfMemoryError ome) {
			handleOutOfMemoryError();
   		}				
		return rtn;
	}
     
	public void windowClosing(){
		//this might be a bit of a hack but here's what's going on.
		//during rescaling, the old window closes and a new one opens.
		//in this case, we don't want to remove the image the manager.
		//so if the selfClose flag is set, don't call remove image, just
		//reset it to false.
		if (selfClose){
			selfClose = false;
		} else {
			segVolCounter=null;
			volCount=null;
			PlexiManager.removeImage(this);
		}
	}
	
	public int open(PlexiMessagePanel msgPanel) {
		this.msgPanel=msgPanel;
		int rtn = 1;

		if (PlexiManager.findImage(uselection)!=null) {
			PlexiManager.findImage(uselection).getWindow().toFront();
		}else {
				rtn = loadImages();
				System.gc();
		}
		return rtn;
	}
   
	public void setWaitCursor(boolean wait){
	}
	
	public void setMinAndMax(ImageProcessor ip) {
			double min2 = ip.getMin();
			double max2 = ip.getMax();
			if ((ip instanceof ShortProcessor) || (ip instanceof FloatProcessor)) {
				ip.resetMinAndMax();
			}
			ip.setMinAndMax(min2, max2);
			min2 = ip.getMin();
			max2 = ip.getMax();
		}
	
		public void setSlice(int sNo) {
			origIP.setSlice(sNo);
			asegIP.setSlice(sNo);
		}
	
		public void setUpImages() {
			int slice = 1;
			if (display.equalsIgnoreCase("STACK"))
				slice=50;
			setSlice(slice);
			setMinAndMax(origIP.getProcessor());
		}
	
	
	public void show(int scale) {
		int currentRegionShown = ((Integer)PlexiManager.getSetting("SegImage.regionID")).intValue();
		segCanvas = new SegCanvas(this);
		segCanvas.setCmRegion(currentRegionShown);
		segCanvas.setVolumeCount(volCount);
		segCanvas.updateAndDraw();
		show();
		stackWin.tp.setRegionName(currentRegionShown);
		stackWin.setMessage("");
	}
	
	public boolean show() {
		boolean rtn = true;
		//setUpImages();
		//long startTime = System.currentTimeMillis();
		if (selfClose) {
			asegIP.setWindow(null);
			origIP.setWindow(null);
			stackWin.stopThread();
			boolean isRunning = stackWin.running || stackWin.running2;
			stackWin.running = stackWin.running2 = false;
			if (isRunning) IJ.wait(500);
			stackWin.setVisible(false);
			stackWin.dispose();
		}
		Runtime.getRuntime().runFinalization();
		long totalMemory = Runtime.getRuntime().totalMemory();
		Runtime.getRuntime().gc();
		long freeMemory = Runtime.getRuntime().freeMemory();
		int freeMemPct = (int)(((float)freeMemory/(float)totalMemory)*100);
		System.out.println("Seg Image Memory Free:: " + freeMemPct  +"%");
		int limit = 25;
		BrowserVersionInfo bInfo = new BrowserVersionInfo();
		if (bInfo.isWin()) {
			limit =15;
		}
		if (freeMemPct<limit) {
			//PlexiNoFrillsTextWindow tw = new PlexiNoFrillsTextWindow("Image Viewer Warning");
			//tw.show("Not enough memory available to show the image.");
			msgPanel.setMessage("Not enough memory! See Viewer Help to increase memory");
			System.out.println("Not enough memory available to show " );
			getAsegImage().flush();
			getStrImage().flush();
			windowClosing();
			rtn = false;
		}else {
			stackWin = new SegStackWindow(this,segCanvas);
			stackWin.setRegion(((Integer)PlexiManager.getSetting("SegImage.regionID")).intValue());
			msgPanel.setMessage("Procuring requested Image");
			stackWin.setMessage("Procuring image. Please wait");
			stackWin.updateSliceSelector();
			listenTo(stackWin);
		}
		return rtn;
	}


	public String setTitle() {
		String title = uselection.getWindowTitle(); 
		if (uselection.isRadiologic())
			title += ":Radiologic";
		return title;	
	}
		
	public synchronized void setMessage(String descriptor, String message){
		  if (stackWin != null){
			stackWin.setMessage(message);
			//stackWin.message();
			stackWin.repaint();
		  }
		//msgPanel.setMessage(descriptor+" " + message);
	}	   
    
	public  ImagePlus getImageCopy() {return new ImagePlus();}
	public  synchronized ImagePlus getImagePlus() {return getStrImage(); }
	public ImagePlus getAsegImage() { return asegIP; }
	public ImagePlus getStrImage() { return origIP; }
	public int getScale() { return scaleFactor; } 
    
	public void rescale(int s) {
		if (s > 4) return;
		if (display.equalsIgnoreCase("MONTAGE")) return;
		if (s != scaleFactor){
			//System.out.println("Rescale called " + s);
			selfClose = true;
			scaleFactor = s;
			//this process closes the old window. Set this flag so that windowClosing doesn't
			//remove this object from the manager list.
			rescaling = true;
			coords.setScale(scaleFactor);
		   // System.out.println("Scaling to " + scaleFactor);
			if (display.equalsIgnoreCase("STACK")){
				show(scaleFactor);
			}
			adjustCoords(x,y,z,ipValue,true, orientation, this.radiologic);
			rescaling = false;
		}
	}
    
	
    
	public boolean isScaling(){
		 return rescaling;
	}    
        
	//listen for key events coming from image window...
	public void keyTyped(java.awt.event.KeyEvent e) {}
	public void keyReleased(java.awt.event.KeyEvent e) {}
	
	public void keyPressed(java.awt.event.KeyEvent e) {
        
		int keycode = e.getKeyCode();
		char keyChar = e.getKeyChar();
		if (Character.isDigit(keyChar)){
			rescale(Integer.parseInt(String.valueOf(keyChar)));
			int currentSlice = asegIP.getCurrentSlice();
			asegIP.setSlice(currentSlice);
			origIP.setSlice(currentSlice);
			if (stackWin!=null)
				stackWin.setSelectorToSlice();
		} else if (keycode == e.VK_LEFT){
			if (stackWin != null){
				int currentSlice = origIP.getCurrentSlice();
				origIP.setSlice(currentSlice+1);
				asegIP.setSlice(currentSlice+1);
			}
		} else if (keycode == e.VK_RIGHT){
			if (stackWin != null){
				int currentSlice = origIP.getCurrentSlice();
				origIP.setSlice(currentSlice-1);
				asegIP.setSlice(currentSlice-1);
			}
		}
	}
    	
	public UserSelection getUserSelection() {return uselection;}
	public String getDisplay(){return display;};
	public boolean getRadiologic(){ return radiologic; };
	
	
	
	public void syncRegions(int region, String regionName) {
		String imageViewerClass = uselection.getImageViewerClass();
		ipValue = asegIP.getProcessor().getPixelValue(x,y);
		PlexiManager.syncSegStacks(this.x, this.y, this.z, ipValue, orientation , radiologic , mDisplay.getLayout().getName(), imageViewerClass , region,  regionName);
	}

	public void syncEvent(boolean resetSlice){
		ipValue = asegIP.getProcessor().getPixelValue((int)(x/getScale()), (int)(y/getScale()));
		PlexiManager.syncStacks(this.x, this.y, this.z, ipValue, resetSlice, orientation, radiologic ,mDisplay.getLayout().getName(),uselection.getProject(), uselection.getDataType());
	}
    
	public void syncEvent(int x, int y, boolean resetSlice){
		setCoords(x,y);
		ipValue = asegIP.getProcessor().getPixelValue((int)(x/getScale()), (int)(y/getScale()));
		PlexiManager.syncStacks(this.x, this.y, this.z,ipValue,resetSlice,orientation, radiologic , mDisplay.getLayout().getName(), uselection.getProject(), uselection.getDataType());
	}
    
	public void setCoords(int x, int y){
		if (display.equalsIgnoreCase("STACK"))
			setStackCoords((int)x,(int)y);
		else
			setMontageCoords(x,y);
	}
    
	private void setMontageCoords(int x, int y) {
		coords.setTalFromMontage(x,y);
		setCoords(coords.getPosTal());
	}

	 private void setCoords(Point3d talCoordinates) {
		 this.x = (int)talCoordinates.getX();
		 this.y = (int)talCoordinates.getY();
		 this.z = (int)talCoordinates.getZ();	
		 stackWin.setCoords(x,y,z);
	 }
    
	 private void setStackCoords(int x, int y) {
		int currentSlice = (this.getStrImage()).getCurrentSlice();
		coords.setTalFromStack(x,y,currentSlice);
		setCoords(coords.getPosTal());
	 }
    
	 public void setCoordBySlice(){
		int currentSlice = origIP.getCurrentSlice();
		coords.setTalFromSlice(currentSlice);
		this.z = (int)coords.getPosTal().getZ();
	 }    
	
	public void adjustCoords(int x, int y, int z, float ipValue, boolean resetSlice, String fromView, boolean fromRadiologic) {
 		if (display.equalsIgnoreCase("STACK")){
			coords.setTalFromTal(x,y,z);
			Point3d winPos = coords.getPosWindow(display, fromView, fromRadiologic);
			if (resetSlice){
				stackWin.showSlice((int)winPos.getZ());
			}
			setCoords(new Point3d(x,y,z));
			int [] val = this.getAsegImage().getPixel((int)(winPos.x/getScale()), (int)(winPos.y/getScale()));
			stackWin.setRegion(val[0]);
			stackWin.updateCrosshairs((int)winPos.getX(),(int)winPos.getY());
			getAsegImage().updateAndDraw();
			getStrImage().updateAndDraw();
		}
	}

	public void adjustRegions(int x, int y, int z, float ipValue, boolean resetSlice, String regionName, boolean fromRadiologic, int region) {
		segCanvas.setCmRegion(region);
		stackWin.tp.displayChoice.select(regionName);
		stackWin.tp.setVolumeCount(segCanvas.getVolumeCount(region));
	}


	public void listenTo(Component c){
        
		  c.addKeyListener(this);
		  c.removeKeyListener(IJ.getInstance());
		  if (c instanceof Container){
			  Component [] allComps = ((Container) c).getComponents();
			  for (int i=0; i< allComps.length; i++){
				  listenTo(allComps[i]);
			  }
		  }
	  }
	
	/**
	 * @param b
	 */
	public void setSelfClose(boolean b) {
		selfClose = b;
	}
	
	
	public synchronized void setMontageDisplay(MontageDisplay md) {
		mDisplay = md;
	}
	
	/**
	 * @return
	 */
	public Layout getLayout() {
		if (mDisplay!=null)
			return mDisplay.getLayout();
		else 
			return null;	
	}

	
	
	public  void setWindowDimensions(int w, int h, int s) {
		winWidth = w;
		winHeight = h;
		slices=s;
	}
	
	public  void setWindowDimensions(PlexiImageFile pf) {
			winWidth = pf.getDimX();
			winHeight = pf.getDimY();
			slices=pf.getDimZ();
			initIP(winWidth, winHeight,setTitle());
			segCanvas = new SegCanvas(this);
			if (display!=null && display.equalsIgnoreCase("Montage"))
				coords = new PlexiCoordinates(mDisplay.getOriginalWidth(),mDisplay.getOriginalHeight(), mDisplay.getOriginalStackSize(), mDisplay, orientation, radiologic, mDisplay.getNumberOfColumns());
			else 
				coords = new PlexiCoordinates(origIP.getWidth(), origIP.getHeight(), slices , mDisplay, orientation, radiologic, nColumns);
			coords.setScale(getScale());	
			segCanvas.setVolumeCount(volCount);
			show();
		}
		
	public void initIP(int width, int height,String title) {
			origIP = new ImagePlus(title,new ByteProcessor(width, height));
			asegIP = new ImagePlus(title,new ByteProcessor(width, height));
			origIP.getProcessor().setColor(java.awt.Color.black);
			origIP.getProcessor().fill();
			asegIP.getProcessor().setColor(java.awt.Color.black);
			asegIP.getProcessor().fill();

	}

	public String getOrientation() {
		return orientation;	
	}
	
	public synchronized Rectangle getWindowDimensions() {
		return new Rectangle(winWidth,winHeight);
	}
	
	public boolean isInitialized() {
		return true;
	}
	
	public void handleOutOfMemoryError() {
		System.out.println("Handling out of memory " + selfClose);
		windowClosing();
		if (origIP!=null)origIP.flush();
		if (asegIP!=null)asegIP.flush();
		volCount=null;
		if (segCanvas!=null)segCanvas.setVolumeCount(null);
		if (stackWin!=null) {
			stackWin.removeKeyListener(this);
			stackWin.close();
			System.out.println("Out of Memory Encountered");
			stackWin.setVisible(false);
			stackWin.dispose();
		}
		msgPanel.setMessage("Not enough memory! See Viewer Help to increase memory");
		//PlexiNoFrillsTextWindow tw = new PlexiNoFrillsTextWindow("Image Viewer Warning");
		//tw.show("Not enough memory available to show the image.\nPlease try increasing the memory (see Viewer Help on how to)");
	}
	
	public synchronized void setImage(int index, ImageStack s) {
		try {
			if (index==0) {
				ImageWindow win = origIP.getWindow();
				origIP.setWindow(null);
				origIP.setStack(setTitle(),s);
				origIP.setWindow(win);//hack since ImgaeJ sets the window depending on StackSize
			}else {
				asegIP.setStack(setTitle(),s);
				((SegCanvas)origIP.getWindow().getCanvas()).updateAndDraw();
			}
			if (this.getDisplay().equalsIgnoreCase("Stack")) {
				stackWin.updateSliceSelector();
			}	
			if (asegIP.getStackSize()==s.getSize() && origIP.getStackSize()==s.getSize()) {
				stackWin.setRegion(((Integer)PlexiManager.getSetting("SegImage.regionID")).intValue());
				stackWin.setMessage("");
				msgPanel.setMessage("");
				if (this.getDisplay().equalsIgnoreCase("Montage")) {
					stackWin.getCanvas().repaint();
					stackWin.repaint();
				}
			}
		}catch(OutOfMemoryError ome) {
			handleOutOfMemoryError();
		}
	}
	
	public void setImage(int index, ImagePlus s) {
		
	}


	
	public void updateCrossHair(){
	  if (display.equalsIgnoreCase("STACK")) {
		Point3d point = coords.getPosWindow(display,orientation,radiologic);
		stackWin.updateCrosshairs((int)point.x, (int)point.y);
	  }
   }
   
   public void updateCrossHair(String fromOrientation, int sliceNo){
		 if (display.equalsIgnoreCase("STACK") && !orientation.equalsIgnoreCase(fromOrientation)) {
			   Point3d point = coords.getPosWindow(display,orientation,radiologic);
			   int x = (int)point.x;
			   int y = (int)point.y; 
			   if (fromOrientation.equalsIgnoreCase("TRANSVERSE")) {
					   y=sliceNo; 
			   }else if (fromOrientation.equalsIgnoreCase("CORONAL")) {
				   if (orientation.equalsIgnoreCase("TRANSVERSE")) {
					   y= sliceNo;
				   }else if (orientation.equalsIgnoreCase("SAGITTAL")) {
					   x = sliceNo;
				   }
			   }else if (fromOrientation.equalsIgnoreCase("SAGITTAL")) {
					x= sliceNo;
			   }
			   setCoords(x,y);
			   //printCoords();
			   stackWin.updateCrosshairs(x,y);
		 }
   }
}
	   
