/*
 * org.nrg.plexiViewer.lite.DBFSXMLInfo
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

public class DBFSXMLInfo  extends java.lang.Object implements java.io.Serializable {
	GenericIdentifier viType_displayName;
	Vector scanNos;
	Vector fsxmlmergedInfo;
    Hashtable viewableOrienations;
	

    public DBFSXMLInfo() {
		fsxmlmergedInfo = new Vector();
		scanNos=new Vector();
        viewableOrienations = new Hashtable();
	}
	
	/**
	 * @return
	 */
	public Vector getFsxmlmergedInfo() {
		return fsxmlmergedInfo;
	}

	/**
	 * @return
	 */
	public Vector getScanNos() {
		return scanNos;
	}

	/**
	 * @param hashtable
	 */
	public void setFsxmlmergedInfo(Vector v) {
		fsxmlmergedInfo = v;
	}

	/**
	 * @param vector
	 */
	public void setScanNos(Vector vector) {
		scanNos = vector;
	}

	/**
	 * @return
	 */
	public String getScanToDisplay() {
		return (String)viType_displayName.getValue();
	}

	/**
	 * @return
	 */
	public GenericIdentifier getTag() {
		return (GenericIdentifier)viType_displayName;
	}


	/**
	 * @param string
	 */
	public void setTag(GenericIdentifier g) {
		viType_displayName = g;
	}
	
	public Vector getLinkedDropDown() {
		Vector rtn=new Vector();
		for (int i=0;i<this.fsxmlmergedInfo.size();i++)
			rtn.addElement(((FSXMLInfo)fsxmlmergedInfo.elementAt(i)).getDataToDisplay());
		return rtn;	
	}

	public void addScanNo(Object obj) {
		scanNos.addElement(obj);
	}
    /**
     * @return Returns the viewableOrienations.
     */
    public Vector getViewableOrienations(Object scanNo) {
        Vector rtn = new Vector();
        if (viewableOrienations.containsKey(scanNo)) {
        	rtn = (Vector)viewableOrienations.get(scanNo);
        }
    	return rtn;
    }

    /**
     * @param viewableOrienations The viewableOrienations to set.
     */
    public void setViewableOrienations(Object scanNo, Vector viewableOrienations) {
        this.viewableOrienations.put(scanNo, viewableOrienations);
    }
    
    public String toString() {
    	String rtn = "DISPLAY " + getScanToDisplay();
    	rtn += "Scan Nos: " + getScanNos();
    	return rtn;
    }
	
}
