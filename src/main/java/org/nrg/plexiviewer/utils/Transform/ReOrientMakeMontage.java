//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.utils.Transform;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.process.StackProcessor;

import java.util.Hashtable;

import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.display.MontageDisplay;
import org.nrg.plexiviewer.lite.xml.MontageInfo;
import org.nrg.plexiviewer.utils.ImageUtils;
import org.nrg.plexiviewer.utils.imageformats.DicomFileOpener;

public class ReOrientMakeMontage {
	
	UserSelection options;
	String format=null;
	MontageDisplay mDisplay;
	int nColumns, nRows;
	
	public ReOrientMakeMontage(UserSelection opt,String format, MontageDisplay mD) {
		options = opt;
		mDisplay = mD;
		this.format=format;
	}
	
	public ImagePlus doPerform(ImagePlus image) {
		if (image!=null) {
			FileInfo fi = image.getOriginalFileInfo();
			//System.out.println("ReOrientMakeMontage inImage File Type is " + image.getOriginalFileInfo().fileType);
			if (format!=null && format.startsWith("ANALYZE")) {
					if (image!=null) {
					   if (!DicomFileOpener.ORIENTATION_AS_ACQUIRED.equalsIgnoreCase(options.getOrientation())) {	
                       if (options.getOrientation().equalsIgnoreCase("sagittal") || !options.isRadiologic()) { 
    					   StackProcessor sp = new StackProcessor(image.getStack(), image.getProcessor());
    					   sp.flipHorizontal();
                       }
					   }
					}
                    if (options.getOrientation().equalsIgnoreCase("sagittal")){
                            image=new plexiViewerImageRelayer(options.getOrientation()).reverseStacks(image);
                    }    
			}
			if (options.getDisplay()!=null && options.getDisplay().equalsIgnoreCase("Montage")) {
					double aspect = 1;
                    PlexiMontageMaker mm = new PlexiMontageMaker();
                    mDisplay.setOriginalWidth(image.getWidth());
                    mDisplay.setOriginalHeight(image.getHeight());
                    mDisplay.setOriginalStackSize(image.getStackSize());
                    try {
                        int startSlice = mDisplay.getStartSlice();
    					int endSlice = mDisplay.getEndSlice();
    					int noSlices = ((endSlice - startSlice)) / mDisplay.getSliceSpacing() + 1;
    				    if (image.getStackSize() == 1) {
				        	nColumns = 1; nRows =1;
    				    }else {
    				    	nColumns = PlexiMontageMaker.getColumns(noSlices,aspect); 
    				    	nRows = (int)Math.ceil(noSlices /(float) nColumns);
    				    }
                        mDisplay.setNumberOfColumns(nColumns);
                        image= mm.makeMontage(image,nColumns,nRows,mDisplay.getScale(),startSlice,endSlice,mDisplay.getSliceSpacing(),false,false);
    					System.out.println("ReOrientMakeMontage::Image DImensions are " + image.getWidth() + " " + image.getHeight());
                    }catch(Exception e) {
                    	System.out.println("ReOrientMakeMontage::Unable to get slice information trying defaults");
                        Hashtable attribs = ImageUtils.getSliceIncrement(image);
                        image= mm.makeMontage(image,5,5,0.5,((Integer)attribs.get("startslice")).intValue(),((Integer)attribs.get("endslice")).intValue(),((Integer)attribs.get("increment")).intValue(),true,false);
                        mDisplay.setNumberOfColumns(5);
                        MontageInfo mInfo = new MontageInfo();
                        mInfo.setStartSlice(((Integer)attribs.get("startslice")).intValue());
                        mInfo.setEndSlice(((Integer)attribs.get("endslice")).intValue());
                        mInfo.setSliceSpacing(((Integer)attribs.get("increment")).intValue());
                        mDisplay.setMInfo(mInfo);
                    }
			}
			image.setFileInfo(fi);
			System.out.println("ReOrientMakeMontage outImage File Type is " + image.getOriginalFileInfo().fileType);
		}
		return image;
	}
	
	public int getNumberOfColumns() {
		return nColumns;
	}
	
	public int getNumberOfRows() {
		return nRows;
	}
}
