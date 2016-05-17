//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Servlet;

import ij.ImagePlus;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nrg.plexiviewer.io.ImageLoader;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.image.SerializableCalibration;
import org.nrg.plexiviewer.lite.ui.LoadStatus;
import org.nrg.plexiviewer.manager.PlexiStatusPublisherManager;
import org.nrg.plexiviewer.utils.MailUtils;
import org.nrg.plexiviewer.utils.PlexiPublisher;


public class ImageDistributorServlet extends HttpServlet {
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
		ImagePlus image=null;
		String cloneWithoutFiles="";
		UserSelection options =null;
		try{
			System.out.println("Image Distributor Servlet invoked \n");
			
			String contentType = "application/x-java-serialized-object";
			response.setContentType(contentType);
		
			ObjectInputStream inputFromClient = new ObjectInputStream(request.getInputStream());
			options= (UserSelection) inputFromClient.readObject();
			cloneWithoutFiles = options.toString();

			//System.out.println("ImageDistributorServlet userSelection " + cloneWithoutFiles);
			//PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Procuring Required Image");
			ImageLoader loader = new ImageLoader(options);
			image = loader.getImage();
			  
			if( image ==  null){
				PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Image not available.....");
				System.out.println("The image is null");
				Vector list = new Vector();
				list.add("Image not available");
				LoadStatus status = new LoadStatus(false,list,0,0,0,null,0);
				PlexiStatusPublisherManager.GetInstance().setHandled(cloneWithoutFiles);
				PlexiStatusPublisherManager.GetInstance().removePublisher(cloneWithoutFiles);
				PlexiStatusPublisherManager.GetInstance().removeSubscriberProxy(cloneWithoutFiles);
				out.writeObject(status);
				out.flush();
				return;
			}
			
			SerializableCalibration scal = null;
			int fileType=0;
			if (options.getDisplay().equalsIgnoreCase("STACK")) {
				if (image.getCalibration()!=null)
				scal = new SerializableCalibration(image.getCalibration());
				if (image.getOriginalFileInfo()!=null)
					fileType = image.getOriginalFileInfo().fileType;	
			}
			System.out.println("ImageDistributorServlet::Tunnel Max is " + image.getProcessor().getMax() + " Min is " + image.getProcessor().getMin());

			PlexiPublisher pb = PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles);
			if (pb!=null) pb.setValue("Delivering Image.....Please wait");
			int count = image.getStackSize();
			int width = image.getWidth();
			int height = image.getHeight();
			Object [] stack = image.getStack().getImageArray();
			Vector list = new Vector();
			list.add("no problems");
			LoadStatus status = new LoadStatus(true,list,count,width,height, scal, fileType);
			Object pixels;
			out.writeObject(status);
			out.flush();
			out.writeObject(loader.getMontageDisplay());
			out.flush();
			System.out.println("The stack size is " + count + "\n");

           // out.writeObject(scal);
           // out.flush();
            
		    for (int i =0; i < count; i++){
				pixels = stack[i];
				out.writeObject(pixels);
				out.flush();
			}
			pixels=null;
			for (int i =0; i < count; i++){
				stack[i]=null;	
			}
			PlexiStatusPublisherManager.GetInstance().setHandled(cloneWithoutFiles);
			PlexiStatusPublisherManager.GetInstance().removePublisher(cloneWithoutFiles);
			PlexiStatusPublisherManager.GetInstance().removeSubscriberProxy(cloneWithoutFiles);
			long endTime = System.currentTimeMillis();
			if (image.lock()) image.unlock();
			image.flush();
			image=null;
			System.gc();
			System.out.println("ImageDistributorServlet is about to return. Total time (s) " + (endTime-startTime)/1000);
        }catch (Exception e){
			if (image!=null)image.flush();
			System.out.println("Handling exception..." + e.getClass().getName());
			if (e.getClass().getName().endsWith(".ClientAbortException")) {
			    System.out.println("Ignoring client abort exception from user with IP " + request.getRemoteAddr());
            }else {
    			MailUtils.send("PlexiViewer ImageDistributor Exception", "User from " + request.getRemoteAddr() + " ImageDistributor encountered an exception. \n Session Id:" + options.getSessionId() + " Scan No:"+ options.getScanNo()+ " \n Stack trace:\n " + getStackTraceVector(e,"string"));
    			PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Error occured");
    			LoadStatus status = new LoadStatus(false,(Vector)getStackTraceVector(e,"vector"),0,0,0,null,0);
    			out.writeObject(status);
            }
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
		return "Image Distributor Servlet";
	}
    
}
