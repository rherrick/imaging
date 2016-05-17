//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.utils;

import java.net.*;
import java.io.*;

import org.nrg.plexiviewer.lite.gui.*;

public class PlexiSubscriberClientProxy  extends Thread {
		PlexiMessagePanel msgPanel;
		boolean update=false;
		String opt;
		private URL dataURL;
		private URLConnection servletConnection;
		private boolean stop=false;	
		private int inc;
		private boolean isConnected;
				
		public PlexiSubscriberClientProxy(PlexiMessagePanel mPanel, String opt) {
			this.opt = opt;
			msgPanel=mPanel;
			inc=1;
			isConnected = false;
		}

	private void openConnection() {
		 String suffix = HTTPDetails.getSuffix("PublisherServlet");
		 try {
			dataURL = HTTPDetails.getURL(HTTPDetails.host,suffix);
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
		 }catch(IOException ioe) {
			 ioe.printStackTrace();
		 }
	 }


	public void run() {
		  while(!stop) {
			try {
					//inc=inc+1;
					//msgPanel.showProgress(inc);
				retrieveMessage();
				sleep(40);
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		  }
	}  

	public void finish() {
		stop =  true;
		//msgPanel.resetMessages();
		//super.stop();
	}
		
	private void retrieveMessage() throws IOException {
		//System.out.println("Isconnected " + isConnected + " " + inc);
		openConnection();
		InputStream is = servletConnection.getInputStream();
		ObjectInputStream in =  new ObjectInputStream(is);
		 	try {
		    	Object message=in.readObject();
			  //System.out.println("Client Proxy recd msg " + message);
			  msgPanel.setMessage((String)message);
		   }catch(Exception e) {
			  msgPanel.setMessage("");
		   }
		is.close();
		in.close();
	}  
}
