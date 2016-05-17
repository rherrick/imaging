/*
 * org.nrg.plexiViewer.lite.xml.CropDetails
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
import java.awt.Rectangle;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;

public class CropDetails  implements java.io.Serializable, java.lang.Cloneable {
			int startSlice, endSlice;
			Rectangle boundingRectangle;
		
			public CropDetails() {
				startSlice =-1;
				endSlice = -1;
				boundingRectangle = new Rectangle(-1,-1,-1,-1);
			}
			
			public void setDimensions(PlexiImageFile pf) {
				if (crop()) {
					int dimZ=pf.getDimZ(), sSlice,eSlice;
					sSlice=1; eSlice=dimZ;
					if (startSlice!=-1 && endSlice==-1) {
						sSlice=startSlice;
					}else if (endSlice!=-1 && startSlice==-1) {
						eSlice=endSlice;
					}else if (endSlice!=-1 && startSlice!=-1) {
						sSlice=startSlice; eSlice=endSlice;
					}
					dimZ=endSlice-startSlice+1;
					pf.setDimensions( boundingRectangle.width,  boundingRectangle.height, dimZ);
				}
			}
			
			/**
			 * @return
			 */
			public Rectangle getBoundingRectangle() {
				return boundingRectangle;
			}

			/**
			 * @return
			 */
			public int getEndSlice() {
				return endSlice;
			}

			/**
			 * @return
			 */
			public int getStartSlice() {
				return startSlice;
			}

			/**
			 * @param rectangle
			 */
			public void setBoundingRectangle(int x, int y, int width, int height) {
				boundingRectangle = new Rectangle(x,y,width,height);
			}
		
		
			public void setBoundingRectangle(Rectangle rectangle) {
				boundingRectangle = rectangle;
			}

			/**
			 * @param i
			 */
			public void setEndSlice(int i) {
				endSlice = i;
			}

			/**
			 * @param i
			 */
			public void setStartSlice(int i) {
				startSlice = i;
			}

			public boolean deleteSlices() {
				boolean rtn = false;
				if (startSlice!=-1 || endSlice!=-1) {
					rtn=true;
				} 
				return rtn;
			}
	
			public boolean crop() {
				boolean rtn = false;
				if (boundingRectangle.x!=-1 && boundingRectangle.y!=-1 && boundingRectangle.width!=-1 && boundingRectangle.height!=-1 )
					rtn = true;
				return rtn;	
			}

			public String toString() {
				String rtn = "Crop Details:\n";
				rtn += "\t\t Start Slice: " + startSlice + "\n";
				rtn += "\t\t End Slice: " + endSlice + "\n";
				rtn += "\t\t Bounding Rectangle: " + boundingRectangle.toString();
				rtn += "\n";
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

