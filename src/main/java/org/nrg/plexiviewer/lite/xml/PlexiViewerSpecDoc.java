/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.lite.xml;

import java.util.Hashtable;

import org.nrg.plexiviewer.utils.PlexiConstants;

public class PlexiViewerSpecDoc {
    private Hashtable viewerSpecForSession;
    
    public PlexiViewerSpecDoc() {
        viewerSpecForSession = new Hashtable();
    }
    
    public synchronized void setViewerSpecification(String project, PlexiViewerSpecForSession spec) {
       System.out.println("Setting for Session type:" + project +":");
       spec.setProject(project);
        viewerSpecForSession.put(project,spec);
    }
    
    public PlexiViewerSpecForSession getPlexiViewerSpecForSession(String sessionType) {
        if ((sessionType == null) || !viewerSpecForSession.containsKey(sessionType)) {
           System.out.println("Couldnt find the PlexiSpec for sessionType :" + sessionType + ": returning default");
           PlexiViewerSpecForSession rtn =  (PlexiViewerSpecForSession)viewerSpecForSession.get(PlexiConstants.PLEXI_DEFAULT_SPEC);
           if (rtn == null)
               return new PlexiViewerSpecForSession();
           else
               return rtn;
        }else {
            return (PlexiViewerSpecForSession)viewerSpecForSession.get(sessionType);
        }
    }
    
    
    
}
