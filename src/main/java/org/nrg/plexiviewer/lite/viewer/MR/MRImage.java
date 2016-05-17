//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MR;

import java.awt.*;
import java.awt.event.*;
import ij.*;
import ij.process.*;
import ij.io.FileInfo;

import org.nrg.plexiviewer.lite.manager.*;
import  org.nrg.plexiviewer.lite.xml.*;
import  org.nrg.plexiviewer.lite.*;
import org.nrg.plexiviewer.lite.tunneler.*;
import org.nrg.plexiviewer.lite.utils.StringUtils;
import org.nrg.plexiviewer.lite.display.*;
import org.nrg.plexiviewer.lite.utils.*;
import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.lite.gui.*;

public class MRImage extends java.lang.Object implements KeyListener, PlexiImageViewerI {

	private ImagePlus ip;     //this image gets manipulated by montagemaker and maybe elsewhere
	private ImagePlus baseIp; //this image is pristine
	private MRStackWindow stackWin;
	private MontageDisplay mDisplay;
	private MRMontageWindow montageWin;
	private int x,y,z;
	private float ipValue;
	private int winWidth, winHeight, slices;
  
	private int nColumns;
	private int nRows;
	private int scaleFactor ;
	private boolean selfClose = false;
	private boolean rescaling = false;
	private UserSelection uselection;
	private PlexiCoordinates coords;
	private boolean isInit = false;
	private String pathToHiResFile;
	private PlexiMessagePanel msgPanel;

	/** Creates new MRImage */
	public MRImage(UserSelection u) {
		uselection = u;
		x = y = z = 0;
		winWidth  = -1;
		winHeight = -1;
		scaleFactor =1;
	}
    
	public int open(PlexiMessagePanel msgPanel){
		int rtn = 1;
		this.msgPanel = msgPanel;
		//System.out.println(this.toString() + "\n");
		//we need to know the dimensions of the image in order to
		//pop it up prior to loading the image.  So these numbers
		//are currently accurate but are liable to break if image
		//dimensions are ever altered.  -dan 7.2.02
		
		ip = PlexiManager.findImage(uselection);
		if (ip != null){
			rtn=0;
			ip.getWindow().toFront();
		} else {
			rtn=loadByTunnel();
		}
		return rtn;
	}


	public String setTitle() {
		String title = uselection.getWindowTitle();
		if (uselection.isRadiologic())
			title += ":Radiologic";
		return title;	
	}
	
	public void initIP(int width, int height, String title) {
		ip = new ImagePlus(title,new ByteProcessor(width, height));
		//System.out.println("MRIMAGE::Came to InitIP");
	}
    
	/* loads images from server using MRTunneler, an implementation of HTTP tunneling */
	public int loadByTunnel(){
		int rtn=1;
		//if (!uselection.hasFile()) {
		//	uselection.setHiResLayerNum(((Integer)uselection.getHiResLayerNos().elementAt(0)).intValue());
		//}
		uselection.setHiResLayerNum(0);
		if (this.getDisplay().equalsIgnoreCase("Montage")) {
			MontageImageLoader loader = new MontageImageLoader(this);
			loader.load();
		}else {
			ImageDimLoader loader = new ImageDimLoader((UserSelection)getUserSelection().clone(),HTTPDetails.getHost());
			loader.load();
			if (loader.isError()) {
				/*initIP(202,200,"Error");
				ip.show();
				Vector msg = new Vector(); msg.addElement("Image couldnt be found");
				LoadErrorDialog led = new LoadErrorDialog(ip.getWindow(), msg, false);
				led.show(); */
				loader=null;
				System.gc();
				Runtime.getRuntime().gc();
				msgPanel.setMessage("Image couldnt be found");
				rtn=0;
			}else {
				this.setMontageDisplay((MontageDisplay)loader.getMontageDisplay().clone());
				this.setWindowDimensions((PlexiImageFile)loader.getPlexiImageFile().clone());
				this.uselection.setFile((PlexiImageFile)loader.getPlexiImageFile().clone());
				loader=null;
				System.gc();
				Runtime.getRuntime().gc();
				msgPanel.setMessage("Procuring requested Image");
				//System.out.println("Invoking ImageTunneler");
				ImageTunneler tunnel = new ImageTunneler(this,HTTPDetails.getHost());
				tunnel.load();
			} 

		}	
		return rtn;	
	}
    
    public void setImageInitialized(boolean val) {
    	isInit=val;
    }
    
	public void setWaitCursor(boolean wait){
		if (wait){
			//use the wait cursor
			if(ip!=null) {
				ip.getWindow().setCursor(Cursor.WAIT_CURSOR);
				ip.getWindow().getCanvas().setCursor(new Cursor(Cursor.WAIT_CURSOR));
			}
		} else {
			if(ip!=null) {
				//go back to the default cursors
				ip.getWindow().setCursor(Cursor.DEFAULT_CURSOR);
				ip.getWindow().getCanvas().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
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
			msgPanel.setMessage("");
			PlexiManager.removeImage(this);
		}
	}

	public void setSlice(int sNo) {
		ip.setSlice(sNo);
	}

	public void show(){
		if (uselection.getDisplay().equalsIgnoreCase("MONTAGE") || slices == 1) {
			showMontage();
		}else 	if (uselection.getDisplay().equalsIgnoreCase("STACK") ){
			showStack();
		}
	}
    
    
	private void showStack(){
		if (selfClose) {
			stackWin.stopThread();
			boolean isRunning = stackWin.running || stackWin.running2;
			stackWin.running = stackWin.running2 = false;
			if (isRunning) IJ.wait(500);
			stackWin.setVisible(false);
			stackWin.dispose();
		}
		stackWin = new MRStackWindow(this,new MRCanvas(this));
		setMessage("Procuring Image","Please wait");
		listenTo(stackWin);
	}
    
	private void showMontage(){
		montageWin = new MRMontageWindow(this);
		listenTo(montageWin);
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
	
	
	public String getSubject() { return uselection.getSessionId(); }
	public boolean getRadiologic() { return uselection.isRadiologic(); }
	public  ImagePlus getImagePlus() {
		if (ip.getTitle()==null || ip.getTitle().equals(""))ip.setTitle(setTitle()); return ip; 
	}
	public int getScale() {return scaleFactor; }
	public UserSelection getUserSelection() {return uselection;}
	
    
	public String getDisplay(){
	   return uselection.getDisplay(); 
       
	}
    
	public String getRadString(){
		if (uselection.isRadiologic())
			return "true";
		else
			return "false";
	}
    
	public void rescale(int s) {
		if (s > 4) return;
		if (uselection.getDisplay().equalsIgnoreCase("MONTAGE")) return;
		if (s != scaleFactor){
			//System.out.println("Rescale called " + s);
			selfClose = true;
			scaleFactor = s;
			//this process closes the old window. Set this flag so that windowClosing doesn't
			//remove this object from the manager list.
			rescaling = true;
            coords.setScale(scaleFactor);
           // System.out.println("Scaling to " + scaleFactor);
			if (uselection.getDisplay().equalsIgnoreCase("STACK")){
				showStack();
			}
			adjustCoords(x,y,z,ipValue,true, uselection.getOrientation(), uselection.isRadiologic());
			rescaling = false;
		}
        
	}

	public void syncEvent(boolean resetSlice){
		ipValue = ip.getProcessor().getPixelValue(x,y);
		PlexiManager.syncStacks(this.x, this.y, this.z, ipValue,resetSlice, uselection.getOrientation(), uselection.isRadiologic(), mDisplay.getLayout().getName(), uselection.getProject(), uselection.getDataType());
	}
    
	public  void syncEvent(int x, int y, boolean resetSlice){
		//System.out.println(mgrDetails.getExperiment().getType() + " " + mDisplay.getLayout().getName());
		setCoords(x,y);
		ipValue = ip.getProcessor().getPixelValue((int)(x/getScale()), (int)(y/getScale()));		
		PlexiManager.syncStacks(this.x, this.y, this.z,ipValue,resetSlice, getOrientation(), getRadiologic(), mDisplay.getLayout().getName(),uselection.getProject(),  uselection.getDataType());
	}
    
	public void setCoords(int x, int y){
		if (getDisplay().equalsIgnoreCase("STACK"))
			setStackCoords((int)x,(int)y);
		else
			setMontageCoords(x,y);
	}

	private void setCoords(Point3d TalCoords) {
		this.x = (int) TalCoords.getX();
		this.y = (int) TalCoords.getY();
		this.z = (int) TalCoords.getZ();
	}
    
	private void setMontageCoords(int x, int y) {
		//System.out.println("MONTAGE REQ FOR " + x + "\t" + y);
		coords.setTalFromMontage(x,y);
		setCoords(coords.getPosTal());
	}
    
	private void setStackCoords(int x, int y) {
		int currentSlice = ip.getCurrentSlice();
		coords.setTalFromStack(x,y,currentSlice);
		setCoords(coords.getPosTal());
		
	}
    
	public void setCoordBySlice(){
		int currentSlice = ip.getCurrentSlice();
		coords.setTalFromSlice(currentSlice);
		this.z = (int)coords.getPosTal().getZ();
	}
    
	public void setMessage(String descriptor, String message){
		if (getDisplay().equalsIgnoreCase("STACK") && stackWin != null){
			stackWin.setMessage(descriptor, message);
			stackWin.message();
			stackWin.repaint();
		}
		else if (getDisplay().equalsIgnoreCase("MONTAGE") && montageWin != null){
			montageWin.setMessage(descriptor, message);
			montageWin.message();
			montageWin.repaint();
		}
		//msgPanel.setMessage(descriptor+" " + message);
	}
    
    
    
	public void printCoords(){
		//format the string nicely: each dimension should get a four character column
		String message = "";
		
		String xs = Integer.toString(this.x);
		String ys = Integer.toString(this.y);
		String zs = Integer.toString(this.z);
		String pixelValue = ""+StringUtils.d2s(ipValue,1);
		int i;
		for (i=0;i<( 4 - xs.length() ); i++){
			message += " ";
		}
		message +=  xs + ",";
		for (i=0;i<( 4 - ys.length() ); i++){
			message += " ";
		}
		message +=  ys + ",";
		for (i=0;i<( 4 - zs.length() ); i++){
			message += " ";
		}
		message +=  zs + ":";	 
		for (i=0;i<( 4 - pixelValue.length() ); i++){
			message += " ";
		}
		message += pixelValue;
        
		if (mDisplay.getLayout().getName().equalsIgnoreCase("native"))
		   setMessage("Coordinates (Native): ", message);
	   else 
		   setMessage("Coordinates ("+ mDisplay.getLayout().getName().toUpperCase() + "):", message);
        
	}
    
	public  void adjustCoords(int x, int y, int z, float ipValue, boolean resetSlice, String fromView, boolean fromRadiologic) {
		if (getDisplay().equalsIgnoreCase("STACK")){
			coords.setTalFromTal(x,y,z); 
			Point3d winPos = coords.getPosWindow(getDisplay(), fromView, fromRadiologic);
			if (resetSlice){
				stackWin.showSlice((int)winPos.getZ());
			}
			setCoords(new Point3d(x,y,z));
			this.ipValue = ipValue;
			(stackWin).updateCrosshairs((int)winPos.getX(),(int)winPos.getY());
			printCoords();
			ip.updateAndDraw();
		}else if (getDisplay().equalsIgnoreCase("MONTAGE")) {
			this.ipValue= ipValue;
			printCoords();
		}
	}
    
	public void adjustRegions(int x, int y, int z,float ipValue, boolean resetSlice, String fromView, boolean fromRadiologic, int region) {
	}
    
	//listen for key events coming from image window...
	public void keyTyped(java.awt.event.KeyEvent e) {}
	public void keyReleased(java.awt.event.KeyEvent e) {}
	public void keyPressed(java.awt.event.KeyEvent e) {
        
		int keycode = e.getKeyCode();
		char keyChar = e.getKeyChar();
		if (Character.isDigit(keyChar)){
			rescale(Integer.parseInt(String.valueOf(keyChar)));
			int currentSlice = ip.getCurrentSlice();
			ip.setSlice(currentSlice);
			if (stackWin!=null)
				stackWin.setSelectorToSlice();
		} else if (keycode == e.VK_LEFT){
			if (stackWin != null){
				int currentSlice = ip.getCurrentSlice();
				ip.setSlice(currentSlice+1);
				this.setCoordBySlice();
				this.printCoords();
				this.syncEvent(true);
			}
		} else if (keycode == e.VK_RIGHT){
			if (stackWin != null){
				int currentSlice = ip.getCurrentSlice();
				ip.setSlice(currentSlice-1);
				this.setCoordBySlice();
				this.printCoords();
				this.syncEvent(true);
			}
		}
	}

	/**
	 * @param b
	 */
	public void setSelfClose(boolean b) {
		selfClose = b;
	}
	
	
	public void setMontageDisplay(MontageDisplay md) {
		mDisplay = md;
	}
	
	/**
	 * @return
	 */
	public Layout getLayout() {
		return mDisplay.getLayout();
	}

	public  void setWindowDimensions(PlexiImageFile pf) {
		winWidth = pf.getDimX();
		winHeight = pf.getDimY();
		slices=pf.getDimZ();
		int type=pf.getFileType();
		//System.out.println("MRIMAGE::setWIndowDimensions The image file type is " + type);
		String title=setTitle();
		if (type == FileInfo.GRAY8 || type== FileInfo.COLOR8|| type==FileInfo.BITMAP)
			ip= new ImagePlus(title,new ByteProcessor(winWidth, winHeight));
		else if (type == FileInfo.RGB||type== FileInfo.BGR || type==FileInfo.ARGB || type==FileInfo.RGB_PLANAR) 
			ip= new ImagePlus(title,new ColorProcessor(winWidth, winHeight));
		else if (type == FileInfo.GRAY16_SIGNED || type == FileInfo.GRAY16_UNSIGNED || type==FileInfo.GRAY12_UNSIGNED) 
			ip= new ImagePlus(title,new ShortProcessor(winWidth, winHeight));
		else
			ip= new ImagePlus(title,new FloatProcessor(winWidth, winHeight));
		ip.getProcessor().setColor(java.awt.Color.black);
		ip.getProcessor().fill();
		if (getDisplay()!=null && getDisplay().equalsIgnoreCase("Montage"))
			coords = new PlexiCoordinates(mDisplay.getOriginalWidth(),mDisplay.getOriginalHeight(), mDisplay.getOriginalStackSize(), mDisplay, getOrientation(), getRadiologic(), mDisplay.getNumberOfColumns());
		else 
			coords = new PlexiCoordinates(ip.getWidth(), ip.getHeight(), slices , mDisplay, getOrientation(), getRadiologic(), nColumns);
		coords.setScale(scaleFactor);
		this.uselection.setFile(pf);
		show();
		
	}
	
	public  void setWindowDimensions(int w, int h, int s) {
		winWidth = w;
		winHeight = h;
		slices=s;
	} 

	public String getOrientation() {
		return uselection.getOrientation();	
	}
	
	public Rectangle getWindowDimensions() {
		return new Rectangle(winWidth,winHeight);
	}
	
	public boolean isInitialized() {
		return isInit;
	}
	public void setImage(int index, ImageStack s) {
		
	}
	
	public void setImage(int index, ImagePlus s) {
		
	}
	
	public void updateCrossHair(){
	  if (getDisplay().equalsIgnoreCase("STACK")) {
		Point3d point = coords.getPosWindow(getDisplay(),getOrientation(),getRadiologic());
		stackWin.updateCrosshairs((int)point.x, (int)point.y);
	 }
	}

	public void updateCrossHair(String fromOrientation, int sliceNo){
		  if (getDisplay().equalsIgnoreCase("STACK") && !getOrientation().equalsIgnoreCase(fromOrientation)) {
				Point3d point = coords.getPosWindow(getDisplay(),getOrientation(),getRadiologic());
				int x = (int)point.x;
				int y = (int)point.y; 
				if (fromOrientation.equalsIgnoreCase("TRANSVERSE")) {
						y=sliceNo; 
				}else if (fromOrientation.equalsIgnoreCase("CORONAL")) {
					if (getOrientation().equalsIgnoreCase("TRANSVERSE")) {
						y= sliceNo;
					}else if (getOrientation().equalsIgnoreCase("SAGITTAL")) {
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
	
	public int getStackSize() {
		return slices;
	}
	
	public static void main(String[] args) {
		UserSelection u = new UserSelection();
		u.setSessionId("000115_92046");
		u.setDataType("TSE_Processed");
		u.setOrientation("CORONAL");
		u.setDisplay("Stack");
		PlexiImageFile pf = new PlexiImageFile();
		pf.setPath( "C:\\Mohana\\Temp\\arc003\\050721_dm1\\PROCESSED\\TSE\\T88_111");
		pf.setName("050721_dm1_tse7_t88_111_fill.4dfp.img");
		//pf.setCachePath("C:\\Mohana\\Temp\\LoRes\\Cache");
		u.setFile(pf);
		MRImage mr = new MRImage(u);
		//mr.open();		
	}
	
	public void handleOutOfMemoryError() {
			ip.flush();
			windowClosing();
			if (stackWin!=null) {
				stackWin.removeKeyListener(this);
				stackWin.close();
				stackWin.setVisible(false);
				stackWin.dispose();
			}else if (montageWin!=null) {
				montageWin.removeKeyListener(this);
				montageWin.close();
				montageWin.setVisible(false);
				montageWin.dispose();
			}
			System.out.println("Out of Memory Encountered");
			msgPanel.setMessage("Not enough memory! See Viewer Help to increase memory");
			//PlexiNoFrillsTextWindow tw = new PlexiNoFrillsTextWindow("Image Viewer Warning");
			//tw.show("Not enough memory available to show the image.\nPlease try increasing the memory (see Viewer Help on how to)");
	}
	
	/**
	 * @return
	 */
	public String getPathToHiResFile() {
		return pathToHiResFile;
	}

	/**
	 * @param string
	 */
	public void setPathToHiResFile(String string) {
		pathToHiResFile = string;
	}

}
 
 
 