/*
 * org.nrg.plexiViewer.lite.tunneler.ImageDimLoader
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.tunneler;

/**
 * @author Mohana
 *
 */

import java.net.*;
import java.io.*;
import org.nrg.plexiviewer.lite.utils.HTTPDetails;
import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.lite.*;
import org.nrg.plexiviewer.lite.display.*;

public class ImageDimLoader {
	
	private URL dataURL;
	UserSelection options;
	private String host;
	private URLConnection servletConnection;
	private boolean windowShowing;  
	boolean error=false;
	PlexiImageFile pf;
	MontageDisplay m;
		
	public ImageDimLoader(UserSelection u, String host) {
		options=u;
		this.host = host;
	}
    
		private void openConnection() {
			String suffix = HTTPDetails.getSuffix("ImageLoaderServlet");
			try {
				dataURL = new URL(HTTPDetails.getProtocol(), HTTPDetails.getHost(), HTTPDetails.getPort(), suffix);
				servletConnection = HTTPDetails.openConnection(dataURL);
				servletConnection.setDoInput(true);          
				servletConnection.setDoOutput(true);
				//Don't use a cached version of URL connection.
				servletConnection.setUseCaches (false);
				servletConnection.setDefaultUseCaches (false);
				//Specify the content type that we will send binary data
				servletConnection.setRequestProperty ("Content-Type", "application/octet-stream");

				ObjectOutputStream outStreamToServlet = new ObjectOutputStream(servletConnection.getOutputStream());
				outStreamToServlet.writeObject(options);
				outStreamToServlet.flush();
				outStreamToServlet.close();
			} catch(MalformedURLException mfe) {
				mfe.printStackTrace();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
    
		public void load() {
			try {
				getImageFileName();
		   } catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
	private void getImageFileName() throws IOException {
		   openConnection();
		   InputStream is = servletConnection.getInputStream();
		   ObjectInputStream in =  new ObjectInputStream(is);
		   try {
				m = (MontageDisplay)in.readObject();
			 	pf = (PlexiImageFile)in.readObject();
			if (pf==null) {
				error=true;
			}
			options.setFile(pf);
		   }catch(Exception e) {
		   		e.printStackTrace();
				error=true;
		   }
		   in.close();
		   is.close();
	} 
	/**
	 * @return
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @return
	 */
	public UserSelection getUserSelection() {
		return options;
	}

	/**
	 * @param b
	 */
	public void setError(boolean b) {
		error = b;
	}



	/**
	 * @return
	 */
	public MontageDisplay getMontageDisplay() {
		return m;
	}

	/**
	 * @return
	 */
	public PlexiImageFile getPlexiImageFile() {
		return pf;
	}

}
