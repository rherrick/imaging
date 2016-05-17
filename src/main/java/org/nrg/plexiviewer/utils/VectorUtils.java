/*
 * org.nrg.plexiViewer.utils.VectorUtils
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.utils;

/**
 * @author Mohana
 *
 */
import java.util.*;
public class VectorUtils {
	
	public static Vector intersect(Vector v1, Vector v2) {
		Vector rtn = new Vector();
		for (int i = 0; i < v1.size(); i++) {
			if (v2.contains(v1.elementAt(i))) {
				rtn.add(v1.get(i));	
			}	
		}
		return rtn;
	}
	
}
