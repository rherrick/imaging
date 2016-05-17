//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.io;

import ij.*;
import ij.process.*;

public class plexiViewerImageRelayer {
	String orientation;	
	String fileName, dir;
	
	public plexiViewerImageRelayer(String orientation) {
		this.orientation = orientation;
	}

	private ImageStack reverseStack(ImageStack stack, ImagePlus imp) {
			int n;
			ImageStack stack2 = imp.createEmptyStack();
			while ((n=stack.getSize())>0) { 
				stack2.addSlice(stack.getSliceLabel(n), stack.getProcessor(n));
				stack.deleteLastSlice();
			}
			return stack2;
	   }

	public ImagePlus reverseStacks(ImagePlus image) {
		ImageStack stackNew = reverseStack(image.getStack(),image);
		ImagePlus tIP =  new ImagePlus(image.getTitle(), stackNew);
		tIP.setFileInfo(image.getFileInfo());
		return tIP;		
	}

    
    public ImagePlus FormatDicomImageForImageJ(ImagePlus image) {
        return this.Format4dfpImageForImageJ(image);
    }
	public ImagePlus Format4dfpImageForImageJ(ImagePlus image) {
		ImagePlus rtn=null;
		//System.out.println("Image Ori is " + getOrientation());
		if (getOrientation().equalsIgnoreCase("TRANSVERSE")) {
			StackProcessor sp = new StackProcessor(image.getStack(), image.getProcessor()); 
			sp.flipVertical();
			rtn= image;
		}else if (getOrientation().equalsIgnoreCase("CORONAL")) {
			StackProcessor sp = new StackProcessor(image.getStack(), image.getProcessor());
			sp.flipHorizontal();
			rtn= reverseStacks(image);
		}else if (getOrientation().equalsIgnoreCase("SAGITTAL")) {
			StackProcessor sp = new StackProcessor(image.getStack(), image.getProcessor());
			sp.flipHorizontal();
			sp.flipVertical();
			rtn= reverseStacks(image);
		}
		return rtn;
	}

	public ImagePlus FormatAnalyzeImageForViewer(ImagePlus image) {
		ImagePlus rtn=image;
		StackProcessor sp = new StackProcessor(rtn.getStack(), rtn.getProcessor());
		if (getOrientation().equalsIgnoreCase("TRANSVERSEF") || getOrientation().equalsIgnoreCase("CORONALF") || getOrientation().equalsIgnoreCase("SAGITTALF")) {
			System.out.println("Found Flipped..." + getOrientation() + "..Flipping image back");
			sp.flipVertical();
			orientation = orientation.substring(0,orientation.length()-1);
		}else if (getOrientation().equalsIgnoreCase("TRANSVERSE") || getOrientation().equalsIgnoreCase("CORONAL") || getOrientation().equalsIgnoreCase("SAGITTAL")) {
			sp.flipVertical();
			sp.flipHorizontal();
		}	
		//System.out.println("Analyze Image Ori is " + getOrientation());
		if (getOrientation().equalsIgnoreCase("CORONAL") || getOrientation().equalsIgnoreCase("SAGITTAL")) {
			//rtn= reverseStacks(rtn);
		}
		return rtn;
	}
	
	public String getOrientation() {
		return orientation; 
	}
	

	
}
