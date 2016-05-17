/*
 * org.nrg.plexiViewer.lite.xml.HiRes
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
public class HiRes implements Cloneable {
		Layout layout;
		String layoutName;
		String formatConverterClassName;
		Hashtable layersHash;
		MontageView montageView;
		String format;
		Float minIntensity=null;
		Float maxIntensity=null;

		
		public HiRes() {
			layersHash = new Hashtable();
		}
		/**
		 * @return
		 */
		public boolean isLayered() {
			if (layersHash.size()>1) return true; else return false;
		}

		/**
		 * @return
		 */
		public Hashtable getLayersHash() {
			return layersHash;
		}

		/**
		 * @return
		 */
		public Layout getLayout() {
			return layout;
		}

		/**
		 * @return
		 */
		public SchemaLink getSchemaLink(Integer location) {
			return (SchemaLink)layersHash.get(location);
		}

		public void setLayer(Integer i, SchemaLink s) {
			layersHash.put(i,s);
		}
		/**
		 * @param hashtable
		 */
		public void setLayersHash(Hashtable hashtable) {
			layersHash = hashtable;
		}

		/**
		 * @param layout
		 */
		public void setLayout(Layout layout) {
			this.layout = layout;
		}

		/**
		 * @return
		 */
		public String getFormatConverterClassName() {
			return formatConverterClassName;
		}

		/**
		 * @param string
		 */
		public void setFormatConverterClassName(String string) {
			formatConverterClassName = string;
		}

		/**
		 * @return
		 */
		public String getLayoutName() {
			return layoutName;
		}

		/**
		 * @param string
		 */
		public void setLayoutName(String string) {
			layoutName = string;
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
		
		/**
		 * @return
		 */
		public MontageView getMontageView() {
			return montageView;
		}
		
		/**
		 * @param view
		 */
		public void setMontageView(MontageView view) {
			montageView = view;
		}

		public Float getMaxIntensity() {
			return maxIntensity;
		}

		public void setMaxIntensity(Float maxIntensity) {
			this.maxIntensity = maxIntensity;
		}

		public Float getMinIntensity() {
			return minIntensity;
		}

		public void setMinIntensity(Float minIntensity) {
			this.minIntensity = minIntensity;
		}

        public Object clone() {
            try {
                return super.clone();
            }catch(CloneNotSupportedException e) {
                return null;
            }
    }
}
