/*
 * org.nrg.plexiViewer.Servlet.MontageImageLoaderServlet
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
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.lite.*;
import org.nrg.plexiviewer.lite.display.*;
import org.nrg.plexiviewer.manager.PlexiStatusPublisherManager;
import org.nrg.plexiviewer.io.*;
import org.nrg.plexiviewer.utils.MailUtils;
import ij.*;

public class MontageImageLoaderServlet extends HttpServlet{
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

			PlexiImageFile pf = new PlexiImageFile();
			try{
				System.out.println("Montage Image Loader Servlet invoked \n");
				String contentType = "application/x-java-serialized-object";
				response.setContentType(contentType);
				ObjectInputStream inputFromClient = new ObjectInputStream(request.getInputStream());
				UserSelection options = (UserSelection) inputFromClient.readObject();
				Boolean sendImage = (Boolean)inputFromClient.readObject();

				String cloneWithoutFiles= options.toString();

				PlexiStatusPublisherManager.GetInstance().createPublisher(cloneWithoutFiles);
					PlexiStatusPublisherManager.GetInstance().createSubscriber(cloneWithoutFiles);
					PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Procuring requested Image");

				
				ImageLocator imageLocator = new ImageLocator();
				pf = imageLocator.getImage(options);

				options.setFile(pf);
				ImageLoader loader = new ImageLoader(options);
				ImagePlus image = loader.getImage();
				MontageDisplay mDisplay = loader.getMontageDisplay();
				pf=loader.getPlexiImageFile();  

				if( image ==  null){
					System.out.println("The image is null");
					MailUtils.send("PlexiViewer MontageImageLoader Error", " Couldnt fetch the requested file " + options);
					out.writeObject(mDisplay);
					out.flush();
					out.writeObject(pf);
					out.flush();
					return;
				}
				//System.out.println("MOntageLoadre Servlet :: OPened file is " + pf.toString());
				pf.setFileType(image.getOriginalFileInfo().fileType);
				pf.setDimX(image.getWidth()); pf.setDimY(image.getHeight()); pf.setDimZ(image.getStackSize());
					
				Object [] stack = image.getStack().getImageArray();
				out.writeObject(mDisplay);
				out.flush();
				out.writeObject(pf);
				out.flush();
				if (sendImage.booleanValue()) {
					Object pixels;
					for (int i =0; i < stack.length; i++){
						   pixels = stack[i];
						   out.writeObject(pixels);
						   out.flush();
					  }
				}  
			   image.flush();	
				System.gc();
				System.out.println("MontageImageLoaderServlet is about to return. Total time (s) ");
			} catch (Exception e){
				System.out.println("Handling exception...");
				e.printStackTrace();
				MailUtils.send("PlexiViewer MontageImageDistributor Exception", "MontageImageDistributor encountered an exception " + e.getMessage());
				MontageDisplay mDisplay =null;
				pf=null;
				out.writeObject(mDisplay);
				out.flush();
				out.writeObject(pf);
				out.flush();
			}
		}
  
    
		public static final Vector getStackTraceVector(Exception e) {
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
        
			StringTokenizer tok = new StringTokenizer(stackTrace,"\n");
			while(tok.hasMoreTokens()){
				String s = tok.nextToken();
				stackArray.add(s);
			}
			return(stackArray);
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
