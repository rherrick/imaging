/*
 * org.nrg.plexiViewer.lite.xml.ViewableItem
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
import java.util.*;
public class ViewableItem implements Cloneable {
	 String type;
	 String imageViewerClassName;
     //Viewer imageViewer;
	 SchemaLink schemaLink;
	 UserInterface userInterface;
	 Thumbnail thumbnail;
	 Hashtable loResHash;
	 HiRes hiRes;
	 String displayCondition;
	
	public ViewableItem () {
		//imageViewer = null;
        imageViewerClassName = null;
		loResHash = new Hashtable();
		schemaLink=null;
		displayCondition=null;
	}
	
	public boolean isWrapper() {
		boolean rtn=false;
		if (getImageViewerClassName()==null)
			rtn=true;
		return rtn;	
	}
	
	/**
	 * @return
	 */
	public String getImageViewerClassName() {
		//String rtn = null;
        //if (imageViewer != null)
        //    rtn = imageViewer.getClassName();
        //return rtn;
        return imageViewerClassName;
	}

	/**
	 * @return
	 */
	public SchemaLink getSchemaLink() {
		return schemaLink;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	//public void setViewer(Viewer v) {
	//	imageViewer = v;
	//}

    public void setImageViewerClassName(String v) {
      imageViewerClassName = v;
    }

    
	/**
	 * @param string
	 */
	public void setSchemaLink(SchemaLink s) {
		schemaLink = s;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * @return
	 */
	public HiRes getHiRes() {
		return hiRes;
	}

	/**
	 * @return
	 */
	public Hashtable getLoResHash() {
		return loResHash;
	}

	/**
	 * @return
	 */
	public Thumbnail getThumbnail() {
		return thumbnail;
	}

	/**
	 * @return
	 */
	public UserInterface getUserInterface() {
		return userInterface;
	}

	/**
	 * @param res
	 */
	public void setHiRes(HiRes res) {
		hiRes = res;
	}

	public void addToLoResHash(LoRes loRes) {
		loResHash.put(loRes.getType(), loRes);
	}
	
	public String hasUniqueLoRes() {
		String rtn=null;
		if (getLoResHash()!=null && getLoResHash().size()>0) {
				if (getLoResHash().size()==1) {
					Object uniqueKey = getLoResHash().keys().nextElement();
					rtn=((LoRes)getLoResHash().get(uniqueKey)).getType();
				}
			}
		return rtn;
	}
	
	public LoRes getLoRes(String type) {
		return (LoRes)loResHash.get(type);
	}

	/**
	 * @param hashtable
	 */
	public void setLoResHash(Hashtable hashtable) {
		loResHash = hashtable;
	}

	/**
	 * @param thumbnail
	 */
	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * @param interface1
	 */
	public void setUserInterface(UserInterface interface1) {
		userInterface = interface1;
	}

	/**
	 * @return
	 */
	public String getDisplayCondition() {
		return displayCondition;
	}

	/**
	 * @param string
	 */
	public void setDisplayCondition(String string) {
		displayCondition = string;
	}
	
	public String toString() {
		String rtn = "";
		rtn += "Type:  " + type + "\n";
		rtn += "SchemaLink:: " + getSchemaLink() ;
		rtn += "UserInterface: " + getUserInterface();
		rtn += "IsWrapper: " + isWrapper();
		rtn += "HiRes: " + getHiRes();
		rtn += "LoRes: " + getLoRes("32bit");
		return rtn;
	}
    
     public Object clone(){
            try {
               ViewableItem deepClone = (ViewableItem)super.clone();
                if (schemaLink==null) {
                    deepClone.setSchemaLink(null);                    
                }else {
                    deepClone.setSchemaLink((SchemaLink)schemaLink.clone());
                }
                if (userInterface == null)
                    deepClone.setUserInterface(null);
                else
                    deepClone.setUserInterface((UserInterface)userInterface.clone());
                if (thumbnail == null)
                    deepClone.setThumbnail(null);
                else 
                    deepClone.setThumbnail((Thumbnail)thumbnail.clone());
                if (hiRes == null)
                    deepClone.setHiRes(null);
                else 
                    deepClone.setHiRes((HiRes)hiRes.clone());
                if (loResHash == null)
                    deepClone.setLoResHash(null);
                else {
                    Enumeration enumerator = loResHash.keys();
                    while (enumerator.hasMoreElements()) {
                        String key = (String)enumerator.nextElement();
                        LoRes value = (LoRes)loResHash.get(key);
                        deepClone.addToLoResHash(value);
                    }
                }
                    
                return deepClone;
            }catch(CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }

}
