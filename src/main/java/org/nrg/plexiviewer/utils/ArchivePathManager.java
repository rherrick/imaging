/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.utils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.nrg.xdat.bean.ArcArchivespecificationBean;
import org.nrg.xdat.bean.ArcPathinfoBean;
import org.nrg.xdat.bean.ArcProjectBean;
import org.nrg.xdat.bean.base.BaseElement;
import org.nrg.xdat.bean.reader.XDATXMLReader;
import org.nrg.xft.XFTItem;

public class ArchivePathManager {
    
    private static ArchivePathManager self;
    private static ArcArchivespecificationBean archiveSpecification;
    private static final String elementName = "arc:ArchiveSpecification"; 
    private static long lastAccess = 0;
    private static final long lastAccessCheckDuration = 600000; //5 minutes
    private static Logger logger = Logger.getLogger(ArchivePathManager.class);
    public static final String LORES_SUBFOLDER_NAME="LORES";
    public static final String THUMBNAIL_SUBFOLDER_NAME="THUMB";
    
    private ArchivePathManager() {
        
    }
    
    public static ArchivePathManager GetInstance() {
        if (self == null) {
            self = new ArchivePathManager();
        }
        refresh();
        return self;
    }
    
    private static void refresh() {
        if ( lastAccess == 0 || needsUpdate()) {
            try {
                archiveSpecification = new ArcArchivespecificationBean();
                org.nrg.xft.collections.ItemCollection ic = org.nrg.xft.search.ItemSearch.GetAllItems(elementName,null,true);
                ArrayList list = ic.getItems();
                XFTItem arcSpecItem = (XFTItem)list.get(0);
                XDATXMLReader xdatXmlReader = new XDATXMLReader();
                BaseElement baseElement = xdatXmlReader.parse(new ByteArrayInputStream(arcSpecItem.toXML_BOS(null).toByteArray()));
                synchronized(archiveSpecification) {
                        archiveSpecification = (ArcArchivespecificationBean)baseElement;
                }
                list = null;
                arcSpecItem = null;
                lastAccess = Calendar.getInstance().getTimeInMillis();
            }catch(Exception e) {
                archiveSpecification = null;
                e.printStackTrace();
                logger.debug(e);
            }
        }
    }
    
    private static boolean needsUpdate() {
        boolean rtn = false;
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - lastAccess > lastAccessCheckDuration) {
            rtn = true;
        }
        return rtn;
    }
    
    
    public String getArchivePathLocation(String projectId) {
        String path = null;
        if (archiveSpecification != null) {
            List projects=archiveSpecification.getProjects_project();
            for (int i = 0; i < projects.size(); i++){
                ArcProjectBean project = (ArcProjectBean)projects.get(i);
                if (project.getId().equals(projectId)){
                    ArcPathinfoBean pathInfo= project.getPaths();
                    if (pathInfo!=null){
                        path=pathInfo.getArchivepath();
                    }
                    break;
                }
            }
            if (path==null || path.trim().equals("")){
                ArcPathinfoBean pathInfo= archiveSpecification.getGlobalpaths();
                if (pathInfo!=null){
                    path=pathInfo.getArchivepath();
                }
            }
        if (path==null){
            path =".";
        }
        path = path.replace('\\', '/');
        if (!path.endsWith("/")){
            path = path +"/";
        }
        }
        return path;
    }
    
//    public String getLoResLocation(String projectId) {
  //      return getCachePathLocation(projectId) +  LORES_SUBFOLDER_NAME;
    //}
    
    public String getLoResLocation(String projectId, String sessionId) {
       String cpath = getCachePathLocation(projectId, sessionId);
       //if (cpath.endsWith(File.separator))
    	 //  cpath = cpath.substring(0,cpath.length()-1);
    	//String rtn =  cpath +  sessionId;
    	return cpath;
    }

    public String getThumbnailLocation(String projectId) {
        return getCachePathLocation(projectId, null) +  THUMBNAIL_SUBFOLDER_NAME;
    }

    
    public String getCachePathLocation(String projectId, String sessionId) {
        String path = null;
        if (archiveSpecification != null) {
            List projects=archiveSpecification.getProjects_project();
            for (int i = 0; i < projects.size(); i++){
                ArcProjectBean project = (ArcProjectBean)projects.get(i);

                if (project.getId().equals(projectId)){
                    ArcPathinfoBean pathInfo= project.getPaths();
                    if (pathInfo!=null){
                        path=pathInfo.getCachepath();
                    }
                    break;
                }
            }
            if (path==null || path.trim().equals("")){
                ArcPathinfoBean pathInfo= archiveSpecification.getGlobalpaths();
                if (pathInfo!=null){
                    path=pathInfo.getCachepath();
                }
            }
        if (path==null){
            path =".";
        }
        path = path.replace('\\', '/');
        if (!path.endsWith("/")){
            path = path +"/";
        }
        }
        if (sessionId != null) path += sessionId;
        return path;
    }
    
}
