/*
 * org.nrg.plexiViewer.lite.xml.PlexiViewerSpecForSession
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.xml;

/**
 * @author Mohana
 *
 */
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.nrg.plexiviewer.utils.ArchivePathManager;
public class PlexiViewerSpecForSession implements Cloneable{
	private String defaultLoResType;
	private Hashtable viewableItemHash;
	private String project;
    
	/**
     * @return Returns the project.
     */
    public String getProject() {
        return project;
    }

    /**
     * @param project The project to set.
     */
    public void setProject(String project) {
        this.project = project;
    }

  
    public String getArchiveLocation() {
        return ArchivePathManager.GetInstance().getArchivePathLocation(project);
    }

    public String getCacheLocation() {
     return ArchivePathManager.GetInstance().getCachePathLocation(project, null);
    }

//    public String getLoResArchiveLocation() {
  //      return ArchivePathManager.GetInstance().getLoResLocation(project);
  // }
    
    public String getLoResArchiveLocation(String sessionId) {
        return ArchivePathManager.GetInstance().getLoResLocation(project, sessionId);
   }

    public String getThumbnailArchiveLocation() {
        return ArchivePathManager.GetInstance().getThumbnailLocation(project);
   }
    
    public PlexiViewerSpecForSession() {
		viewableItemHash = new Hashtable();
	}
	

	/**
	 * @return
	 */
	public Hashtable getViewableItemHash() {
		return viewableItemHash;
	}


	/**
	 * @param hashtable
	 */
	public void setViewableItemHash(Hashtable hashtable) {
		viewableItemHash = hashtable;
	}
	
	public ViewableItem getViewableItem(String type) {
        ViewableItem rtn = (ViewableItem)viewableItemHash.get(type);
        if (rtn == null) 
            rtn = new ViewableItem();
        return rtn;
    }
	public void addViewableItem (ViewableItem v) {
		viewableItemHash.put(v.getType(),v);
	}
	
	public Vector getDropDownItems(String type) {
		Vector rtn = new Vector();
		ViewableItem v = getViewableItem(type);
		Vector linkedVItems = v.getUserInterface().getLinkedDropDownList();
		for (int i=0;i<linkedVItems.size();i++) {
			String vtype = ((LinkedDropDown)linkedVItems.elementAt(i)).getViewableItemType();
			ViewableItem vi = getViewableItem(vtype);
			rtn.add(i,vi.getUserInterface().getDisplayText());		
		}
		return rtn;
	}
	/**
	 * @return
	 */
	public String getDefaultLoResType() {
		return defaultLoResType;
	}

	public int getNextAvailableSelectionPriority() {
		int rtn = -1;
		Enumeration keys = viewableItemHash.keys();
		while (keys.hasMoreElements()) {
			String viType = (String)keys.nextElement();
			ViewableItem vi = (ViewableItem)viewableItemHash.get(viType);
			if (vi != null && vi.isWrapper() && vi.getUserInterface() != null) {
				if (vi.getUserInterface().getSelectionPriority() > rtn) {
					rtn = vi.getUserInterface().getSelectionPriority();
				}
			}
		}
		if (rtn == -1) rtn = 1000;
		else rtn++;
		return rtn;
	}
	
	/**
	 * @param string
	 */
	public void setDefaultLoResType(String string) {
		defaultLoResType = string;
	}

    
     public Object clone() {
            try {
                PlexiViewerSpecForSession deepClone = (PlexiViewerSpecForSession)super.clone();
                if (viewableItemHash==null) {
                    deepClone.setViewableItemHash(null);                    
                }else {
                    Enumeration enumerator = viewableItemHash.keys();
                    while (enumerator.hasMoreElements()) {
                        String key = (String)enumerator.nextElement();
                        ViewableItem value = (ViewableItem)viewableItemHash.get(key);
                        deepClone.addViewableItem(value);
                    }
                }
                return deepClone;
            }catch(CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }
}
