/*
 * org.nrg.plexiViewer.io.ImageLoaderI
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.io;

import ij.ImagePlus;

import org.nrg.plexiviewer.lite.display.MontageDisplay;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;


public interface ImageLoaderI {
	public ImagePlus load()  throws   Exception ;
	public MontageDisplay getMontageDisplay();
	public PlexiImageFile getPlexiImageFile();
	
}
