//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Servlet;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nrg.plexiviewer.Reader.ReadFileContents;
import org.nrg.plexiviewer.io.IOHelper;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.utils.LiteFileUtils;
import org.nrg.plexiviewer.utils.ArchivePathManager;
import org.nrg.plexiviewer.utils.PlexiConstants;
import org.nrg.plexiviewer.utils.URIUtils;

public class GetRecFileContents extends HttpServlet {
	
		  /** Initializes the servlet */
		  public void init(ServletConfig config) throws ServletException {
			  super.init(config);
    
		  }
    
		  /** Destroys the servlet  */
		  public void destroy() {
    
		  }
			  
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, java.io.IOException {
		
	} 
			  
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, java.io.IOException {
		ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());			
		try {
			System.out.println("GetRecFileContents Servlet invoked ");
			String contentType = "application/x-java-serialized-object";
			ObjectInputStream inputFromClient = new ObjectInputStream(request.getInputStream());
			UserSelection userOptions = (UserSelection) inputFromClient.readObject();
			String rtn; PlexiImageFile pf=null;
	        String cachePathLocation = ArchivePathManager.GetInstance().getCachePathLocation(userOptions.getProject(), userOptions.getSessionLabel());

			if (userOptions.hasFile()) 
				 pf = userOptions.getFile();
			else if (userOptions.hasXnatFile()) {
				pf = IOHelper.getPlexiImageFile(userOptions.getXnatFile(), cachePathLocation, userOptions.getFormattedWindowTitle() + "_"+ LiteFileUtils.getFileName(userOptions.getXnatFile()));
			}
			if (pf.getXsiType().equals(PlexiConstants.PLEXI_IMAGERESOURCE) || pf.getXsiType().equals(PlexiConstants.XNAT_IMAGERESOURCE)) {
			    URIUtils u = new URIUtils(pf.getURIAsString());
                String path = u.getPath();
                String name = u.getName();
                String recFileName = path + File.separator + name + ".rec";
                File  recFile = new File(recFileName);
                out.writeObject(ReadFileContents.getContents(recFile));
                out.flush();
            }
		} catch (Exception e){
			System.out.println("Handling exception... ");
			out.writeObject("Rec File Details not available\n");
			out.flush();
		}
	}
					
    
	  /** Returns a short description of the servlet.
	   */
	  public String getServletInfo() {
		  return "Short description";
	  }
    				
}
