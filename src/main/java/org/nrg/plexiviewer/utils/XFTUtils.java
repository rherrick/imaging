/*
 * org.nrg.plexiViewer.utils.XFTUtils
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
import java.util.*;
import org.nrg.xft.*;
import org.nrg.plexiviewer.lite.xml.*;
public class XFTUtils {
	
	public static String getSessionFieldName(SchemaLink schemaLink) {
		String element = schemaLink.getElementName();
		String rtn=element;
		int elementDotIndex = element.indexOf(".");
		if (elementDotIndex <0) {
			//Assume we have an mrAssessor
			rtn= element + "." + getSessionFieldName("xnat:mrAssessorData"); 	
		}else {
			rtn=element.substring(0,elementDotIndex) + "." + getSessionFieldName(element.substring(0,elementDotIndex));
		}
		//System.out.println("XFTUtils::getSessionFieldName " + rtn);
		return rtn;
	}
	
	public static String getSessionFieldName(String xmlPath) {
		String rtn=null;
		if (xmlPath.equals("xnat:imageScanData") || xmlPath.equals("xnat:mrScanData") || xmlPath.equals("xnat:reconstructedImageData")) {
			rtn="image_session_ID";
		}else if (xmlPath.equals("xnat:mrAssessorData") || xmlPath.equals("xnat:imageAssessorData")){
			//rtn="mrSession_ID";
			rtn = "imageSession_ID";
		}
		return rtn;
	}
	
	/*public static String getSessionFieldName(String xmlPath) {
		String rtn=null;
		if (xmlPath.equals("xnat:mrScanData")) {
			rtn="mr_session_id";
		}else if (xmlPath.equals("xnat:mrAssessorData")){
			rtn="mrSession_ID";
		}else if (xmlPath.equals("xnat:reconstructedImageData")){
			rtn = "reconstructions_reconstructedim_id";	
		}else if (xmlPath.equals("xnat:imageScanData")) {
			rtn="image_session_ID";
		}
		return rtn;
	}*/
	
	public static String getFileContentFieldName() {
		return "content";
	}
	
	public static String getFileContentFieldName(String xmlPath) {
		return (xmlPath + "." + getFileContentFieldName());
	}
	
	public static String getXMLPathToScan() {
		return "xnat:imageScanData";
	}
	
	/*public static String getXMLPathToScan() {
		return "xnat:mrScanData";
	}*/
	
	public static ArrayList getChildItems(ArrayList parentList, String xmlPath, Object value) {
		ArrayList  rtn = new ArrayList();
		try {
			for (int j=0;j<parentList.size();j++) {
				XFTItem parent = (XFTItem)parentList.get(j);
				ArrayList children = parent.getChildItems(xmlPath);
				if (children!=null) {
					if (children.size() == 1) {
						rtn.add(children.get(0));
					}else {
						for (int i=0; i<children.size();i++) {
							//System.out.println(((XFTItem)children.get(i)).getProperty(getFileContentFieldName()));
							if (((XFTItem)children.get(i)).getProperty(getFileContentFieldName()).equals(value)) {
								rtn.add(children.get(i));
							}
						}
					}
				}	
			}
		}catch (Exception e){
			System.out.println("XFTUtils::getCHildTems Exception occured");
			return rtn;
		}
		return rtn;
	}
    
    public static ArrayList getChildItems(ArrayList parentList, String xmlPath) {
        ArrayList  rtn = new ArrayList();
        try {
            for (int j=0;j<parentList.size();j++) {
                XFTItem parent = (XFTItem)parentList.get(j);
                ArrayList children = parent.getChildItems(xmlPath);
                if (children!=null) {
                    for (int i=0; i<children.size();i++) {
                            rtn.add(children.get(i));
                    }
                }   
            }
        }catch (Exception e){
            System.out.println("XFTUtils::getCHildTems Exception occured");
            return rtn;
        }
        return rtn;
    }
}