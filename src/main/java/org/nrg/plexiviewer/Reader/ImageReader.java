//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.Reader;

import ij.io.*;
import java.io.*; 

public interface ImageReader {
	public FileInfo getFileInfo() throws IOException ;
	public String getOrientation() ;
	public int getOrientationForWriter();
    public int getVolumes();
    public boolean isZipped();
    public void clearTempFolder();
}
