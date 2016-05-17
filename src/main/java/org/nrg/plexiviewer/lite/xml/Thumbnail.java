/*
 * org.nrg.plexiViewer.lite.xml.Thumbnail
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.xml;

import java.util.*;

public class Thumbnail implements Cloneable {
	CropDetails cropDetails;
	String converterClassName;
	Hashtable slices;
	String format;
	
	
	public Thumbnail() {
		slices = new Hashtable();
	}
	
	/**
	 * @return
	 */
	public CropDetails getCropDetails() {
		return cropDetails;
	}

	
	public Vector getSlices(String orientation) {
		return (Vector)slices.get(orientation);
	}

	/**
	 * @return
	 */
	public Hashtable getSlices() {
		return slices;
	}

	/**
	 * @param details
	 */
	public void setCropDetails(CropDetails details) {
		cropDetails = details;
	}

	/**
	 * @param hashtable
	 */
	public void setSlices(Hashtable hashtable) {
		slices = hashtable;
	}
	
	public void setSlice(String orientation, Vector sliceNumbers) {
		slices.put(orientation,sliceNumbers);
	}
	/**
	 * @return
	 */
	public String getConverterClassName() {
		return converterClassName;
	}

	/**
	 * @param string
	 */
	public void setConverterClassName(String string) {
		converterClassName = string;
	}

	/**
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param string
	 */
	public void setFormat(String string) {
		format = string;
	}

    public Object clone() {
        try {
            Thumbnail deepClone = (Thumbnail)super.clone();
            if (slices==null) {
                deepClone.setSlices(null);                    
            }else {
                Enumeration enumerator = slices.keys();
                while (enumerator.hasMoreElements()) {
                    String key = (String)enumerator.nextElement();
                    Vector value = (Vector)slices.get(key);
                    Vector cloneValue = new Vector();
                    for (int i=0;i<value.size();i++) {
                        cloneValue.addElement((Integer)value.elementAt(i));     
                    }
                    deepClone.setSlice(key,cloneValue);
                }
            }
            return deepClone;
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
