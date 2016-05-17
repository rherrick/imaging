/*
 * org.nrg.plexiViewer.lite.FSXMLInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite;

/**
 * @author Mohana
 *
 */
import java.util.*;
import org.nrg.plexiviewer.lite.ui.*;
import org.nrg.plexiviewer.lite.io.*;

public class FSXMLInfo extends java.lang.Object implements java.io.Serializable{
	Vector files; //Vector of PlexiImageFile objects;
	GenericIdentifier viType_displayName;
	boolean allowedToChooseScanNos, allowedToChooseFiles;
	String imageViewerClassName;
	Vector hiResLayerNos;
	
	
	public FSXMLInfo() {
		files = new Vector();
		viType_displayName = new GenericIdentifier();
		hiResLayerNos = new Vector();
	}
	
	public Vector getFiles() {
		Vector rtn=new Vector();
		if (allowedToChooseFiles) {
			rtn=files;
		}
		return rtn;
	}
	
	public Vector getFilesDeepCopy() {
		Vector rtn=null;
		if (files!=null) {
			rtn=new Vector();
			for (int i=0;i<files.size();i++) {
				if (files.elementAt(i)!=null)
					rtn.addElement(((PlexiImageFile)files.elementAt(i)).clone());	
			}
		}
		return rtn;
	}
	
	public Vector getHiResLayerNosDeepCopy() {
			Vector rtn=null;
			if (hiResLayerNos!=null) {
				rtn=new Vector();
				for (int i=0;i<hiResLayerNos.size();i++) {
					if (hiResLayerNos.elementAt(i)!=null)
						rtn.addElement(new Integer(((Integer)hiResLayerNos.elementAt(i)).intValue()));	
				}
			}
			return rtn;
		}
	
	

	public String getViewableType() {
		return (String)viType_displayName.getId();
	}

	public void setFiles(Vector vector) {
		files = vector;
	}

	public void setViewableItemType(String v) {
		viType_displayName.setId(v);
	}

	public String getDataToDisplay() {
		return (String)viType_displayName.getValue();
	}

	public void setDataType(String string) {
		viType_displayName.setValue(string);
	}

	public boolean canChooseScanNos() {
		return allowedToChooseScanNos;
	}

	public void canChooseScanNos(boolean b) {
		allowedToChooseScanNos=b;
	}

	public void canChooseFiles(boolean b) {
		allowedToChooseFiles = b;
	}	

	
	public boolean canChooseFiles() {
		return allowedToChooseFiles;		
	}	

	/**
	 * @return
	 */
	public String getImageViewerClassName() {
		return imageViewerClassName;
	}

	/**
	 * @param string
	 */
	public void setImageViewerClassName(String string) {
		imageViewerClassName = string;
	}

	/**
	 * @return
	 */
	public Vector getHiResLayerNos() {
		return hiResLayerNos;
	}

	/**
	 * @param vector
	 */
	public void setHiResLayerNos(Vector vector) {
		hiResLayerNos = vector;
	}

	

	

}
