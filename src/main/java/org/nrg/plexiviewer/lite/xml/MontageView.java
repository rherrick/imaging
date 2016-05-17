//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.xml;

import java.util.Enumeration;
import java.util.Hashtable;

public class MontageView implements java.io.Serializable, java.lang.Cloneable {
	
    public static final float SCALE = (float)0.65;  
	private float scale;
	private Hashtable viewInfo;
	
	public MontageView() {
		viewInfo = new Hashtable();
		scale=1;
	}
	
	/**
	 * @return
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * @param i
	 */
	public void setScale(float i) {
		scale = i;
	}

	public void addViewInfo(String orientation, int startSlice, int endSlice, int sliceSpacing) {
		MontageInfo mInfo = new MontageInfo();
		mInfo.setStartSlice(startSlice);
		mInfo.setEndSlice(endSlice);
		mInfo.setSliceSpacing(sliceSpacing);
		viewInfo.put(orientation,mInfo);
	}

	public MontageInfo getMontageInfo(String orientation) {
		return ((MontageInfo)viewInfo.get(orientation));
	}

	public int getSliceSpacing (String orientation) {
		return ((MontageInfo)viewInfo.get(orientation)).getSliceSpacing();
	}

	public int getStartSlice(String orientation) {
		return ((MontageInfo)viewInfo.get(orientation)).getStartSlice();
	}
	
	public int getEndSlice(String orientation) {
		return ((MontageInfo)viewInfo.get(orientation)).getEndSlice();
	}
	
	public String toString() {
		String rtn = "MontageView: \n";
		rtn +=  "\t\t Scale: " + scale + "\n";
		for (Enumeration e=viewInfo.keys();e.hasMoreElements();) {
			Object key = e.nextElement();
			rtn+= "\t\t Orientation: " + key + "\n";
			rtn += "\t\t\t\t Details: " + ((MontageInfo)viewInfo.get(key)).toString();
			rtn +="\n";
		}
		return rtn;
	}
    
    /*public static MontageView newInstance(String imgOrientation, int imgWidth, int imgHeight, int nSlices) {
        MontageView mView = new MontageView();
        mView.setScale(SCALE);
        if ()
        return mView;
        
    }*/
	
	public Object clone() {
		try {
			MontageView deepClone = (MontageView) super.clone();
			Hashtable h = new Hashtable();
			for (Enumeration e = viewInfo.keys();e.hasMoreElements();) {
				Object key = e.nextElement();
				MontageInfo mClone = (MontageInfo)((MontageInfo)viewInfo.get(key)).clone();
				h.put(key,mClone);
			}
			deepClone.setViewInfoHash(h);
			return deepClone;
		}catch(CloneNotSupportedException e) {
			return null;
		}
	}
	
	/**
	 * @return
	 */
	public Hashtable getViewInfoHash() {
		return viewInfo;
	}

	/**
	 * @param hashtable
	 */
	public void setViewInfoHash(Hashtable hashtable) {
		viewInfo = hashtable;
	}

}

 