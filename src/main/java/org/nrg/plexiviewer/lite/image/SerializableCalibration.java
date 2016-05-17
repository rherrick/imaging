//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.image;

import ij.measure.Calibration;

public class SerializableCalibration  implements java.io.Serializable {
	/** Pixel width in 'unit's */
	   public double pixelWidth = 1.0;
    
	   /** Pixel height in 'unit's */
	   public double pixelHeight = 1.0;
    
	   /** Pixel depth in 'unit's */
	   public double pixelDepth = 1.0;
	   
	   public SerializableCalibration() {
	   }
    
	   public SerializableCalibration (Calibration c) {
	   	 this.pixelDepth = c.pixelDepth;
	   	 this.pixelHeight = c.pixelHeight;
	   	 this.pixelWidth = c.pixelWidth;
	   }
	   
	   public Calibration getCalibration() {
	   		Calibration c = new Calibration();
	   		c.pixelHeight = this.pixelHeight;
	   		c.pixelWidth = this.pixelWidth;
	   		c.pixelDepth = this.pixelDepth;
	   		return c;
	   }
	   
	   public String toString() {
	   	 String str = "SerializableCalibration:: \n";
	   	 str += "Pixel Width:  " + this.pixelWidth + "\n";
	   	 str += "Pixel Height: " + this.pixelHeight + "\n";
	   	 str += "Pixel Depth: " + this.pixelDepth + "\n";
	   	 return str;
	   }
	   
}
