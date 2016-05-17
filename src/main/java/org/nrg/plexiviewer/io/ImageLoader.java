//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.io;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.display.MontageDisplay;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;

public class ImageLoader {

	UserSelection opt;	
	PlexiImageFile openedImageFile;
	/**
	 * mDisplay is populated with the MontageDisplay information as in the Specification XML
	 * for the LoRes/HiRes image type.
	 */
	MontageDisplay mDisplay;
	
	
	/**
	 * Constructor 
	 * @param options: the options for which the image is to be fetched
	 */
	public ImageLoader(UserSelection options) {
		opt = options;
	}
	
	/**
	 * Returns the associated image based on the imageType. 
	 * Populates the  montageDisplay from the XML for the image type.
	 * 
	 * @param imageType: Specify LoRes, Thumbnail, HiRes
	 * @param convertHiResToDefaultLoRes: boolean 
	 * @return associated Image as an ImagePlus object
	 */
	public ImagePlus getImage(String imageType, boolean convertHiResToDefaultLoRes)  {
		ImagePlus img = null;
		try {
			ImageLoaderI loader= null;
			//if (imageType.equalsIgnoreCase("Thumbnail"))
			//	loader = new ThumbnailLoader(opt);
			if (imageType.equalsIgnoreCase("LoRes"))
				loader = new LoResLoader(opt);	
			else if (imageType.equalsIgnoreCase("HiRes"))
				loader = new HiResLoader(opt);
			img = loader.load();
			System.out.println("ImageLoader is in charge");
			if (img!=null) {
				mDisplay = loader.getMontageDisplay();
				openedImageFile = loader.getPlexiImageFile();
				System.out.println("ImageLoader openedIamgeFile " + openedImageFile.toString());
				System.out.println("ImageLoader getIamgeFile " + getPlexiImageFile().toString());
			}
					
		}catch(Exception e) {
			e.printStackTrace();
		}
		return img;		
	}

	   // returns true if this is an imported signed 16-bit image
		boolean isSigned16Bit(ImagePlus imp)  {
			if (imp.getType()!=ImagePlus.GRAY16)
			   return false;
		   else
			   return imp.getCalibration().getCValue(0)==-32768;
		}

	   // adds the specified value to every pixel in the stack
	   void add(ImagePlus imp, int value) {
		   //IJ.log("add: "+value);
		   ImageStack stack = imp.getStack();
		   for (int slice=1; slice<=stack.getSize(); slice++) {
			   ImageProcessor ip = stack.getProcessor(slice);
			   short[] pixels = (short[])ip.getPixels();
			   for (int i=0; i<pixels.length; i++)
				   pixels[i] = (short)((pixels[i]&0xffff)+value);
		   }
		}


	public ImagePlus getImage()  {
		ImagePlus img = null;
		try {
			ImageLoaderI loader= null;
			try {
				loader = new LoResLoader(opt);
			}catch(Exception e) {
				loader = new HiResLoader(opt);
			}
			img = loader.load();
			System.out.println("ImageLoader is in charge");
			openedImageFile = loader.getPlexiImageFile();
			//System.out.println("ImageLoader openedIamgeFile " + openedImageFile.toString());
			//System.out.println("ImageLoader getIamgeFile " + getPlexiImageFile().toString());

			if (img!=null)
				mDisplay = loader.getMontageDisplay();	
		}catch(Exception e) {
			e.printStackTrace();
		}
		return img;		
	}

	public PlexiImageFile getPlexiImageFile() {
		return openedImageFile;
	}
		
	public MontageDisplay getMontageDisplay() {
		return mDisplay; 
	}
}
