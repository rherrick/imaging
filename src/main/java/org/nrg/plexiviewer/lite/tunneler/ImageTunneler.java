//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.tunneler;

import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.StackWindow;
import ij.measure.Calibration;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.display.MontageDisplay;
import org.nrg.plexiviewer.lite.manager.PlexiImageViewerI;
import org.nrg.plexiviewer.lite.ui.LoadStatus;
import org.nrg.plexiviewer.lite.utils.HTTPDetails;


public class ImageTunneler implements Runnable {
    private URL dataURL;
    PlexiImageViewerI  image;
    private String host;
    private URLConnection servletConnection;
	private boolean windowShowing;    
	Thread queryRetriever;
	UserSelection userSelection;
	
	public ImageTunneler(PlexiImageViewerI  image, String host) {
		this.image = image;
		this.host = host;
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
    
    public void load() {
		userSelection = (UserSelection)image.getUserSelection().clone();
 		queryRetriever = new Thread(this,"ImageTunneller");
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
		   image.setMessage("Loading image:","Receiving data...");
		   LoadStatus status = (LoadStatus)in.readObject();
		   if (!status.isSuccess()){
			   image.setMessage("Loading image:","Error");
			   image.setMessage("Error","Unable to find the image");
			   //PlexiImageFile pf = new PlexiImageFile();
			   //pf.setDimX(200); pf.setDimY(200); pf.setDimZ(0);
			   //image.setWindowDimensions(pf);
			   //LoadErrorDialog led = new LoadErrorDialog(image.getImagePlus().getWindow(), status.getMessage(), false);
			   //led.show(); 
			   //image.getImagePlus().getWindow().close();
			   return;
		   }
						
		   int nSlices = status.getCount();
		   int width = status.getDimensions().width;
		   int height = status.getDimensions().height;
		   image.setMessage("Loading image:","Connected to server...");
		   Object pixels;
		   ImageStack stack = new ImageStack(width,height);
		   ImagePlus mrip = image.getImagePlus();
           Calibration cal = mrip.getCalibration();
           if (userSelection.getFile().getVoxelResX()!= -1)
               cal.pixelWidth = userSelection.getFile().getVoxelResX();
           if (userSelection.getFile().getVoxelResY()!= -1)
               cal.pixelHeight = userSelection.getFile().getVoxelResY();
           if (userSelection.getFile().getVoxelResZ()!= -1)
               cal.pixelDepth = userSelection.getFile().getVoxelResZ();
           
           
		   MontageDisplay display = (MontageDisplay)in.readObject();
		   for (int i =0; i<nSlices; i++){
			   pixels = (Object)in.readObject();
			   stack.addSlice("",pixels);
			   if (mrip!=null) {
				   if (nSlices == 1) {
					   if (i == 0){
							mrip.setStack("",stack);
						   WindowManager.addWindow(mrip.getWindow());
					   }
				   }else {
					   if (i == 1){
							mrip.setStack("",stack);
						   WindowManager.addWindow(mrip.getWindow());
					   }
				   }
				   if (i>0){
						mrip.setSlice(i);
						ImageProcessor ip = mrip.getProcessor();
						 if ((ip instanceof ShortProcessor) || (ip instanceof FloatProcessor)) {
								 ip.resetMinAndMax();
						 }		
							if (i>1) {
								try {
						            ((StackWindow)mrip.getWindow()).updateSliceSelector();
								}catch(ClassCastException cce){cce.printStackTrace();}
							}
						}
			   } 
				if (image != null)
				   image.setMessage("Loading image:", "Slice " + (i+1) + "/" + nSlices);
		   }
		   int midSlice = (int)(stack.getSize()/2);
		   if (midSlice == 0) midSlice =1;
		   mrip.setSlice(midSlice);
		  // System.out.println("MidSlice is " + midSlice);
		   ImageProcessor ip = mrip.getProcessor();
		   double min = ip.getMin();
		   double max = ip.getMax();
			if ((ip instanceof ShortProcessor) || (ip instanceof FloatProcessor)) {
					ip.resetMinAndMax();
		    }		
		   ip.setMinAndMax(min, max);
			mrip.getProcessor().resetMinAndMax();
			mrip.updateAndDraw();	   
            mrip.setCalibration(cal);
	   } catch(ClassNotFoundException cnfe) {
		   cnfe.printStackTrace();
	   }catch (OutOfMemoryError ome) {
	  	 image.handleOutOfMemoryError();
  		}
	   image.setMessage("Loading image:","Complete");
	   if (windowShowing) {
		   image.setWaitCursor(false);
		   image.getImagePlus().updateAndRepaintWindow();
	   }
	   in.close();
	   is.close();
   }

}
