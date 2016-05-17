//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.utils.Transform;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.awt.image.ColorModel;


public class TransformationTools {
    
	public static ImagePlus cropImage(ImagePlus inImg, String orientation) {
		int width = inImg.getWidth();
		int height = inImg.getHeight();
		Rectangle r = new Rectangle();
		if (orientation.equalsIgnoreCase("SAGITTAL") || orientation.equalsIgnoreCase("SAGITTALF")) {
			r.x=0;
			r.y=0;
			r.width = width;
			r.height = height - height/4;
		}else if (orientation.equalsIgnoreCase("CORONAL") || orientation.equalsIgnoreCase("CORONALF")) {
			r.x = width/6;
			r.y = height/14;
			r.width = 2*width/3; //w-w/6-w/4
			r.height = 2*height/3; 
		}else if (orientation.equalsIgnoreCase("TRANSVERSE") || orientation.equalsIgnoreCase("TRANSVERSEF")) {
			r.x = width/6;
			r.y = 0;
			r.width = 2*width/3; //w-w/6-w/4
			r.height = 2*height/3; 			
		}
		System.out.println(orientation + " Rectangle details  " + r.x + ", " + r.y + " w: " + r.width + " h: " + r.height);
		return crop(inImg, r);
	}
	
	public static ImagePlus crop(ImagePlus imp, Rectangle bounds) {
		ImageStack stack=imp.getStack();
		ColorModel cm=imp.createLut().getColorModel();
		int nslices=imp.getStackSize();
		ImageStack newstack=new ImageStack(bounds.width,bounds.height,cm);
		for (int n=1; n<=nslices;n++) {
			ImageProcessor sliceIP=stack.getProcessor(n);
			ImageProcessor newsliceIP=sliceIP.createProcessor(bounds.width,bounds.height);
			sliceIP.setRoi(bounds);
			ImageProcessor imtemp=sliceIP.crop();
			newsliceIP.insert(imtemp,0,0);
			newstack.addSlice(stack.getSliceLabel(n),newsliceIP);
		}
		
		ImagePlus newplus=new ImagePlus(imp.getTitle(),newstack);
        newplus.setFileInfo(newplus.getFileInfo());
		return newplus;
	}
	
	/* This method will create a new Image 
	 * whose startSlice = startSlice and endSlice = endSlice (both inclusive)
	 */
	public static ImagePlus dropSlices(ImagePlus inImage, int startSlice, int endSlice) {
		if (endSlice==-1) 
			endSlice = inImage.getStackSize();
		
		//Data input check
		if (startSlice<0 || startSlice > inImage.getStackSize() || startSlice >endSlice) {
			System.out.println("Invalid startSlice value (" + startSlice  + ") supplied. ");
			return inImage;
		}
		if (endSlice>inImage.getStackSize() || endSlice<startSlice || endSlice<0) {
			System.out.println("Invalid endSlice value (" + endSlice  + ") supplied. ");
			return inImage;
		}
			
		
		ImageStack stackNew = new ImageStack(inImage.getWidth(),inImage.getHeight());
		ImageStack stackOld = inImage.getStack();
		//Create the new stack
		for (int i=startSlice; i<=endSlice; i++){
			stackNew.addSlice("",stackOld.getPixels(i));
		}

		ImagePlus rtnImage =  new ImagePlus(inImage.getTitle(), stackNew);
		Calibration cal = inImage.getCalibration();
		rtnImage.setCalibration(cal);
		rtnImage.setFileInfo(rtnImage.getFileInfo());
		return rtnImage;
	}
}	
