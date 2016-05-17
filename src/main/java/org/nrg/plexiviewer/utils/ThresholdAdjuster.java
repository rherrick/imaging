/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.utils;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class ThresholdAdjuster {
    Double min, max;
    
    public ThresholdAdjuster(Double min, Double max) {
        this.min = min;
        this.max = max;
    }
    
    public ImagePlus applyThresold(ImagePlus img) {
        //img.getProcessor().setThreshold(min,max,)
        int rx, ry, rw, rh;
        int width, height;
        width = img.getProcessor().getWidth();
        height = img.getProcessor().getHeight();
        rx = 0;
        ry = 0;
        rw = width;
        rh = height;
        ImageStack thresholdedStack = new ImageStack(width,height);
        for (int i = 1; i <= img.getStackSize(); i++) {
            img.setSlice(i);
            ImageProcessor ip = img.getProcessor();
            ImageProcessor ip2 = ip.createProcessor(width,height);
            for (int y=ry; y<(ry+rh); y++ ) {
                for (int x=rx; x<(rx+rw); x++) {
                    double v = ip.getPixel(x,y);
                    /*ip2.putPixelValue(x,y,v);
                    if (min != null ){
                        if (v > min.doubleValue()) {
                            ip2.putPixelValue(x,y,1);
                        }
                    }
                    if (max != null ) {
                        if (v > max.doubleValue()) ip2.putPixelValue(x,y,0);
                    }*/
                    
                }
            }
            thresholdedStack.addSlice(img.getStack().getSliceLabel(i),ip2);
        }
        return new ImagePlus(img.getTitle(),thresholdedStack);
    }
    
 /*   public static void main(String args[]) {
        try {
            ImageResource imgRsc = ImageResource.Factory.newInstance();
            imgRsc.setFormat("IFH");
            imgRsc.setURI("Y:/data2/WORK/PIPELINE_TEST/PIB280_mpr_at_222.4dfp.img");
            PlexiImageFile pf = IOHelper.getPlexiImageFileFromImageResource(imgRsc);
            ImagePlus img = PlexiFileOpener.openBaseFile(pf, false);
            //ThresholdAdjuster tAdj = new ThresholdAdjuster(new Double(150.0),new Double(1400.0));
            //tAdj.applyThresold(img).show();
            img.getProcessor().setMinAndMax(150,1400);
            img.show();
        }catch(Exception e) {
            e.printStackTrace();
        }
    } */
}
