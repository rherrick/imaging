/*
 * org.nrg.plexiViewer.lite.tunneler.MontageImageLoader
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
import java.util.*;
import java.net.*;
import java.io.*;
import org.nrg.plexiviewer.lite.utils.HTTPDetails;
import org.nrg.plexiviewer.lite.utils.LoadErrorDialog;
import org.nrg.plexiviewer.lite.manager.*;
import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.lite.display.*;
import ij.*;
import ij.process.*;

public class MontageImageLoader  {
	
	private URL dataURL;
	PlexiImageViewerI  image;
	private String host;
	private URLConnection servletConnection;
	private boolean windowShowing;  
	Boolean getImage;
	PlexiImageFile pf;
	MontageDisplay m;
	boolean error;
	
	public MontageImageLoader(PlexiImageViewerI  image, boolean getImage) {
		this.image = image;
		this.host = HTTPDetails.host;
		this.getImage=new Boolean(getImage);
	}

	public MontageImageLoader(PlexiImageViewerI  image) {
		this.image = image;
		this.host = HTTPDetails.host;
		this.getImage=new Boolean(true);
	}

    
		private void openConnection() {
			String suffix = HTTPDetails.getSuffix("MontageImageLoaderServlet");
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
				outStreamToServlet.writeObject(image.getUserSelection());
				outStreamToServlet.flush();
				outStreamToServlet.writeObject(getImage);
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
		   	image.setMessage("Fetching Image Dimensions", "Please wait");
			m = (MontageDisplay)in.readObject();
			pf = (PlexiImageFile)in.readObject();
			if (pf==null) {
				Vector msg = new Vector();
				msg.add("Image not available");
				LoadErrorDialog led = new LoadErrorDialog(image.getImagePlus().getWindow(), msg, false);
				led.show(); 
			}
			image.setMontageDisplay(m);
			image.setWindowDimensions(pf);
			System.out.println("Montage Loader:: " + pf.getDimX() + " " + pf.getDimY() + " " + pf.getDimZ());
			if (getImage.booleanValue()) {
				Object pixels;
				ImageStack stack = new ImageStack(pf.getDimX(),pf.getDimY());
				ImagePlus mrip = image.getImagePlus();
				for (int i =0; i<pf.getDimZ(); i++){
					pixels = (Object)in.readObject();
					stack.addSlice("",pixels);
					if (i == 0){
						 mrip.setStack("",stack);
				   } 
				   if (image != null)
						image.setMessage("Loading image:", " ");
				}
				ImageProcessor ip = mrip.getProcessor();
				double min = ip.getMin();
				double max = ip.getMax();
				 if ((ip instanceof ShortProcessor) || (ip instanceof FloatProcessor)) {
						 ip.resetMinAndMax();
				 }		
				ip.setMinAndMax(min, max);
				 mrip.getProcessor().resetMinAndMax();
				 mrip.updateAndDraw();	   
			}	
			
		   }catch(Exception e) {
		   		e.printStackTrace();
				Vector msg = new Vector();
				msg.add("Error! Please contact nrgtech@npg.wust.edu");
				LoadErrorDialog led = new LoadErrorDialog(image.getImagePlus().getWindow(), msg, false);
				led.show(); 
		   }
		   in.close();
		   is.close();
	} 
}
