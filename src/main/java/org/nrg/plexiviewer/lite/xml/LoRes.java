/*
 * org.nrg.plexiViewer.lite.xml.LoRes
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
import java.util.*;

public class LoRes implements Cloneable{
	String type;
	String converterClassName;
	CropDetails cropDetails;
	MontageView montageView;
	Hashtable parameterHash;
	String format;
	
	
	public LoRes() {
		parameterHash = new Hashtable();
		cropDetails = new CropDetails();
        montageView = null;
	}
	
	/**
	 * @return
	 */
	public String getConverterClassName() {
		return converterClassName;
	}

	/**
	 * @return
	 */
	public CropDetails getCropDetails() {
		return cropDetails;
	}

	/**
	 * @return
	 */
	public MontageView getMontageView() {
		return montageView;
	}

	/**
	 * @return
	 */
	public Hashtable getParameterHash() {
		return parameterHash;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setConverterClassName(String string) {
		converterClassName = string;
	}

	/**
	 * @param details
	 */
	public void setCropDetails(CropDetails details) {
		cropDetails = details;
	}

	/**
	 * @param view
	 */
	public void setMontageView(MontageView view) {
		montageView = view;
	}

	/**
	 * @param hashtable
	 */
	public void setParameterHash(Hashtable hashtable) {
		parameterHash = hashtable;
	}

	public void setParameter(String attribute, String value) {
		parameterHash.put(attribute,value);	
	}

	public Object getParameter(String attribute) {
		Object rtn=null;
		if (parameterHash.containsKey(attribute))
			rtn= parameterHash.get(attribute);
		else if (attribute.equals("scaleFactor"))
			rtn= new Float(1.0);
		return rtn;			
	}


	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
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
            LoRes deepClone = (LoRes)super.clone();
            if (parameterHash==null) {
                deepClone.setParameterHash(null);                    
            }else {
                Enumeration enumerator = parameterHash.keys();
                while (enumerator.hasMoreElements()) {
                    String key = (String)enumerator.nextElement();
                    String value = (String)parameterHash.get(key);
                    deepClone.setParameter(key, value);
                }
            }
            if (montageView==null) {
                deepClone.setMontageView(null);                   
            }else {
                deepClone.setMontageView((MontageView)montageView.clone());
            }
            return deepClone;
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
