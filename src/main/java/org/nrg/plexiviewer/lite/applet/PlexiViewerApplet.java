/*
 * org.nrg.plexiViewer.lite.applet.PlexiViewerApplet
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.applet;

/**
 * @author Mohana
 *
 */
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;

import org.nrg.framework.net.JSESSIONIDCookie;
import org.nrg.plexiviewer.lite.UserInterfaceContents;
import org.nrg.plexiviewer.lite.ui.PlexiControlPanel;
import org.nrg.plexiviewer.lite.utils.HTTPDetails;

public class PlexiViewerApplet  extends Applet {
	  PlexiControlPanel panel;
	  public void init() {
			this.setBackground(Color.white);
			this.setLayout(new BorderLayout(0,0));
			//this.setLayout(new GridBagLayout());
			String sessionId  = getParameter("sessionId");
			//System.out.println("Applet CodeBase " + this.getCodeBase());
			HTTPDetails.setHost(this.getCodeBase().getHost());
			HTTPDetails.setPort(this.getCodeBase().getPort());
			HTTPDetails.setWebAppName(this.getCodeBase().getPath());
			HTTPDetails.setProtocol(this.getCodeBase().getProtocol());
			HTTPDetails.setJSESSIONIDCookie(new JSESSIONIDCookie(getParameter("jsessionid")));
			//HTTPDetails.setHost("localhost");
			//HTTPDetails.setPort(8080);
			//HTTPDetails.setWebAppName("/cnda_xnat/applet");
			//HTTPDetails.setProtocol("http");

			//System.out.println("Protocol " + this.getCodeBase().getProtocol());
			UserInterfaceContents uiContents = new UserInterfaceContents(sessionId);
			/*Hashtable uiContents1 = uiContents.getAllScans();
			Enumeration enume = uiContents1.keys();
			while(enume.hasMoreElements()) {
				Integer index = (Integer)enume.nextElement();
				System.out.println("Index is " + index + " " + uiContents1.get(index));
			}*/

			panel = new PlexiControlPanel(uiContents, this.getAppletContext());
			String value = getParameter("startDisplayWith");
			if ( value!=null)
				panel.setStartDisplayWith(value);
		
			add(panel, BorderLayout.NORTH);
			validate();
			long totMem = Runtime.getRuntime().totalMemory();
			String totMemStr = totMem<10000*1024?totMem/1024L+"K":totMem/1048576L+"MB";
			System.out.println("Total Available memory with the JVM is " + totMemStr);
	  }
  
	  public void stop() {
		panel=null;
	  }

	}


