/*
 * org.nrg.plexiViewer.lite.xml.Layout
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.xml;

import org.nrg.plexiviewer.lite.display.Point3d;

public class Layout implements java.io.Serializable, java.lang.Cloneable {
	
		private String name;
		private int voxelSize;
		private Point3d origin;
			
		public Layout() {
			
		}
		/**
		 * Gets the name of the Layout
		 * @return String
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the coordinates of the origin
		 * @return Point3d object
		 */
		public Point3d getOrigin() {
			return origin;
		}

		/**
		 * Set the name of the Layout object
		 * @param string
		 */
		public void setName(String string) {
			name = string;
		}

		/**
		 * Set the coordinates of the origin
		 * @param point3d
		 */
		public void setOrigin(Point3d point3d) {
			origin = point3d;
		}
	
		public String toString() {
			String str =" Layout: \n";
			str += "\t\t Name: " + name + "\n";
			str += "\t\t Origin: " + this.origin.toString() + "\n";
			str += "\t\t Voxel Size: " + voxelSize + "\n";
			return str;
		}
		/**
		 * @return
		 */
		public int getVoxelSize() {
			return voxelSize;
		}

		/**
		 * @param i
		 */
		public void setVoxelSize(int i) {
			voxelSize = i;
		}

		public Object clone() {
			try {
				Layout deepCopy = (Layout) super.clone();
				deepCopy.setOrigin((Point3d)getOrigin().clone());
				return deepCopy;
			}catch(CloneNotSupportedException e) {
				return null;
			}
		}

	}
