/*
 * org.nrg.plexiViewer.utils.ImageUtils
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.utils;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.measure.Calibration;

import java.util.Hashtable;

/**
 * @author Mohana
 *
 */
public class ImageUtils {

	public static int getWidth(int width, int height, int stackSize, String inOrientation, String outOrientation) {
		int rtn = width;
		System.out.println("ImageUtils::getWidth " + width + " " + height + " " + stackSize + " " + inOrientation + " "+ outOrientation);
		if (inOrientation!=null && outOrientation!=null)
		 if (!inOrientation.equalsIgnoreCase(outOrientation)) {
		 	if (inOrientation.equalsIgnoreCase("TRANSVERSE") || inOrientation.equalsIgnoreCase("TRANSVERSEF")) {
		 		if (outOrientation.equalsIgnoreCase("SAGITTAL") || outOrientation.equalsIgnoreCase("SAGITTALF")) {
		 			rtn=height;
		 		}else if (outOrientation.equalsIgnoreCase("CORONAL") || outOrientation.equalsIgnoreCase("CORONALF")) {
					rtn=width;
				}
		 	}else if (inOrientation.equalsIgnoreCase("CORONAL") || inOrientation.equalsIgnoreCase("CORONALF")) {
				if (outOrientation.equalsIgnoreCase("SAGITTAL") || outOrientation.equalsIgnoreCase("SAGITTALF")) {
					rtn=stackSize;
				}else if (outOrientation.equalsIgnoreCase("TRANSVERSE") || outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
					rtn=width;
				}
			}else if (inOrientation.equalsIgnoreCase("SAGITTAL") || inOrientation.equalsIgnoreCase("SAGITTALF")) {
				if (outOrientation.equalsIgnoreCase("CORONAL") || outOrientation.equalsIgnoreCase("CORONALF")) {
					rtn=stackSize;
				}else if (outOrientation.equalsIgnoreCase("TRANSVERSE") || outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
					rtn=stackSize;
				}
			}
		 }
		 System.out.println("ImageUtils::getWidth::returning " + rtn);
		 return rtn;
	}

	public static int getHeight(int width, int height, int stackSize, String inOrientation, String outOrientation) {
		int rtn = height;
		if (inOrientation!=null && outOrientation!=null)
		 if (!inOrientation.equalsIgnoreCase(outOrientation)) {
			if (inOrientation.equalsIgnoreCase("TRANSVERSE") || inOrientation.equalsIgnoreCase("TRANSVERSEF")) {
				if (outOrientation.equalsIgnoreCase("SAGITTAL") || outOrientation.equalsIgnoreCase("SAGITTALF")) {
					rtn=stackSize;
				}else if (outOrientation.equalsIgnoreCase("CORONAL") || outOrientation.equalsIgnoreCase("CORONALF")) {
					rtn=stackSize;
				}
			}else if (inOrientation.equalsIgnoreCase("CORONAL") || inOrientation.equalsIgnoreCase("CORONALF")) {
				if (outOrientation.equalsIgnoreCase("SAGITTAL") || outOrientation.equalsIgnoreCase("SAGITTALF")) {
					rtn=height;
				}else if (outOrientation.equalsIgnoreCase("TRANSVERSE") || outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
					rtn=stackSize;
				}
			}else if (inOrientation.equalsIgnoreCase("SAGITTAL") || inOrientation.equalsIgnoreCase("SAGITTALF")) {
				if (outOrientation.equalsIgnoreCase("CORONAL") || outOrientation.equalsIgnoreCase("CORONALF")) {
					rtn=height;
				}else if (outOrientation.equalsIgnoreCase("TRANSVERSE") || outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
					rtn=width;
				}
			}
		 }
		 return rtn;

	}

	public static int getStackSize(int width, int height, int stackSize, String inOrientation, String outOrientation) {
		int rtn = stackSize;
		if (inOrientation!=null && outOrientation!=null)
		 if (!inOrientation.equalsIgnoreCase(outOrientation)) {
			if (inOrientation.equalsIgnoreCase("TRANSVERSE") || inOrientation.equalsIgnoreCase("TRANSVERSEF")) {
				if (outOrientation.equalsIgnoreCase("SAGITTAL") || outOrientation.equalsIgnoreCase("SAGITTALF")) {
					rtn=width;
				}else if (outOrientation.equalsIgnoreCase("CORONAL") || outOrientation.equalsIgnoreCase("CORONALF")) {
					rtn=height;
				}
			}else if (inOrientation.equalsIgnoreCase("CORONAL") || inOrientation.equalsIgnoreCase("CORONALF")) {
				if (outOrientation.equalsIgnoreCase("SAGITTAL") || outOrientation.equalsIgnoreCase("SAGITTALF")) {
					rtn=width;
				}else if (outOrientation.equalsIgnoreCase("TRANSVERSE") || outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
					rtn=height;
				}
			}else if (inOrientation.equalsIgnoreCase("SAGITTAL") || inOrientation.equalsIgnoreCase("SAGITTALF")) {
				if (outOrientation.equalsIgnoreCase("CORONAL") || outOrientation.equalsIgnoreCase("CORONALF")) {
					rtn=width;
				}else if (outOrientation.equalsIgnoreCase("TRANSVERSE") || outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
					rtn=height;
				}
			}
		 }
		 return rtn;
	}


    public static Hashtable getSliceIncrement(ImagePlus img) {
        return getSliceIncrement(img, 25);
    }

    public static Hashtable getSliceIncrement(int imgStackSize) {
        return getSliceIncrement(imgStackSize,25);
    }
    public static Hashtable getSliceIncrement(ImagePlus img, int totalSlices) {
        int stacksize = img.getImageStackSize();
        return getSliceIncrement(stacksize,totalSlices);
    }

     public static Hashtable getSliceIncrement(int stacksize, int totalSlices) {
         Hashtable rtn = new Hashtable();
         final String STARTSLICE = "startslice";
         final String ENDSLICE = "endslice";
         final String INCREMENT = "increment";
         int startslice = 1, endslice = stacksize, increment = 1;

         if (stacksize > totalSlices) {
             increment = (int)Math.floor((double)(stacksize-totalSlices)/((double)(totalSlices)));
             if (increment < 1) {
                 increment = 1;
             }else if (increment > 1){
                 int extra = stacksize/increment;
             	startslice = (int)extra/2;
             	int cnt = 1;
             	endslice = startslice;
             	while (cnt < totalSlices) {
             		endslice  += increment;
             		cnt++;
             	}
                 if (startslice < 1) startslice = 1;
             }
             if (increment == 1){
                 int extra = stacksize - totalSlices;
             	if (extra == 1) {
             		startslice = 2;
             	}else if (extra == 2) {
             		startslice = 2;
             		endslice = stacksize - 1;
             	}else if (extra==3) {
             	     startslice = 3;
             	     endslice = stacksize - 1;
             	}else {
             		startslice +=((int)extra/2) ;
             		endslice = stacksize - (extra - ((int)extra/2));
             	}

                 if (startslice < 1) {
                 	startslice = 1;
                 	endslice = stacksize - 1;
                 }
             }
         }
         rtn.put(STARTSLICE,new Integer(startslice));
         rtn.put(ENDSLICE,new Integer(endslice));
         rtn.put(INCREMENT,new Integer(increment));
         return rtn;
     }


    public static FileInfo getFileInfo(Calibration cal, int w, int h, int s) {
        FileInfo fi = new FileInfo();
        fi.fileType = FileInfo.GRAY8;
        fi.width = w;
        fi.height = h;
        fi.nImages = s;
        fi.pixelWidth = cal.pixelWidth;
        fi.pixelHeight = cal.pixelHeight;
        fi.pixelDepth = cal.pixelDepth;
        return fi;
    }


}
