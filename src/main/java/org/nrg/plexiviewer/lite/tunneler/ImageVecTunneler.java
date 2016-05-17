//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.tunneler;

/**
 * @author Mohana
 *
 */
import java.net.*;
import java.io.*;
import java.util.*;
import ij.*;
import org.nrg.plexiviewer.lite.manager.*;
import org.nrg.plexiviewer.lite.*;
import org.nrg.plexiviewer.lite.ui.*;
import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.lite.display.*;
import org.nrg.plexiviewer.lite.utils.HTTPDetails;
import org.nrg.plexiviewer.lite.utils.LoadErrorDialog;

public class ImageVecTunneler implements Runnable {
	private boolean success = false;
	private boolean isDone = false;
	private URL dataURL;
	private String host;
	PlexiImageViewerI image;
	Vector imageVector;
	private URLConnection servletConnection;
    private int index;
    private UserSelection opt;
    
	public ImageVecTunneler(PlexiImageViewerI image, String host) {
		this.image = image;
		this.host = host;
	}
    
	private void openConnection(UserSelection opt) {
		String suffix = HTTPDetails.getSuffix("ImageDistributorServlet");
		try {
			dataURL = HTTPDetails.getURL(host,suffix);
			servletConnection = HTTPDetails.openConnection(dataURL);
			servletConnection.setDoInput(true);          
			servletConnection.setDoOutput(true);
			//Don't use a cached version of URL connection.
			servletConnection.setUseCaches (false);
			servletConnection.setDefaultUseCaches (false);
			//Specify the content type that we will send binary data
			servletConnection.setRequestProperty ("Content-Type", "application/octet-stream");
			ObjectOutputStream outStreamToServlet = new ObjectOutputStream(servletConnection.getOutputStream());
			outStreamToServlet.writeObject(opt);
			outStreamToServlet.flush();
			outStreamToServlet.close();
		} catch(MalformedURLException mfe) {
			mfe.printStackTrace();
			isDone = true;
		}catch(IOException ioe) {
			ioe.printStackTrace();
			isDone = true;
		}
	}
    
	public void load(UserSelection opt, int loc) {
		index=loc;
		this.opt = opt;
		Thread queryRetriever = new Thread(this);
		queryRetriever.start();
	}
    
	public void run() {
		try {
			retrieveImage();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}

	}
    
	public boolean isDone() {
		return(isDone);
	}
    
	public boolean isSuccess() {
		return success;
	}
    
	private void retrieveImage() throws IOException {
		openConnection(opt);
		InputStream is = servletConnection.getInputStream();
		//System.out.println("is: " + is);
		ObjectInputStream in =  new ObjectInputStream(is);
		try {
			image.setMessage("Loading image:","Receiving data...");
		  	LoadStatus status = (LoadStatus)in.readObject();
		  	if (!status.isSuccess()){
			  image.setMessage("Loading image:","Error");
			  PlexiImageFile pf = new PlexiImageFile();
			  pf.setDimX(200); pf.setDimY(200); pf.setDimZ(0);
			  image.setWindowDimensions(pf);
			  LoadErrorDialog led = new LoadErrorDialog(image.getImagePlus().getWindow(), status.getMessage(), false);
			  led.show(); 
			  //image.getImagePlus().getWindow().close();
			  return;
		  	}
			int nSlices = status.getCount();
			int width = status.getDimensions().width;
			int height = status.getDimensions().height;
			if (image.getWindowDimensions().width==-1 || image.getWindowDimensions().height==-1)
				image.setWindowDimensions(width,height,nSlices);
			image.setMessage("Loading image:","Connected to server...");
			Object pixels;
			ImageStack stack = new ImageStack(width,height);
			MontageDisplay display = (MontageDisplay)in.readObject();
			if (image.getLayout()==null) {
				//System.out.println("Layout is null will try to set it");
				image.setMontageDisplay(display);			
			}
			for (int i =0; i<nSlices; i++){
				pixels = (Object)in.readObject();
				stack.addSlice("",pixels);
			}
			image.setImage(index,new ImagePlus("",stack));
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		image.setMessage("Loading image:","Complete");
		in.close();
		is.close();
	}
    
}
