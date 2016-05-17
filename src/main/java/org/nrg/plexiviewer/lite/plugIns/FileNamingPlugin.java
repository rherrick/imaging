/*
 * org.nrg.plexiViewer.lite.plugIns.FileNamingPlugin
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.plugIns;

/**
 * @author Mohana
 *
 */
import org.nrg.plexiviewer.lite.ui.GenericIdentifier;
public class FileNamingPlugin {
	public GenericIdentifier getFileNameToDisplay(String exptType, String fileName) {
		GenericIdentifier rtn= new GenericIdentifier(fileName,fileName,true);
		if (exptType.equalsIgnoreCase("Gleek")) {
			int indexOfUnderScore = fileName.lastIndexOf("_");
			int secondLastIndexOfUnderScore = fileName.substring(0,indexOfUnderScore).lastIndexOf("_");
			String id;
			if (secondLastIndexOfUnderScore>0) {
				 id=fileName.substring(secondLastIndexOfUnderScore+1);
			}else
				id = fileName.substring(indexOfUnderScore);
			rtn.setId(id);	
		} 
		return rtn;
	}
	
	
}
