/*
 * org.nrg.plexiViewer.lite.display.Point3d
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.display;

public class Point3d implements java.io.Serializable, java.lang.Cloneable {
	
		public float x;
		public float y;
		public float z;

		public Point3d(){
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
    
		public Point3d(float x, float y, float z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
    
		public Point3d(Point3d p){
			this.x = p.x;
			this.y = p.y;
			this.z = p.z;
		}
    
		public void set(Point3d p){
			this.x = p.x;
			this.y = p.y;
			this.z = p.z;
		}

		public void set(float x, float y, float z){
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public String toString(){
    
			return new String(" x: " + x + " y: " + y + " z: " + z);
		}


		/**
		 * @return
		 */
		public float getX() {
			return x;
		}

		/**
		 * @return
		 */
		public float getY() {
			return y;
		}

		/**
		 * @return
		 */
		public float getZ() {
			return z;
		}

		public boolean equals(Point3d other) {
			boolean rtn = false;
			if ((this.getX()==other.getX()) && (this.getY()==other.getY()) && (this.getZ()==other.getZ()) )
				rtn = true;
			return rtn;
		}
		/**
		 * @param f
		 */
		public void setX(float f) {
			x = f;
		}

		/**
		 * @param f
		 */
		public void setY(float f) {
			y = f;
		}

		/**
		 * @param f
		 */
		public void setZ(float f) {
			z = f;
		}

		public Object clone() {
			try {
				return super.clone();
			}catch(CloneNotSupportedException e) {
				return null;
			}
		}
}

