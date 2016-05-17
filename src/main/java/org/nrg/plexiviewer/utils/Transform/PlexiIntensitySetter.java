//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.utils.Transform;

import ij.*;
import ij.process.*;

import org.nrg.plexiviewer.io.PlexiFileOpener;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.xml.HiRes;
import org.nrg.plexiviewer.utils.PlexiConstants;

public class PlexiIntensitySetter {
	boolean reset=false;
	double min, max;
    
	public PlexiIntensitySetter(HiRes hRes) {
		if (hRes.getMaxIntensity()!=null) {
		    max = hRes.getMaxIntensity().floatValue();
			reset=true;
		}
		if (hRes.getMinIntensity()!=null) {
			min=hRes.getMinIntensity().floatValue();
			reset=true;
		}
		System.out.println("PlexiIntensitySetter::reset " + reset);
		System.out.println("Desired intensity range Min " + min + " max " + max);
	}

    public PlexiIntensitySetter(float minIntensity, float maxIntensity) {
        min = minIntensity;
        max = maxIntensity;
        reset=true;
        System.out.println("PlexiIntensitySetter::reset " + reset);
        System.out.println("Desired intensity range Min " + min + " max " + max);
    }
    
	public void setIntensities(ImagePlus image, boolean entireStack) {
		if (reset==false){
			return;
		}
		 if (image.getType()!=ImagePlus.GRAY8) {
			    System.out.println("Cannot set intensities:: Image not of GRAY8");
			 	return;
		 } 	 
		ImageProcessor ip = image.getProcessor();
		ip.setRoi(image.getRoi());
		int[] table = new int[256];
        for (int i=0; i<256; i++) {
            if (i<=min)
                table[i] = 0;
            else if (i>=max)
                table[i] = 255;
            else
                table[i] = (int)(((double)(i-min)/(max-min))*255);
            //System.out.println("Table " + i + " val = " + table[i] );
        }
        if (image.getStackSize()>1) {
        	if (entireStack) {
	        	ImageProcessor mask = image.getMask();
			    for (int i=1; i<=image.getStackSize(); i++) {
		                image.setSlice(i);
		                ip = image.getProcessor();
		                if (mask!=null) ip.snapshot();
		                ip.applyTable(table);
		                ip.reset(mask);
		        }
        	}else {
        		 if (ip.getMask()!=null) ip.snapshot();
                 ip.applyTable(table);
                 ip.reset(ip.getMask());
        	}
        } else {
            if (ip.getMask()!=null) ip.snapshot();
            ip.applyTable(table);
            ip.reset(ip.getMask());
        }
        reset(image, ip);
        image.changes=true;
        table=null;
	}
	
	void reset(ImagePlus imp, ImageProcessor ip) {
        if ((ip instanceof ShortProcessor) || (ip instanceof FloatProcessor)) {
            ip.resetMinAndMax();
        }
        double ipmin = ip.getMin();
        double ipmax = ip.getMax();
        //System.out.println("IP Min and Max are " + ipmin + " " + ipmax);
        ip.setMinAndMax(ipmin, ipmax);
    }
	
	 
    public static void main(String args[]) {
        try {
    	PlexiImageFile pf = new PlexiImageFile();
        pf.setURIAsString("file:/C:/data/cache/FTest2/061121_tc22923/061121_tc22923_mpr_n2_111_t88_gfc.4dfp_8bit_tra");
        pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCE);
        PlexiFileOpener pfo = new PlexiFileOpener("ANALYZE",pf);
        
        ImagePlus img = pfo.getImagePlus();
        PlexiIntensitySetter i = new PlexiIntensitySetter(0,3);
        i.setIntensities(img,true);
        img.show();
        }catch(Exception e) {e.printStackTrace();}
    }
}
