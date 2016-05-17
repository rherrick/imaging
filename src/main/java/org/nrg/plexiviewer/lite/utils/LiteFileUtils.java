/*
 * org.nrg.plexiViewer.lite.utils.LiteFileUtils
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */
package org.nrg.plexiviewer.lite.utils;

import org.nrg.xdat.bean.XnatAbstractresourceBean;
import org.nrg.xdat.bean.XnatResourceBean;

public class LiteFileUtils {
	public static String getFileName(XnatAbstractresourceBean file){
		String rtn = "";
		if (file instanceof XnatResourceBean) {
			XnatResourceBean resource = (XnatResourceBean)file;
			String uri = resource.getUri();
			int i = uri.lastIndexOf("/");
			if (i != -1) {
				rtn = uri.substring(i+1); //assuming doesnt end in /
			}
		}
		return rtn;
	}

	public static String getFilePath(XnatAbstractresourceBean file){
		String rtn = "";
		if (file instanceof XnatResourceBean) {
			XnatResourceBean resource = (XnatResourceBean)file;
			String uri = resource.getUri();
			int i = uri.lastIndexOf("/");
			if (i != -1) {
				rtn = uri.substring(0,i); //assuming doesnt end in /
			}
		}
		return rtn;
	}

}
