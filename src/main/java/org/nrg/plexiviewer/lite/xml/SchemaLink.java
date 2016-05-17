/*
 * org.nrg.plexiViewer.lite.xml.SchemaLink
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.xml;
/**
 * @author Mohana
 *
 */
public class SchemaLink implements Cloneable {
	String element;
	String value=null;
	
	/**
	 * @return
	 */
	public String getElementName() {
		return element;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param string
	 */
	public void setElementName(String string) {
		element = string;
	}

	/**
	 * @param string
	 */
	public void setValue(String string) {
		value = string;
	}
	
	public boolean hasValue() {
		boolean rtn=false;
		if (value!=null) 
			rtn=true;
		return rtn;	
	}
	
	public String toString() {
		String rtn = "Element Name " + element + "\n";
		rtn +="Value  " + value ;
		return rtn;
	}
    
    public Object clone() {
        try {
            return super.clone();
        }catch(CloneNotSupportedException e) {
            return null;
        }
}

}
