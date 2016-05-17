//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.tunneler;

import java.net.*;
import java.io.*;
import ij.*;
import org.nrg.plexiviewer.lite.manager.*;
import org.nrg.plexiviewer.lite.utils.HTTPDetails;
import org.nrg.plexiviewer.lite.ui.*;
import org.nrg.plexiviewer.lite.display.*;
import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.lite.*;


public class NoMsgImageTunneler implements Runnable {
    private URL dataURL;
    private String host;
    private URLConnection servletConnection;
	private boolean windowShowing;    
	Thread queryRetriever;
	PlexiImageViewerI  image;
	UserSelection userSelection;
	int index;
		
	public NoMsgImageTunneler(  PlexiImageViewerI  i) {
		image=i;
		this.host = HTTPDetails.getHost();
		windowShowing=false;
		userSelection=null;
	}
    
    private void openConnection() {
		String suffix = HTTPDetails.getSuffix("ImageDistributorServlet");
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
			outStreamToServlet.writeObject(userSelection);	
			outStreamToServlet.flush();
			outStreamToServlet.close();
		} catch(MalformedURLException mfe) {
			mfe.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
    }
    

	public void load(UserSelection u, int i) {
		index =i;
		userSelection = u;
		queryRetriever = new Thread(this,"NoMsgImageTunneler");
		queryRetriever.start();
	}

    
    public void run() {
       	try {
   	        retrieveImage();
       } catch(IOException ioe) {
       	    ioe.printStackTrace();
       	}catch (OutOfMemoryError ome) {
			image.handleOutOfMemoryError();
   		}
    }
    
	private void retrieveImage() throws IOException {
	   openConnection();
	   InputStream is = servletConnection.getInputStream();
	   ObjectInputStream in =  new ObjectInputStream(is);
	   try {
		   LoadStatus status = (LoadStatus)in.readObject();
		   if (!status.isSuccess()){
			   PlexiImageFile pf = new PlexiImageFile();
			   pf.setDimX(200); pf.setDimY(200); pf.setDimZ(0);
			   //LoadErrorDialog led = new LoadErrorDialog(image.getImagePlus().getWindow(), status.getMessage(), false);
			   //led.show(); 
			   //image.getImagePlus().getWindow().close();
			   return;
		   }
						
		   int nSlices = status.getCount();
		   int width = status.getDimensions().width;
		   int height = status.getDimensions().height;
		   Object pixels;
		   ImageStack stack = new ImageStack(width,height);
		   MontageDisplay display = (MontageDisplay)in.readObject();

		   for (int i =0; i<nSlices; i++){
			   pixels = (Object)in.readObject();
			   stack.addSlice("",pixels);
		   }
		   image.setImage(index,stack); 
	   } catch(ClassNotFoundException cnfe) {
		   cnfe.printStackTrace();
	   } catch (OutOfMemoryError ome) {
	   		image.handleOutOfMemoryError();
	   }
	   in.close();
	   is.close();
   }

}
