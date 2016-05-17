/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.utils.imageformats;

import ij.ImagePlus;
import ij.process.StackProcessor;

public class DicomImageFlipper {
    public ImagePlus relay(ImagePlus img, String orientation, String patientOrientation) {
        if (patientOrientation == null) return img;
        String[] positives = patientOrientation.split("\\\\");
        if (positives == null || positives.length == 0) return  img;
        StackProcessor sp = new StackProcessor(img.getStack(), img.getProcessor());
        System.out.println("PatientOrientation " + patientOrientation + " " + orientation);
        if (orientation.equals("TRANSVERSE")) {
            if (positives[0].equals("L")) sp.flipHorizontal();
            if (positives[1].equals("A")) sp.flipVertical();
        }else  if (orientation.equals("CORONAL")) {
            if (positives[0].equals("L")) sp.flipHorizontal();
            if (positives[1].equals("H")) sp.flipVertical();
        }else  if (orientation.equals("SAGITTAL")) {
            if (positives[0].equals("A")) sp.flipHorizontal();
            if (positives[1].equals("H")) sp.flipVertical();
        }
        return img;
    }
}
