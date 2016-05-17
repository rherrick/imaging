/*
 * org.nrg.plexiViewer.lite.xml.UserInterface
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.xml;
import java.util.Vector;

public class UserInterface implements Cloneable{
	 int selectionPriority;
	 String displayText;
	 boolean allowedToChooseFiles;
	 boolean allowedToChooseId;
	 Vector linkedDropDownList;
	 
	
	public UserInterface() {
		 allowedToChooseFiles=false;
		 allowedToChooseId=false;
		linkedDropDownList = new Vector();
	}
	 	 	
	/**
	 * @return
	 */
	public boolean isAllowedToChooseFiles() {
		return allowedToChooseFiles;
	}

	/**
	 * @return
	 */
	public boolean isAllowedToChooseId() {
		return allowedToChooseId;
	}

	/**
	 * @return
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * @return
	 */
	public Vector getLinkedDropDownList() {
		return linkedDropDownList;
	}

	/**
	 * @return
	 */
	public int getSelectionPriority() {
		return selectionPriority;
	}

	/**
	 * @param b
	 */
	public void setAllowedToChooseFiles(boolean b) {
		allowedToChooseFiles = b;
	}

	/**
	 * @param b
	 */
	public void setAllowedToChooseIds(boolean b) {
		allowedToChooseId = b;
	}

	/**
	 * @param string
	 */
	public void setDisplayText(String string) {
		displayText = string;
	}

	/**
	 * @param vector
	 */
	public void setLinkedDropDownList(Vector vector) {
		linkedDropDownList = vector;
	}

	/**
	 * @param i
	 */
	public void setSelectionPriority(int i) {
		selectionPriority = i;
	}
	
	public void addToLinkedDropDown(LinkedDropDown l) {
		linkedDropDownList.add(l);
	}
    
    public Object clone(){
        try {
           UserInterface deepClone = (UserInterface)super.clone();
            if (linkedDropDownList==null) {
                deepClone.setLinkedDropDownList(null);                    
            }else {
                Vector cloneValue = new Vector();
                for (int i=0;i<linkedDropDownList.size();i++) {
                    cloneValue.addElement(((LinkedDropDown)linkedDropDownList.elementAt(i)).clone());     
                }
                deepClone.setLinkedDropDownList(cloneValue);
            }
            return deepClone;
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String toString() {
    	String rtn = "";
    	rtn += "Display Test: " + getDisplayText();
    	rtn += "Linked Drop Down: " ;
    	Vector linked = getLinkedDropDownList();
    	for (int i = 0; i< linked.size(); i++) {
    		rtn += "" + linked.get(i);
    	}
    	return rtn;
    }

	

	

}
