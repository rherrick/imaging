//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.nrg.plexiviewer.utils.*;
import org.nrg.plexiviewer.manager.*;

public class PublisherServlet extends HttpServlet {
	
	/** Initializes the servlet.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
    
	/** Destroys the servlet.
	 */
	public void destroy() {
        
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, java.io.IOException  {
		long startTime = System.currentTimeMillis();
		ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
		try{
			String contentType = "application/x-java-serialized-object";
			response.setContentType(contentType);
			ObjectInputStream inputFromClient = new ObjectInputStream(request.getInputStream());
			String options = (String) inputFromClient.readObject();
			PlexiSubscriberProxy subscriber = PlexiStatusPublisherManager.GetInstance().getSubscriberProxy(options);
            if (PlexiStatusPublisherManager.GetInstance().isHandled(options)!=null) {
				if (((Boolean)PlexiStatusPublisherManager.GetInstance().isHandled(options)).booleanValue()) {
					out.writeObject("About to receive image....Please wait");
					out.flush();
				}else if(subscriber!=null) {
					subscriber.register();
					if (subscriber.hasUpdate()) {
						out.writeObject(subscriber.getMessage());
						out.flush();
					} 
				}else {
					out.writeObject("Processing Request....Please wait");
					out.flush();
				}
			}else {
				if(subscriber!=null) {
                    subscriber.register();
					if (subscriber.hasUpdate()) {
						out.writeObject(subscriber.getMessage());
						out.flush();
					}
				}else {
					out.writeObject("");
					out.flush();
				}
			}
			long endTime = System.currentTimeMillis();
			out.close();
			//System.out.println("P servlet total time " +  (endTime-startTime)/1000);
		}catch(Exception e) {
			out.writeObject("Encountered some problem.....");
			out.flush();
			out.close();
			return;
		}
	}
	
	/** Handles the HTTP <code>GET</code> method.
		 * @param request servlet request
		 * @param response servlet response
		 */
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {
			System.out.println("do Get called");
			processRequest(request, response);
			
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
			return "Publisher Servlet";
		}
}
