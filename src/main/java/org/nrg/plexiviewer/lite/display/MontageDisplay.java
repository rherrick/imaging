//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.lite.display;

import java.io.Serializable;

import org.nrg.plexiviewer.lite.xml.*;
public class MontageDisplay implements Serializable, Cloneable {
	
	private MontageInfo mInfo;
	private float scale ;
	private Layout layout;
	private int nColumns;
	private int origIWidth, origIHeight, origStackSize;
	
	/**
     * @param info The mInfo to set.
     */
    public void setMInfo(MontageInfo info) {
        mInfo = info;
    }

    public MontageDisplay(MontageView mView, String orientation, Layout layout) {
		mInfo = null;
        if (mView != null) {
            this.mInfo = mView.getMontageInfo(orientation);
    		scale = mView.getScale();
        }else {
            scale = (float)0.65;
        }
		this.layout = layout;
	}
	
	public Layout getLayout() {
		return layout;
	}
	
	/**
		 * @return
		 */
		public int getEndSlice() {
			return mInfo.getEndSlice();
		}

		/**
		 * @return
		 */
		public int getSliceSpacing() {
			return mInfo.getSliceSpacing();
		}

		/**
		 * @return
		 */
		public int getStartSlice() {
			return mInfo.getStartSlice();
		}

	
	/**
	 * @return
	 */
	public float getScale() {
		return scale;
	}
	
	public String toString() {
		String rtn = "";
		rtn += "MontageDisplay:: \n";
		rtn += "\t\t Scale : " + getScale() + "\n";
		rtn += "\t\t StartSlice: " + getStartSlice() + "\n";
		rtn += "\t\t EndSlice: " + getEndSlice() + "\n";
		rtn += "\t\t SliceSpacing: " + getSliceSpacing() +"\n";
		return rtn;
	}
	
	public int getNumberOfColumns() {
		return nColumns;
	}

	public void setNumberOfColumns(int c) {
		nColumns = c;
	}

	public int getOriginalWidth() {
		return origIWidth;
	}

	public void setOriginalWidth(int i) {
		origIWidth =i;
	}

	public int getOriginalHeight() {
		return origIHeight;
	}


	public void setOriginalHeight(int i) {
		origIHeight =i;
	}

	public int getOriginalStackSize() {
		 return origStackSize;
	}

	
	public void setOriginalStackSize(int i) {
		 origStackSize = i;
	}
	
	public Object clone() {
		try {
			return super.clone();
		}catch(CloneNotSupportedException e) {
			return null;
		}
	}


}
