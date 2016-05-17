/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.utils.Transform;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageConverter;
import ij.process.StackConverter;

public class BitConverter {
 public void convertTo8BitColor(ImagePlus img)  {
	   
     if  (img.getType()==ImagePlus.COLOR_RGB) {
  	   ImageStack stack = img.getImageStack();

    	 int nColors = 256;	
                 if (stack.getSize() > 1) { //Not greater than 1 as StackConverter needs a stack size of 2 or more
               	  new StackConverter(img).convertToIndexedColor(nColors);
                 }else {
               	  ImageConverter ic = new ImageConverter(img);
               	  ic.convertRGBtoIndexedColor(nColors);
                 }
               }

  }
 
  public void convertToGrayscale(ImagePlus img) {
	  convertTo8BitColor(img);
	  ImageStack stack = img.getImageStack();
	   
	  if (stack.getSize() > 1) {
	    	  new StackConverter(img).convertToGray8();
	      }else {
	    	  ImageConverter ic = new ImageConverter(img);
	    	  ic.convertToGray8();
	      }
  }
}
