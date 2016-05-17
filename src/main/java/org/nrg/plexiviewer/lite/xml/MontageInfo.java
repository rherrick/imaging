//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.xml;

public class MontageInfo implements java.io.Serializable, java.lang.Cloneable  {
	private int start_slice, end_slice, slice_spacing;
	
	
	public MontageInfo() {
		
	}
	/**
	 * @return
	 */
	public int getEndSlice() {
		return end_slice;
	}

	/**
	 * @return
	 */
	public int getSliceSpacing() {
		return slice_spacing;
	}

	/**
	 * @return
	 */
	public int getStartSlice() {
		return start_slice;
	}

	/**
	 * @param i
	 */
	public void setEndSlice(int i) {
		end_slice = i;
	}

	/**
	 * @param i
	 */
	public void setSliceSpacing(int i) {
		slice_spacing = i;
	}

	/**
	 * @param i
	 */
	public void setStartSlice(int i) {
		start_slice = i;
	}

	public String toString() {
		String rtn ="";
		rtn += "\t\t Slice Spacing: " + slice_spacing + "\n";
		rtn += "\t\t Start Slice: " + start_slice + "\n";
		rtn += "\t\t End Slice: " + end_slice + "\n";
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
