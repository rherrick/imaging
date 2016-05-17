/*
 * org.nrg.plexiViewer.lite.utils.ImageUtils
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.utils;

/**
 * @author Mohana
 *
 */
import ij.io.FileInfo;
public class ImageUtils {

	public static String initImagePlus(int type) {
		String rtn=null;
		if (type == FileInfo.GRAY8 || type== FileInfo.COLOR8|| type==FileInfo.BITMAP)
			rtn= "ij.process.ByteProcessor";
		else if (type == FileInfo.RGB||type== FileInfo.BGR || type==FileInfo.ARGB || type==FileInfo.RGB_PLANAR) 
			rtn="ij.process.ColorProcessor";
		else if (type == FileInfo.GRAY16_SIGNED || type == FileInfo.GRAY16_UNSIGNED || type==FileInfo.GRAY12_UNSIGNED) 
			rtn="ij.process.ShortProcessor";
		else
			rtn="ij.process.FloatProcessor";
		return rtn;	
	}
}
