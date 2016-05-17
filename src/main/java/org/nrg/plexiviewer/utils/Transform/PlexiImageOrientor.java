//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.utils.Transform;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.process.StackProcessor;

public class PlexiImageOrientor {
		private int width, height, stackSize;
		private ImagePlus inImg;
		private int owidth, oheight, oStackSize;
		private String format;
        public static final String AS_ACQUIRED_TXT = "As Acquired    ";
        public static final String AS_ACQUIRED="ASACQUIRED";
        public static final String AS_ACQUIRED_ORIENTATION = "as acquired";

		public PlexiImageOrientor(ImagePlus inImg) {
			this.inImg  = inImg;
			width = inImg.getWidth();
			height = inImg.getHeight();
			stackSize = inImg.getStackSize();
			format=null;
		}

		public PlexiImageOrientor(ImagePlus inImg, String format) {
			this.inImg  = inImg;
			width = inImg.getWidth();
			height = inImg.getHeight();
			stackSize = inImg.getStackSize();
			this.format=format;
		}

		
		private void init(String inOrientation, String outOrientation) {
			if (inOrientation!=null) {
                if (outOrientation.toUpperCase().equals(AS_ACQUIRED) || outOrientation.toUpperCase().equals(AS_ACQUIRED +"F")) {
                    owidth = width;
                    oheight = height;
                    oStackSize = stackSize;
                }else {
    				if (inOrientation.equalsIgnoreCase("Sagittal") || inOrientation.equalsIgnoreCase("SagittalF")) {
    					if (outOrientation.equalsIgnoreCase("Transverse") || outOrientation.equalsIgnoreCase("TransverseF")) {
    						owidth = stackSize;
    						oheight = width;
    						oStackSize = height;
    					}else if (outOrientation.equalsIgnoreCase("Coronal") || outOrientation.equalsIgnoreCase("CoronalF")) {
    						owidth = stackSize;
    						oheight = height;
    						oStackSize = width;
    					}
    				}else if (inOrientation.equalsIgnoreCase("Transverse") || inOrientation.equalsIgnoreCase("TransverseF")) {
    					if (outOrientation.equalsIgnoreCase("Sagittal") || outOrientation.equalsIgnoreCase("SagittalF")) {
    						owidth = height;
    						oheight = stackSize;
    						oStackSize = width;
    					}else if (outOrientation.equalsIgnoreCase("Coronal") || outOrientation.equalsIgnoreCase("CoronalF")) {
    						owidth = width;
    						oheight = stackSize;
    						oStackSize = height;
    					}
    				}else if (inOrientation.equalsIgnoreCase("Coronal") || inOrientation.equalsIgnoreCase("CoronalF")) {
    					if (outOrientation.equalsIgnoreCase("Transverse") || outOrientation.equalsIgnoreCase("TransverseF")) {
    						owidth = width;
    						oheight = stackSize;
    						oStackSize = height;
    					}else if (outOrientation.equalsIgnoreCase("Sagittal") || outOrientation.equalsIgnoreCase("SagittalF")) {
    						owidth = stackSize;
    						oheight = height;
    						oStackSize = width;
    					}
    				}
    			}	
            }
		}

        private Calibration getCalibration(String inOrientation, String outOrientation) {
            Calibration cal = inImg.getCalibration();
            if (inOrientation!=null) {
                if (outOrientation.toUpperCase().equals(AS_ACQUIRED)) {
                    return cal;
                }
                if (inOrientation.equalsIgnoreCase("Sagittal") || inOrientation.equalsIgnoreCase("SagittalF")) {
                    if (outOrientation.equalsIgnoreCase("Transverse") || outOrientation.equalsIgnoreCase("TransverseF")) {
                        cal.pixelWidth = inImg.getCalibration().pixelDepth;
                        cal.pixelHeight = inImg.getCalibration().pixelWidth;
                        cal.pixelDepth = inImg.getCalibration().pixelHeight;
                    }else if (outOrientation.equalsIgnoreCase("Coronal") || outOrientation.equalsIgnoreCase("CoronalF")) {
                        cal.pixelWidth = inImg.getCalibration().pixelDepth;
                        cal.pixelHeight = inImg.getCalibration().pixelHeight;
                        cal.pixelDepth = inImg.getCalibration().pixelWidth;
                    }
                }else if (inOrientation.equalsIgnoreCase("Transverse") || inOrientation.equalsIgnoreCase("TransverseF")) {
                    if (outOrientation.equalsIgnoreCase("Sagittal") || outOrientation.equalsIgnoreCase("SagittalF")) {
                        cal.pixelWidth = inImg.getCalibration().pixelHeight;
                        cal.pixelHeight = inImg.getCalibration().pixelDepth;
                        cal.pixelDepth = inImg.getCalibration().pixelWidth;
                    }else if (outOrientation.equalsIgnoreCase("Coronal") || outOrientation.equalsIgnoreCase("CoronalF")) {
                        cal.pixelWidth = inImg.getCalibration().pixelWidth;
                        cal.pixelHeight = inImg.getCalibration().pixelDepth;
                        cal.pixelDepth = inImg.getCalibration().pixelHeight;
                    }
                }else if (inOrientation.equalsIgnoreCase("Coronal") || inOrientation.equalsIgnoreCase("CoronalF")) {
                    if (outOrientation.equalsIgnoreCase("Transverse") || outOrientation.equalsIgnoreCase("TransverseF")) {
                        cal.pixelWidth = inImg.getCalibration().pixelWidth;
                        cal.pixelHeight = inImg.getCalibration().pixelDepth;
                        cal.pixelDepth = inImg.getCalibration().pixelHeight;
                    }else if (outOrientation.equalsIgnoreCase("Sagittal") || outOrientation.equalsIgnoreCase("SagittalF")) {
                        cal.pixelWidth = inImg.getCalibration().pixelDepth;
                        cal.pixelHeight = inImg.getCalibration().pixelHeight;
                        cal.pixelDepth = inImg.getCalibration().pixelWidth;
                    }
                }
            } 
            return cal;
        }

        
        public ImagePlus getImage(String inOrientation, String outOrientation) {
           // System.out.println("PlexiImageOrientor::getImage " + inOrientation + " " + outOrientation);
       if (inOrientation.equalsIgnoreCase(PlexiImageOrientor.AS_ACQUIRED_ORIENTATION +"F") || outOrientation.equalsIgnoreCase(PlexiImageOrientor.AS_ACQUIRED_ORIENTATION + "F") || inOrientation.equalsIgnoreCase(PlexiImageOrientor.AS_ACQUIRED_ORIENTATION) || outOrientation.equalsIgnoreCase(PlexiImageOrientor.AS_ACQUIRED_ORIENTATION))
    			return inImg;
       // if (outOrientation.toUpperCase().equals(AS_ACQUIRED) || outOrientation.toUpperCase().equals(AS_ACQUIRED+"F"))
       //     return inImg;
		if (inOrientation.equalsIgnoreCase(outOrientation))
			return inImg;
		if (outOrientation.substring(0,outOrientation.length()-1).equalsIgnoreCase(inOrientation)) {
			StackProcessor sp = new StackProcessor(inImg.getStack(), inImg.getProcessor());
			sp.flipVertical();
			return inImg;					
		}else if (inOrientation.substring(0,inOrientation.length()-1).equalsIgnoreCase(outOrientation)) {
			StackProcessor sp = new StackProcessor(inImg.getStack(), inImg.getProcessor());
			sp.flipVertical();
			return inImg;					
		}
		ImageProcessor ip,ip2  = null;
		ImagePlus oip = new ImagePlus();
		init(inOrientation,outOrientation);
        //System.out.println("PlexiImageOrientor init called " + format );
		if (format!=null && format.startsWith("ANALYZE")) {
			if (inOrientation!=null && !inOrientation.endsWith("F")) {
				StackProcessor sp = new StackProcessor(inImg.getStack(), inImg.getProcessor());
				sp.flipVertical();
				inOrientation+="F";
			}
		}
		ImageStack inStack = inImg.getStack();
		ImageStack ostack = new ImageStack(owidth,oheight);
        for (int z=0;z<oStackSize;z++) {
			ip2 = inStack.getProcessor(1).createProcessor(owidth,oheight); 
			createSlice(inOrientation, outOrientation,ip2,z);
			ostack.addSlice("",ip2,z);	
		}
        
		oip.setStack("",ostack);
		oip.setCalibration(getCalibration(inOrientation,outOrientation));
		oip.setFileInfo(inImg.getFileInfo());
        oip=correctFlips(inOrientation,outOrientation,oip);
        return oip;
	}

	private ImagePlus correctFlips(String inOrientation, String outOrientation, ImagePlus oip) {
		if (inOrientation.equalsIgnoreCase("CORONALF") ) {
			if (outOrientation.equalsIgnoreCase("TRANSVERSE") || outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
				oip=new plexiViewerImageRelayer(inOrientation).reverseStacks(oip);
				if (outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
					StackProcessor sp = new StackProcessor(oip.getStack(), oip.getProcessor());
					sp.flipVertical();					
				}
			}else if (outOrientation.equalsIgnoreCase("SAGITTAL")) {
				StackProcessor sp = new StackProcessor(oip.getStack(), oip.getProcessor());
				sp.flipVertical();					
			}
		}else if (inOrientation.equalsIgnoreCase("SAGITTALF") ) {
			if (outOrientation.equalsIgnoreCase("TRANSVERSE") || outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
				oip=new plexiViewerImageRelayer(inOrientation).reverseStacks(oip);
				if (outOrientation.equalsIgnoreCase("TRANSVERSEF")) {
					StackProcessor sp = new StackProcessor(oip.getStack(), oip.getProcessor());
					sp.flipVertical();					
				}
			}else if (outOrientation.equalsIgnoreCase("CORONAL")) {
				StackProcessor sp = new StackProcessor(oip.getStack(), oip.getProcessor());
				sp.flipVertical();					
			}
		}else if (inOrientation.equalsIgnoreCase("TRANSVERSEF") ) {
			if (outOrientation.equalsIgnoreCase("CORONAL") || outOrientation.equalsIgnoreCase("CORONALF")) {
				oip=new plexiViewerImageRelayer(inOrientation).reverseStacks(oip);
				if (outOrientation.equalsIgnoreCase("CORONAL")) {
					StackProcessor sp = new StackProcessor(oip.getStack(), oip.getProcessor());
					sp.flipVertical();				
				}
			}else if (outOrientation.equalsIgnoreCase("SAGITTAL")) {
				StackProcessor sp = new StackProcessor(oip.getStack(), oip.getProcessor());
				sp.flipHorizontal();
				sp.flipVertical();					
			}else if (outOrientation.equalsIgnoreCase("SAGITTALF")) {
				StackProcessor sp = new StackProcessor(oip.getStack(), oip.getProcessor());
				sp.flipHorizontal();
			}	
		}	 
		return oip;
	}
		
	private void createSlice(String inOrientation, String outOrientation,ImageProcessor ip2, int z) {
		if (inOrientation.equalsIgnoreCase("Sagittal") || inOrientation.equalsIgnoreCase("SagittalF")) {
			if (outOrientation.equalsIgnoreCase("Transverse") || outOrientation.equalsIgnoreCase("TransverseF")) {
					createSliceSag2Tra(ip2,z);
			}else if (outOrientation.equalsIgnoreCase("Coronal") || outOrientation.equalsIgnoreCase("CoronalF")) {
					createSliceCor2Sag(ip2,z);
			}
		}else if (inOrientation.equalsIgnoreCase("Transverse") || inOrientation.equalsIgnoreCase("TransverseF")) {
			if (outOrientation.equalsIgnoreCase("Sagittal") || outOrientation.equalsIgnoreCase("SagittalF")) {
				createSliceTra2Sag(ip2,z);
			}else if (outOrientation.equalsIgnoreCase("Coronal") || outOrientation.equalsIgnoreCase("CoronalF")) {
				createSliceTra2Cor(ip2,z);
			}
		}else if (inOrientation.equalsIgnoreCase("Coronal") || inOrientation.equalsIgnoreCase("CoronalF")) {
			if (outOrientation.equalsIgnoreCase("Transverse") || outOrientation.equalsIgnoreCase("TransverseF")) {
				createSliceCor2Tra(ip2,z);
			}else if (outOrientation.equalsIgnoreCase("Sagittal") || outOrientation.equalsIgnoreCase("SagittalF")) {
				createSliceCor2Sag(ip2,z);
			}
		}
	}
		
		private void createSliceCor2Sag(ImageProcessor ip2, int x) {
			for (int z=0;z<stackSize;z++) {
				ImageProcessor ip = inImg.getStack().getProcessor(z+1);
				for (int y=0;y<height;y++) {
					ip2.putPixel(z,y,ip.getPixel(x,y));					
				}					
			}
		}

		private void createSliceCor2Tra(ImageProcessor ip2, int y) {
			for (int z=0;z<stackSize;z++) {
				ImageProcessor ip = inImg.getStack().getProcessor(z+1);
				for (int x=0;x<width;x++) {
					ip2.putPixel(x,z,ip.getPixel(x,y));					
				}					
			}
		}

		private void createSliceSag2Tra(ImageProcessor ip2, int y) {
			for (int z=0;z<stackSize;z++) {
				ImageProcessor ip = inImg.getStack().getProcessor(z+1);
				for (int x=0;x<width;x++) {
					ip2.putPixel(z,x,ip.getPixel(x,y));					
				}					
			}
		}

		private void createSliceTra2Sag(ImageProcessor ip2, int x) {
			for (int z=0;z<stackSize;z++) {
				ImageProcessor ip = inImg.getStack().getProcessor(z+1);
				for (int y=0;y<height;y++) {
					ip2.putPixel(y,oheight-1-z,ip.getPixel(x,y));					
				}					
			}
		}

		private void createSliceTra2Cor(ImageProcessor ip2, int y) {
			for (int z=0;z<stackSize;z++) {
				ImageProcessor ip = inImg.getStack().getProcessor(z+1);
				for (int x=0;x<width;x++) {
					//System.out.println(oheight-z);
					ip2.putPixel(x,(oheight-1-z),ip.getPixel(x,y));					
				}					
			}
		}

	public void clearImage() {
		if (inImg!=null)inImg.flush();
	}

	public static void main(String[] args) {
		//PlexiFileOpener pfo = new PlexiFileOpener("IFH", "C:\\Mohana\\Temp\\arc003\\050721_dm1\\PROCESSED\\MPRAGE\\T88_111", "050721_dm1_mpr_n4_111_t88.4dfp.img");
		//PlexiFileOpener pfo = new PlexiFileOpener("ANALYZE", "C:\\Mohana\\Temp\\LoRes", "050721_dm1_mpr_n4_111_t88_8bit_tra.img");
		//PlexiFileOpener pfo = new PlexiFileOpener("IFH", "C:\\Documents and Settings\\Mohana\\Desktop", "3837-2-10.ima.4dfp.img");
		/*ImagePlus i = pfo.getImagePlus();
		PlexiImageOrientor pio = new PlexiImageOrientor(i);
		System.out.println("In Orientation is " + pfo.getOrientation());
		ImagePlus i2= pio.getImage(pfo.getOrientation()+"F","TRANSVERSEF");
		i2.show();*/
	}
}

	