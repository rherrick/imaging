//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.gui;


import ij.*;
import ij.process.*;
import ij.measure.*;
import org.nrg.plexiviewer.lite.utils.StringUtils;
import java.net.*; 
import java.io.*;
import org.nrg.plexiviewer.lite.utils.HTTPDetails;
import org.nrg.plexiviewer.lite.*;

public class PlexiFileInfo  {
    private ImagePlus imp;
	private PlexiTextWindow twin;
	private UserSelection uselection;
	private String host;
	//public static Hashtable infoWindows = new Hashtable();
	
	public PlexiFileInfo() {
		twin= null;
		host=null;
		uselection=null;
	}

	public PlexiFileInfo(ImagePlus img) {
		imp = img;
		twin = null;
		host=null;
		uselection=null;
	}	

	public PlexiFileInfo(ImagePlus img, String host, UserSelection u) {
		imp = img;
		twin = null;
		this.host=host;
		uselection=u;
	}	


	public void show() {
		showInfo();
	}

	public void showInfo() {
		String info = getImageInfo();
		if (info.indexOf("----")>0)
			showInfo(info, 400, 500);
		else
			showInfo(info, 300, 300);
	}
	

    public String getImageInfo() {
        String infoProperty = null;
        if (imp.getStackSize()>1) {
            ImageStack stack = imp.getStack();
            String label = stack.getSliceLabel(imp.getCurrentSlice());
            if (label!=null && label.indexOf('\n')>0)
                infoProperty = label;
        }
        if (infoProperty==null)
            infoProperty = (String)imp.getProperty("Info");
        String info = getInfo();
        if (infoProperty!=null)
            return infoProperty + "\n------------------------\n" + info;
        else
            return info;
        
    }

	String getInfo() {
		return getInfo(imp, imp.getProcessor()); 
	}


	

    String getInfo(ImagePlus imp, ImageProcessor ip) {
        String s = new String("\n");
        s += "Title: " + imp.getTitle() + "\n";
        s+= "(Note::Image dimensions may not be exact)\n";
        Calibration cal = imp.getCalibration();
        int nSlices = imp.getStackSize();
//       System.out.println("The XNATFILEINFO W:" + cal.pixelWidth + "\t H:" + cal.pixelHeight + "\t D: " +cal.pixelDepth);
 //      System.out.println("Cal is scaled: " + cal.scaled() + "\t Myown: " +(cal.pixelWidth!=1.0 || cal.pixelHeight!=1.0 || cal.pixelDepth!=1.0 ));
      
            String unit = cal.getUnit();
            String units = cal.getUnits();
            s += "Width:  "+StringUtils.d2s(imp.getWidth(),2)+" " + units+" ("+imp.getWidth()+")\n";
            s += "Height:  "+StringUtils.d2s(imp.getHeight()*cal.pixelHeight,2)+" " + units+" ("+imp.getHeight()+")\n";
            if (nSlices>1)
                s += "Depth:  "+StringUtils.d2s(nSlices*cal.pixelDepth,2)+" " + units+" ("+nSlices+")\n";                            
            if (nSlices>1)
                s += "Voxel size: "+ cal.pixelWidth + "x" + StringUtils.d2s(cal.pixelHeight,2)+"x"+StringUtils.d2s(cal.pixelDepth,2) + "\n";             
			
			if (host!=null && uselection!=null) {
				String rec = getRecFileDetails();
				if (!rec.equals("Rec File Details not available\n")) {
					s+= "\n--------------REC FILE DETAILS--------------\n"; 
					s+= rec;
					s+= "\n--------------END REC FILE DETAILS--------------\n";
				}	
			}
 
        /*FileInfo fi = imp.getOriginalFileInfo();
        if (fi!=null) {
            if (fi.directory!=null && fi.fileName!=null) {
                s += "Path: " + fi.directory + fi.fileName + "\n";
            }
            if (fi.url!=null && !fi.url.equals("")) {
                s += "URL: " + fi.url + "\n";
            }
        }*/
        return s;
    }
    
    
    private String getRecFileDetails() {
    	//Connect to servlet to get the Rec File Details of the associated Hi-Res File 
    	//Defaults to sending a "\n" string back if no rec file is present
		String suffix = HTTPDetails.getSuffix("GetRecFileContents");
		URL dataURL;
		URLConnection servletConnection;
		try {
				String rtn="\n";	
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
				outStreamToServlet.writeObject(uselection);
				outStreamToServlet.flush();
				outStreamToServlet.close();
				InputStream is = servletConnection.getInputStream();
					//System.out.println("is: " + is);
				ObjectInputStream in =  new ObjectInputStream(is);
				rtn = (String)in.readObject();
				in.close();
				is.close();
				//System.out.println("Rtn::" + rtn + "::");
				if (rtn==null || (rtn.equals("")))
					rtn = "Rec File Details not available\n";
				return rtn;	
			} catch(Exception e) {
				//e.printStackTrace();
				return "Rec File Details not available\n";
			}	
	  }

   public  void showInfo(String info, int width, int height) {
      	String winTitle = "Info for "+imp.getTitle();
      //	if (!infoWindows.containsKey(winTitle))
      		twin = new PlexiTextWindow(winTitle, info, width, height);
		//PlexiNoFrillsTextWindow tw = new PlexiNoFrillsTextWindow(winTitle);
		//tw.show("Came into show info");
    }
}
