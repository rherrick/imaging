//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.utils.Transform;

import ij.*;
import ij.process.*;

public class PlexiReOrient {
		public static ImagePlus reOrient(ImagePlus inImg, String inOrientation, String outOrientation, String[] postProcess, boolean goCount, boolean showProgress) {
			String beginFrom=null;
			ImagePlus rtn = inImg;

			if (inOrientation!=null) {
				if (inOrientation.equalsIgnoreCase("Sagittal")) {
					if (outOrientation.equalsIgnoreCase("Transverse")) {
						beginFrom = "BOTTOM";
					}else if (outOrientation.equalsIgnoreCase("Coronal")) {
						beginFrom="LEFT";
					}else 
						beginFrom=null;
				}else if (inOrientation.equalsIgnoreCase("Transverse")) {
					if (outOrientation.equalsIgnoreCase("Sagittal")) {
						beginFrom = "LEFT";
					}else if (outOrientation.equalsIgnoreCase("Coronal")) {
						beginFrom="TOP";
					}else 
						beginFrom=null;
				}else if (inOrientation.equalsIgnoreCase("Coronal")) {
					if (outOrientation.equalsIgnoreCase("Transverse")) {
						beginFrom = "BOTTOM";
					}else if (outOrientation.equalsIgnoreCase("Sagittal")) {
						beginFrom="LEFT";
					}else 
						beginFrom=null;
				}
				if (beginFrom!=null) {
					rtn = new PlexiSlicer().doSlice(inImg, beginFrom, goCount, showProgress);
					if (postProcess!=null) {
						for (int i=0; i<postProcess.length;i++) {
							doPerform(rtn,postProcess[i]);
						}
					}
				}	
				
			}	
			//System.out.println("Image has been reoriented");	
			return rtn;	
		}

	private static void doPerform(ImagePlus rtn, String process ) {
		if (process!=null && rtn!=null) {
			StackProcessor sp = new StackProcessor(rtn.getStack(), rtn.getProcessor());
			if (process.equalsIgnoreCase("RotateRight")) {
				rtn.setStack(rtn.getTitle(),sp.rotateRight()); 
			}else if (process.equalsIgnoreCase("RotateLeft")) {
				rtn.setStack(rtn.getTitle(),sp.rotateLeft()); 
			}else if (process.equalsIgnoreCase("flipHorizontal")) {
				sp.flipHorizontal();			
			}else if (process.equalsIgnoreCase("flipVertical")) {
				sp.flipVertical();			
			}
		}
	}

	public static ImagePlus reOrient(ImagePlus inImg, String inOrientation, String outOrientation, boolean goCount, boolean showProgress) {
		String beginFrom=null;
		ImagePlus rtn = inImg;

		if (inOrientation!=null) {
			if (inOrientation.equalsIgnoreCase("Sagittal")) {
				if (outOrientation.equalsIgnoreCase("Transverse")) {
					beginFrom = "BOTTOM";
				}else if (outOrientation.equalsIgnoreCase("Coronal")) {
					beginFrom="LEFT";
				}else 
					beginFrom=null;
			}else if (inOrientation.equalsIgnoreCase("Transverse")) {
				if (outOrientation.equalsIgnoreCase("Sagittal")) {
					beginFrom = "LEFT";
				}else if (outOrientation.equalsIgnoreCase("Coronal")) {
					beginFrom="TOP";
				}else 
					beginFrom=null;
			}else if (inOrientation.equalsIgnoreCase("Coronal")) {
				if (outOrientation.equalsIgnoreCase("Transverse")) {
					beginFrom = "BOTTOM";
				}else if (outOrientation.equalsIgnoreCase("Sagittal")) {
					beginFrom="LEFT";
				}else 
					beginFrom=null;
			}
			if (beginFrom!=null) 
				rtn = new PlexiSlicer().doSlice(inImg, beginFrom, goCount, showProgress);
		}		
		return rtn;	
	}

}
