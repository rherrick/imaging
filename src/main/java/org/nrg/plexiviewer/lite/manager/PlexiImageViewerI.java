//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.manager;

import ij.*;
import org.nrg.plexiviewer.lite.xml.*;
import org.nrg.plexiviewer.lite.display.*;
import org.nrg.plexiviewer.lite.*;
import org.nrg.plexiviewer.lite.gui.*;
import org.nrg.plexiviewer.lite.io.*;
import java.awt.Rectangle;

public interface PlexiImageViewerI {

    
	public int open(PlexiMessagePanel msgPanel);
	public void handleOutOfMemoryError();
	public void setWindowDimensions(PlexiImageFile pf);
	public void setWindowDimensions(int w, int h, int s);
	public UserSelection getUserSelection();
	public ImagePlus getImagePlus();
	public Layout getLayout();
	public void adjustCoords(int x, int y, int z, float ipvalue, boolean slice, String fromView, boolean fromRadiologic);
	public void adjustRegions(int x, int y, int z, float ipvalue, boolean slice, String fromView, boolean fromRadiologic, int region);
	public void setWaitCursor(boolean status);
	public void setMessage(String msg, String desc);
	public void setMontageDisplay(MontageDisplay display);	
	public Rectangle getWindowDimensions();
	public boolean isInitialized();
	public void setImage(int index, ImageStack s);
	public void setImage(int index, ImagePlus s);
	public void updateCrossHair();
	public void updateCrossHair(String orientation, int sliceNo);
}
