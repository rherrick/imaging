/*
 * org.nrg.plexiViewer.lite.ui.GenericIdentifier
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.ui;

/**
 * @author Mohana
 *
 */
public class GenericIdentifier implements java.io.Serializable {
	Object id;
	Object value;
	boolean sendId=false;
	
	public GenericIdentifier(Object id, Object value) {
		this.id = id;
		this.value= value;
	}
	
	public GenericIdentifier() {
	}

	public GenericIdentifier(Object id, Object value, boolean sendid) {
		this.id = id;
		this.value= value;
		this.sendId = sendid;
	}
	
	
	/**
	 * @return
	 */
	public Object getId() {
		return id;
	}

	/**
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param object
	 */
	public void setId(Object object) {
		id = object;
	}

	/**
	 * @param object
	 */
	public void setValue(Object object) {
		value = object;
	}
	
	public String toString() {
		String rtn = value.toString();
		if (sendId)
			rtn = id.toString();
		return rtn;	
	}

}
