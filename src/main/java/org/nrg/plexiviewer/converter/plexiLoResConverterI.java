//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.converter;

import java.net.URISyntaxException;

import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;

public interface plexiLoResConverterI {
	public int convertAndSave(UserSelection options);	
	public PlexiImageFile getFileLocationAndName() throws URISyntaxException, Exception;
}
