/*
 * org.nrg.plexiViewer.utils.XnatLoginDetails
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */
package org.nrg.plexiviewer.utils;

import javax.servlet.http.HttpServletRequest;

import org.nrg.xdat.security.XDATUser;

public class XnatLoginDetails {
	public XDATUser getSessionUser(HttpServletRequest httpRequest) {
		return (XDATUser) httpRequest.getSession().getAttribute("user");
	}

}
