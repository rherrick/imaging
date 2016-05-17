//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.ui;

import java.awt.*;
import java.io.*; 
import java.util.*;
import org.nrg.plexiviewer.lite.image.SerializableCalibration;
import ij.measure.*; 
import ij.process.*;
import ij.io.*;
 
public class LoadStatus implements Serializable {

	boolean success = false;
	Vector message;
	int nObjects = 0;
	Dimension dim;
	SerializableCalibration cal;
	int bytesPerPixel;
  
     
	public LoadStatus(boolean success, Vector message, int count, int w, int h, SerializableCalibration cal, int bPerPixel) {
		this.success = success;
		this.message = message;
		this.nObjects = count;
		this.dim = new Dimension(w,h);
		this.cal = cal;
		this.bytesPerPixel = bPerPixel;
	}
    
	public void setMessage(Vector message){
		this.message = message;
	}
    
	public Vector getMessage(){
		return message;
	}
    
	public void setCount(int count){
		nObjects = count;
	}
    
	public int getCount(){
		return nObjects;
	}
    
	public void setDimensions(Dimension d){
		this.dim = d;
	}
    
	public Dimension getDimensions(){
		return dim;
	}
    
	public void setSuccess(boolean sucess){
		this.success = success;
	}
	public boolean isSuccess(){
		return success;
	}

	public String toString() {
		String str = "";
		str += "Success: " + success + "\n";
		str += "Slices: " + getCount() + "\n";
		str += "Width: " + getDimensions().width;
		str += "Height: " + getDimensions().height;
		str += "Calibration: " + cal.toString();
		str += "Messages: \n"; 
		for (int i =0; i<getMessage().size();i++) {
			String message = (String) getMessage().elementAt(i);
			str += message + "\n";
		}
		return str;
	}

	public Calibration getCalibration() {
		Calibration rtn = null;
		if (cal!=null)
			rtn = cal.getCalibration();
		return rtn;	
	}
	/**
	 * @return
	 */
	public int getBytesPerPixel() {
		return bytesPerPixel;
	}

	/**
	 * @param i
	 */
	public void setBytesPerPixel(int i) {
		bytesPerPixel = i;
	}
	
	public ImageProcessor getProcessor(int width, int height) {
		ImageProcessor rtn = null; 
		if (getBytesPerPixel()==4)
			rtn = new FloatProcessor(width,height);
		else 
			rtn = new ByteProcessor(width,height);
		return rtn;		
	}
	
	public FileInfo getFileInfo() {
		FileInfo fi = new FileInfo();
		fi.fileType = (getBytesPerPixel()==4)?fi.GRAY32_FLOAT:fi.GRAY8;
		fi.fileFormat = fi.RAW;
		fi.width = dim.width;
		fi.height = dim.height;
		fi.nImages = getCount();
		fi.gapBetweenImages =0;
		fi.intelByteOrder = false;
		return fi;
	}

}
