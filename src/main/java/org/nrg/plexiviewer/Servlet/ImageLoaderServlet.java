/*
 * org.nrg.plexiViewer.Servlet.ImageLoaderServlet
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.Servlet;

/**
 * @author Mohana
 *
 */

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nrg.plexiviewer.io.ImageLocator;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.display.MontageDisplay;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.utils.ImageDetails;
import org.nrg.plexiviewer.lite.xml.Layout;
import org.nrg.plexiviewer.lite.xml.MontageView;
import org.nrg.plexiviewer.manager.PlexiStatusPublisherManager;
import org.nrg.plexiviewer.utils.ImageUtils;
import org.nrg.plexiviewer.utils.MailUtils;
import org.nrg.plexiviewer.utils.XnatLoginDetails;
import org.nrg.xdat.security.XDATUser;

public class ImageLoaderServlet extends HttpServlet{
	/** Initializes the servlet.
		 */
	
		public void init(ServletConfig config) throws ServletException {
			super.init(config);
		}
    
		/** Destroys the servlet.
		 */
		public void destroy() {
        
		}
		
	/** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
		 * @param request servlet request
		 * @param response servlet response
		 */
		protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {
			long startTime = System.currentTimeMillis();
			ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
			boolean inCache=false;
			PlexiImageFile pf = new PlexiImageFile();
			String format=null;
			String cloneWithoutFiles="";
			String requestDetails = "";
			XDATUser user = new XnatLoginDetails().getSessionUser(request);

			try{
				System.out.println("Image Loader Servlet invoked \n");
				String contentType = "application/x-java-serialized-object";
				response.setContentType(contentType);
				ObjectInputStream inputFromClient = new ObjectInputStream(request.getInputStream());
				UserSelection options = (UserSelection) inputFromClient.readObject();
				cloneWithoutFiles = options.toString();
				requestDetails = "Session Id : " + options.getSessionId() +  " Orientation: " + options.getOrientation(); 
 				PlexiStatusPublisherManager.GetInstance().createPublisher(cloneWithoutFiles);
				PlexiStatusPublisherManager.GetInstance().createSubscriber(cloneWithoutFiles);
				PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Procuring requested Image");
				MontageView mView = null;
				Layout layout = null;
				MontageDisplay mDisplay = null;
				ImageLocator imageLocator = new ImageLocator();
				pf = imageLocator.getImage(options);
				if (pf==null) {
					PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Couldnt launch image builder");
					System.out.println("Couldnt launch the conveter");
					MailUtils.send("PlexiViewer ImageLoader Error", "User " + user.getFirstname() + " " + user.getLastname() + " (" + user.getLogin() + ") from " + request.getRemoteAddr() +  " couldnt fetch the requested file.\n " + requestDetails);
					out.writeObject(mDisplay);
					out.flush();
					out.writeObject(pf);
					out.flush();
					return;
				}
				format = pf.getFormat();
	            layout = ImageDetails.getLayout(pf.getDimX(), pf.getDimY(), pf.getDimZ(), pf.getVoxelResX(), pf.getVoxelResY(), pf.getVoxelResZ(), pf.getFormat());
	            if (mView == null) {
	                mView = new MontageView();
	                mView.setScale(MontageView.SCALE);
	                Hashtable attribs = ImageUtils.getSliceIncrement(pf.getDimZ());
	                mView.addViewInfo(options.getOrientation().toUpperCase(),((Integer)attribs.get("startslice")).intValue(),((Integer)attribs.get("endslice")).intValue(),((Integer)attribs.get("increment")).intValue());
	                mDisplay = new MontageDisplay(mView, options.getOrientation().toUpperCase(), layout);
	            }

                    //System.out.println("Montage View " + mView.toString());
                    //System.out.println("Montage Display " + mDisplay.toString() );
					System.out.println("Image Loader Servlet is pointing to Hi RES file in " + pf.toString());
					out.writeObject(mDisplay);
					out.flush();
					out.writeObject(pf);
					out.flush();
				System.gc();
				System.out.println("ImageLoaderServlet is about to return. Total time (s) ");
			} catch (Exception e){
				System.out.println("Handling exception...");
				PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Error occured");
				e.printStackTrace();
				MailUtils.send("PlexiViewer ImageLoader Exception",  "User " + user.getFirstname() + " " + user.getLastname() + " (" + user.getLogin() + ")  couldnt fetch the requested file. \n " + requestDetails + "\n Stack Trace: \n" + getStackTraceVector(e,"string"));
				MontageDisplay mDisplay =null;
				pf=null;
				out.writeObject(mDisplay);
				out.flush();
				out.writeObject(pf);
				out.flush();
			}
		}
  
    
		public static final Object getStackTraceVector(Exception e, String rtnType) {
			ByteArrayOutputStream byteStream = null;
			PrintWriter printWriter = null;
			String stackTrace = null;
			Vector stackArray = new Vector();
        
			byteStream = new ByteArrayOutputStream();
			printWriter = new PrintWriter(byteStream, true);
        
			e.printStackTrace(printWriter);
        
			printWriter.flush();
        
			stackTrace = byteStream.toString();
        
			printWriter.close();
        
			if (rtnType.equalsIgnoreCase("vector")) {
				StringTokenizer tok = new StringTokenizer(stackTrace,"\n");
				while(tok.hasMoreTokens()){
					String s = tok.nextToken();
					stackArray.add(s);
				}
				return(stackArray);
			}else 
				return stackTrace;
		}
    
    
		/** Handles the HTTP <code>GET</code> method.
		 * @param request servlet request
		 * @param response servlet response
		 */
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {
		
		}
    
		/** Handles the HTTP <code>POST</code> method.
		 * @param request servlet request
		 * @param response servlet response
		 */
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {
			processRequest(request, response);
		}
    
		/** Returns a short description of the servlet.
		 */
		public String getServletInfo() {
			return "Image Loader Servlet";
		}
    

}
