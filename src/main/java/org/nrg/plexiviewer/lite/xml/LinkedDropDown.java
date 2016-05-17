/*
 * org.nrg.plexiViewer.lite.xml.LinkedDropDown
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.xml;
public class LinkedDropDown implements Cloneable{
	String viewableItemType;

	/**
	 * @return
	 */
	public String getViewableItemType() {
		return viewableItemType;
	}


	public String toString() {
		return viewableItemType;
	}
	
	/**
	 * @param string
	 */
	public void setViewableItemType(String string) {
		viewableItemType = string;
	}
    
    public Object clone() {
        try {
            return super.clone();
        }catch(CloneNotSupportedException e) {
            return null;
        }
}

}
