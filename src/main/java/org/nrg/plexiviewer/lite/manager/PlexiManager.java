//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.manager;
import ij.ImagePlus;

import java.applet.Applet;
import java.applet.AppletContext;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.gui.PlexiMessagePanel;
import org.nrg.plexiviewer.lite.utils.CreateUtils;

public class PlexiManager implements PlexiManagerI {
	   
	   private static  Vector mrimages;
       private static AppletContext appletContext;
       public static boolean controlKeys = true; 
	   public static final String APPLET_NAME="PLEXIVIEWER";
	   private static Hashtable viewerSettings;
	
	   /** Creates new MRManager */
    
    
	   public PlexiManager(AppletContext a) {
           appletContext = a;
		   mrimages = new Vector();
		   viewerSettings = new Hashtable();
		   setSetting("SegImage.regionID", new Integer(-1));
	   }
    
      
       public static void show(URL url) {
           appletContext.showDocument(url);
       }

       public static void showRelative(String relativePath) {
           Applet a = appletContext.getApplet("PLEXIVIEWER");
           if (a != null) {
               if (!relativePath.startsWith("/")) relativePath = "/" + relativePath;
               String uStr = a.getCodeBase().toString();
               int i = uStr.indexOf("/applet");
               if (i != -1) {
                   try {
                       URL url = new URL(uStr.substring(0,i) + relativePath);
                       appletContext.showDocument(url);
                   }catch(Exception e) {}
               }
           }
       }

       public static Applet getApplet() {
    	   return appletContext.getApplet(APPLET_NAME);
       }
       
	   public int show(PlexiMessagePanel msgPanel, UserSelection uselection) {
	   		int rtn = 0;
	   		String imageViewerClassName = uselection.getImageViewerClass();
	   		try {
				Class[] intArgsClass = new Class[] {uselection.getClass()};
				Object[] intArgs = new Object[] {uselection};
				Constructor intArgsConstructor;
				Class imgViewerDefinition = Class.forName(imageViewerClassName);
				intArgsConstructor = imgViewerDefinition.getConstructor(intArgsClass);
				PlexiImageViewerI imgViewer = (PlexiImageViewerI) CreateUtils.createObject(intArgsConstructor, intArgs);
				rtn = imgViewer.open(msgPanel);
		   		if (rtn!=0)
		   			addToVector(imgViewer);
	   		} catch (ClassNotFoundException e) {
				System.out.println(e);
			} catch (NoSuchMethodException e) {
				System.out.println(e);
			}
			return rtn;
	   }

	public static  ImagePlus findImage(UserSelection uselection){
		if (mrimages!=null && !mrimages.isEmpty()) {
		  for (int i=0;  i<mrimages.size(); i++){
			PlexiImageViewerI vectorEntry = ((PlexiImageViewerI)(mrimages.elementAt(i)));
		  	 if (uselection.isIdentical(vectorEntry.getUserSelection())) {
		  	 	ImagePlus image = ((PlexiImageViewerI)(mrimages.elementAt(i))).getImagePlus();
				if (image!=null)
					return image;
			}
		  }
		} 
		return null;
	}	

	 public  void addImage(PlexiImageViewerI im){
		 mrimages.addElement(im);
	 }
	 
	 public void getVectorSize() {
	 	System.out.println("PlexiManager:: I have " + mrimages.size() + " Number of elements");
	 }
    
	 public  static void removeImage(PlexiImageViewerI im){
		 boolean ableToRemoveViewer = mrimages.removeElement(im);
		 decrementSegImageCount(im);
		 System.out.println("Manager removed the viewer " + ableToRemoveViewer);
		 im=null;	
		 mrimages.trimToSize();
		 Runtime.getRuntime().gc();
	 }

	/*public static synchronized void syncToScroll( String fromView, boolean fromRadiologic, String fromCoordinateSystem, String exptType, int currentSlice){
		if (mrimages == null)
			return;
	   PlexiImageViewerI mrimage;
	   int i;
		for(i=0; i < mrimages.size(); i++){
			mrimage = (PlexiImageViewerI) mrimages.elementAt(i);
		   // System.out.println(mrimage.getImagePlus().getTitle() + " " + mrimage.getManagerDetails().getExperiment().getType() + " " + mrimage.getLayout().getName() );
			if (mrimage.getUserSelection().getProject().equals(exptType) && mrimage.getLayout().getName().equalsIgnoreCase(fromCoordinateSystem)) {
				mrimage.updateCrossHair(fromView, currentSlice);			   	
			}
		}
    }*/
    
	 public static synchronized void syncStacks(int x, int y, int z, float ipvalue, boolean slice, String fromView, boolean fromRadiologic, String fromCoordinateSystem, String exptType, String dataType) {
		 if (mrimages == null)
			 return;
		PlexiImageViewerI mrimage;
		int i;
		 for(i=0; i < mrimages.size(); i++){
			 mrimage = (PlexiImageViewerI) mrimages.elementAt(i);
			// System.out.println(mrimage.getImagePlus().getTitle() + " " + mrimage.getManagerDetails().getExperiment().getType() + " " + mrimage.getLayout().getName() );
			 if (mrimage.getUserSelection().getProject().equals(exptType) && mrimage.getUserSelection().getDataType().equals(dataType) &&  mrimage.getLayout().getName().equalsIgnoreCase(fromCoordinateSystem)) {
			 	mrimage.adjustCoords(x,y,z, ipvalue, slice, fromView, fromRadiologic);
			 }else {
			 	System.out.println("The values dont match:" + exptType + ": " + mrimage.getUserSelection().getProject() + " " + mrimage.getUserSelection().getDataType() + " " + dataType);
			 }
		 }
	 }
	 
	public static  void syncSegStacks(int x, int y, int z, float ipvalue,  String fromView, boolean fromRadiologic, String fromCoordinateSystem, String imageViewerClassName, int region, String regionName) {
		if (mrimages == null)
			return;
	   PlexiImageViewerI mrimage;
		for(int i=0; i < mrimages.size(); i++){
			mrimage = (PlexiImageViewerI) mrimages.elementAt(i);
			if (mrimage.getLayout().getName().equalsIgnoreCase(fromCoordinateSystem)) {
				String imageViewerClass = mrimage.getUserSelection().getImageViewerClass();
				if (imageViewerClassName.equalsIgnoreCase(imageViewerClass))
			   		mrimage.adjustRegions(x,y,z, ipvalue, true, regionName, fromRadiologic, region);
			}	
		}
	}
	 
    	   
	/**
	 * @return
	 */
	
	private  void addToVector(PlexiImageViewerI imgViewer) {
		Enumeration e = mrimages.elements();
		boolean exists = false;
		while (e.hasMoreElements()) {
			PlexiImageViewerI mrimage = (PlexiImageViewerI) e.nextElement();
			if (imgViewer.getUserSelection().isIdentical(mrimage.getUserSelection())) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			mrimages.addElement(imgViewer);
			incrementSegImageCount(imgViewer);
		}
	}

	public static void decrementSegImageCount(PlexiImageViewerI imgViewer) {
		if (imgViewer instanceof org.nrg.plexiviewer.lite.viewer.Seg.SegImage ||
			imgViewer instanceof org.nrg.plexiviewer.lite.viewer.MiriadEmSeg.SegImage ||
			imgViewer instanceof org.nrg.plexiviewer.lite.viewer.MiriadLobarSeg.SegImage ||
			imgViewer instanceof org.nrg.plexiviewer.lite.viewer.MiriadMrxSeg.SegImage
        ) {
			Integer cnt = (Integer)getSetting("SegImageCount");
			int i=0;
			if (cnt!=null) i=cnt.intValue()-1;
			if (i==0) {
				setSetting("SegImage.regionID", new Integer(-1));
			}
			setSetting("SegImageCount",new Integer(i));
		}				
	}
	

	public static void incrementSegImageCount(PlexiImageViewerI imgViewer) {
		if (imgViewer instanceof org.nrg.plexiviewer.lite.viewer.Seg.SegImage ||
			imgViewer instanceof org.nrg.plexiviewer.lite.viewer.MiriadEmSeg.SegImage ||
			imgViewer instanceof org.nrg.plexiviewer.lite.viewer.MiriadLobarSeg.SegImage ||
			imgViewer instanceof org.nrg.plexiviewer.lite.viewer.MiriadMrxSeg.SegImage
        ) {
			Integer cnt = (Integer)getSetting("SegImageCount");
			int i;
			if (cnt == null) i=0;
			else i=cnt.intValue(); 
			setSetting("SegImageCount",new Integer(i+1));
		}				
	}

	public static Object getSetting(String key) {
		Object rtn=null;
		if (viewerSettings.containsKey(key))
			rtn=viewerSettings.get(key);
		//System.out.println("Key " + key + " Value " + rtn);	
		return rtn;	
	}

	public static void setSetting(String key, Object value) {
		viewerSettings.put(key,value);
	}
	
}
