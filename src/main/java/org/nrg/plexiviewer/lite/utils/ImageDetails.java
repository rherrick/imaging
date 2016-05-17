/*
 * org.nrg.plexiViewer.lite.utils.ImageDetails
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */
package org.nrg.plexiviewer.lite.utils;

import org.nrg.plexiviewer.lite.display.Point3d;
import org.nrg.plexiviewer.lite.xml.Layout;
import org.nrg.plexiviewer.lite.xml.MontageView;

public class ImageDetails {
	private static final String DEFAULT_LORES_TYPE = "8bit";
	private static final String DEFAULT_LORES_FORMAT = "ANALYZE";

	public static String getDefaultLoresFormat() {
		return DEFAULT_LORES_FORMAT;
	}

	public MontageView getDefaultMontageView() {
		MontageView rtn = new MontageView();
		return rtn;
	}
	
	public static String getDefaultLoresType() {
		return DEFAULT_LORES_TYPE;
	}
	
	public static Layout getLayout(int dimx, int dimy, int dimz, double vx, double vy, double vz, String format) {
		Layout rtn = new Layout();
		if (format != null && format.equalsIgnoreCase("IFH")) {
			if (vx==1 && vy==1 && vz==1) {
				rtn.setName("tal_111");
				rtn.setVoxelSize(1);
				Point3d origin = new Point3d(88,84,75);
				rtn.setOrigin(origin);
			}else if (vx==2 && vy==2 && vz==2) {
				rtn.setName("tal_222");
				rtn.setVoxelSize(2);
				Point3d origin = new Point3d(63,63,34);
				rtn.setOrigin(origin);
			}else if (vx==3 && vy==3 && vz==3) {
				rtn.setName("tal_333");
				rtn.setVoxelSize(3);
				Point3d origin = new Point3d(24,29,20);
				rtn.setOrigin(origin);
			}
		}else {
			rtn.setName("native");
		}
		return rtn;
	}
	
}
