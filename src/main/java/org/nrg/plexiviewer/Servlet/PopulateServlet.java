/*
 * org.nrg.plexiViewer.Servlet.PopulateServlet
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
import java.io.ObjectOutputStream;
import java.io.ObjectStreamConstants;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nrg.plexiviewer.utils.Session;
import org.nrg.xdat.bean.XnatImagesessiondataBean;

public class PopulateServlet  extends HttpServlet  {
	
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
		System.out.println("Recd the post\n");
	 }
		 
		 
	/** Handles the HTTP <code>POST</code> method.
		   * @param request servlet request
		   * @param response servlet response
		   */
	  protected void doPost(HttpServletRequest request, HttpServletResponse response)
	  throws ServletException, java.io.IOException {
		  processRequest(request, response);
	  }
	
	/** Handles the HTTP <code>GET</code> method.
			 * @param request servlet request
			 * @param response servlet response
			 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, java.io.IOException {
		String contentType = "application/x-java-serialized-object";
		response.setContentType(contentType);
		String sessionId = request.getParameter("sessionId");
		//Integer exptId = new Integer(Integer.parseInt(request.getParameter("exptId")));
		Session mrSession = new Session(sessionId);
		XnatImagesessiondataBean rtn=mrSession.get();
		ObjectOutputStream outToApplet = new ObjectOutputStream(response.getOutputStream());
		outToApplet.useProtocolVersion(ObjectStreamConstants.PROTOCOL_VERSION_1);
		outToApplet.writeObject(rtn);
		outToApplet.flush();
		outToApplet.close();
	}

}
