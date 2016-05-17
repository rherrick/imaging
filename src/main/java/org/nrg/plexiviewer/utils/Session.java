/*
 * org.nrg.plexiViewer.utils.Session
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.utils;

/**
 * @author Mohana
 *
 */

import org.apache.log4j.Logger;
import org.nrg.plexiviewer.lite.DBFSXMLInfo;
import org.nrg.plexiviewer.utils.Transform.PlexiImageOrientor;
import org.nrg.xdat.bean.XnatImagescandataBean;
import org.nrg.xdat.bean.XnatImagesessiondataBean;
import org.nrg.xdat.bean.XnatMrscandataBean;
import org.nrg.xdat.bean.base.BaseElement;
import org.nrg.xdat.bean.reader.XDATXMLReader;
import org.nrg.xft.XFTItem;
import org.nrg.xft.collections.ItemCollection;

import java.io.ByteArrayInputStream;
import java.util.Vector;


public class Session {
	private String sessionId;
	private String sessionLabel; 
	
    private static Logger logger = Logger.getLogger(Session.class);
    private XnatImagesessiondataBean imageSession;
    
	public Session (String sessionId) {
		this.sessionId = sessionId;
		setSessionBean();
	}
    
    private void setSessionBean() {
        try {
            ItemCollection mrs = org.nrg.xft.search.ItemSearch.GetItems("xnat:imagesessionData.ID",sessionId,null,false);
            XFTItem mrsession = (XFTItem)mrs.getFirst();
            XDATXMLReader xdatXmlReader = new XDATXMLReader();
            BaseElement baseElement = xdatXmlReader.parse(new ByteArrayInputStream(mrsession.toXML_BOS(null).toByteArray()));
            imageSession = (XnatImagesessiondataBean)baseElement;
            if (imageSession != null) this.setSessionLabel(imageSession.getLabel());
        }catch(Exception e) {
            logger.debug(e);
        }
    }
	
	
    
    private boolean isScanIsotropic(Double d_voxel_x, Double d_voxel_y, Double d_voxel_z) {
        boolean rtn = false;
        if (d_voxel_x == null || d_voxel_y == null || d_voxel_z == null) return rtn;
        double threshold = 0.5;
        double voxel_x = d_voxel_x.doubleValue();
        double voxel_y=d_voxel_y.doubleValue();
        double voxel_z = d_voxel_z.doubleValue();
        if (Math.abs(voxel_x-voxel_y) < threshold && Math.abs(voxel_x - voxel_z)<threshold) {
            rtn = true;
        }else if (Math.abs(voxel_x-voxel_y) < threshold && Math.abs(voxel_y - voxel_z)<threshold) {
            rtn = true;
        }else if (Math.abs(voxel_x-voxel_z) < threshold && Math.abs(voxel_z - voxel_y)<threshold) {
            rtn = true;
        }
        return rtn;
    }
    
    
    
    
    

    
    private void insertViewableOrientations(DBFSXMLInfo db_fs_xml, XFTItem item) throws Exception {
        if (isScanIsotropic(item.getDoubleProperty("parameters/voxelRes/x"),item.getDoubleProperty("parameters/voxelRes/y"),item.getDoubleProperty("parameters/voxelRes/z"))) {
            db_fs_xml.setViewableOrienations(item.getProperty("id"),getAcquiredOrientation());
        }else {
             db_fs_xml.setViewableOrienations(item.getProperty("id"),getAcquiredOrientation(item.getStringProperty("parameters/orientation")));
        }
    }
    
    private void insertViewableOrientations(DBFSXMLInfo db_fs_xml,XnatImagescandataBean scan) throws Exception {
        if (scan instanceof XnatMrscandataBean) {
        	insertViewableOrientations(db_fs_xml, (XnatMrscandataBean)scan);
        }else {
             db_fs_xml.setViewableOrienations(scan.getId(),getAcquiredOrientation(null));
        }
    }
    
    private void insertViewableOrientations(DBFSXMLInfo db_fs_xml,XnatMrscandataBean mrScan) throws Exception {
        if (isScanIsotropic(mrScan.getParameters_voxelres_x(),mrScan.getParameters_voxelres_y(),mrScan.getParameters_voxelres_z())) {
            db_fs_xml.setViewableOrienations(mrScan.getId(),getAcquiredOrientation());
        }else {
             db_fs_xml.setViewableOrienations(mrScan.getId(),getAcquiredOrientation(mrScan.getParameters_orientation()));
        }
    }
	
    
    private Vector getAcquiredOrientation(String ori) {
        String tra = "Transverse     ";
        String sag = "Sagittal       ";
        String cor = "Coronal        ";
        String acquired = PlexiImageOrientor.AS_ACQUIRED_TXT;
        if (ori == null) {
            Vector orientation = new Vector();
            orientation.add(acquired);
            return orientation;
        }else {
            Vector orientation = new Vector();
            if (ori.toUpperCase().startsWith("TRA"))
                orientation.add(tra);
            else if (ori.toUpperCase().startsWith("SAG")) 
                orientation.add(sag);
            else if (ori.toUpperCase().startsWith("COR")) 
                orientation.add(cor);
            return orientation;
        }
    }
    
    private Vector getAcquiredOrientation() {
        String tra = "Transverse     ";
        String sag = "Sagittal       ";
        String cor = "Coronal        ";
        Vector orientation = new Vector();
        orientation.add(tra);
        orientation.add(sag);
        orientation.add(cor);
        return orientation;
    }
    
	
	public XnatImagesessiondataBean get() {
		return imageSession;
	}

    /**
	 * @return the sessionLabel
	 */
	public String getSessionLabel() {
		return sessionLabel;
	}

	/**
	 * @param sessionLabel the sessionLabel to set
	 */
	public void setSessionLabel(String sessionLabel) {
		this.sessionLabel = sessionLabel;
	}
}
