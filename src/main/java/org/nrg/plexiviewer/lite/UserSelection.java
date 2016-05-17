/*
 * org.nrg.plexiViewer.lite.UserSelection
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite;

/**
 * @author Mohana
 *
 */
import java.util.Vector;

import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.xdat.bean.XnatAbstractresourceBean;
import org.nrg.xdat.bean.XnatResourceBean;

public class UserSelection extends java.lang.Object implements java.io.Serializable,  Cloneable{
	boolean hasFile=false;
	boolean hasXnatFile = false;
	PlexiImageFile file;
	boolean hasScanNo=false;
	String scanNo;
	String orientation;
	boolean radiologic=false;
	String dataType;
	String display;
	String imageViewerClass;
	String sessionId;
	String exptId;
	String windowTitle;
	String project; 
	String loResType=null;
	int hiResLayerNum=-1;
	float scaleFactor=1;
	boolean overwrite=false;
	Vector allFiles;
     Vector hiResLayerNos; 
	String sessionLabel;
	XnatAbstractresourceBean xnatFile;
	
	public XnatAbstractresourceBean getXnatFile() {
		return xnatFile;
	}


	public void setXnatFile(XnatAbstractresourceBean xnatFile) {
		this.xnatFile = xnatFile;
		hasXnatFile = true;
	}


	public UserSelection() {
		allFiles=new Vector();	
		hiResLayerNos = new Vector();
		file=null;
	}
	
	
	public PlexiImageFile getFile() {
		return file;	
	}
	

	/**
	 * @return
	 */
	public boolean hasFile() {
		return hasFile;
	}

	/**
	 * @return
	 */
	public boolean hasXnatFile() {
		return hasXnatFile;
	}

	
	/**
	 * @return
	 */
	public boolean hasScanNo() {
		return hasScanNo;
	}

	/**
	 * @return
	 */
	public String getOrientation() {
		return orientation;
	}

	/**
	 * @return
	 */
	public boolean isRadiologic() {
		return radiologic;
	}

	/**
	 * @return
	 */
	public String getScanNo() {
		return scanNo;
	}

	/**
	 * @return
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param string
	 */
	public  void setFile(PlexiImageFile gd) {
		file = gd;
		hasFile = true;
	}

	/**
	 * @param string
	 */
	public void setOrientation(String string) {
		orientation = string;
	}

	/**
	 * @param b
	 */
	public void setRadiologic(boolean b) {
		radiologic = b;
	}

	/**
	 * @param string
	 */
	public void setScanNo(String string) {
		scanNo = string;
		hasScanNo=true;
	}

	/**
	 * @param string
	 */
	public void setDataType(String string) {
		dataType = string;
	}

	public boolean isIdentical(UserSelection other) {
		return this.toString().equals(other.toString());
	}
	public boolean equals(Object b) {
		boolean rtn=false;
		if (b==null) return false;
		UserSelection bb = (UserSelection)b; 
		rtn = isIdentical(bb);
		return rtn;		
	}
	
	public String toString() {
		String rtn="User Selected::\n";
		rtn+="Session Id: " + sessionId;
		rtn+= "First Project: " + project; 
		rtn +=" Data : " + getDataType();
		String fileStr = "";
		if (hasFile && file != null) 
			fileStr = file.toString();
		if (hasXnatFile && xnatFile != null) 
			fileStr= xnatFile.toString();
		rtn +=" File : " + fileStr;
		rtn +=" Orientation: " +getOrientation();
		rtn +=" Radiologic: " + isRadiologic();
		rtn +=" Display: " + getDisplay();  
		return rtn;
	}
	/**
	 * @return
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * @param string
	 */
	public void setDisplay(String string) {
		display = string;
	}

	/**
	 * @return
	 */
	public String getImageViewerClass() {
		return imageViewerClass;
	}

	/**
	 * @param string
	 */
	public void setImageViewerClass(String string) {
		imageViewerClass = string;
	}
	
	
		
	
	public Object clone() {
			try {
				UserSelection deepClone = (UserSelection)super.clone();
                if (allFiles==null) {
					deepClone.setAllFiles(null);					
				}else {
					Vector cloneFiles = new Vector();
					for (int i=0;i<allFiles.size();i++) {
						cloneFiles.addElement(((PlexiImageFile)allFiles.elementAt(i)).clone());		
					}
					deepClone.setAllFiles(cloneFiles);
                }
				if (hiResLayerNos==null) {
					deepClone.setHiResLayerNos(null);					
				}else {
					Vector hiResNos = new Vector();
					for (int i=0;i<hiResLayerNos.size();i++) {
						hiResNos.addElement(new Integer(((Integer)hiResLayerNos.elementAt(i)).intValue()));		
					}
					deepClone.setHiResLayerNos(hiResNos);
				}
				if (getFile()!=null)
					deepClone.setFile((PlexiImageFile)this.getFile().clone());
				if (getXnatFile() != null) {
					deepClone.setXnatFile(getXnatFile());
				}
				return deepClone;
			}catch(CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}
	/**
	 * @return
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param string
	 */
	public void setSessionId(String string) {
		sessionId = string;
	}

	/**
	 * @return
	 */
	public String getWindowTitle() {
		String rtn="";
		if (windowTitle==null)
		  windowTitle=getSessionLabel()  + ":" + getDataType() + getFilename();
		rtn=windowTitle;  
		return rtn;
	}
	
	public String getFormattedWindowTitle() {
		String rtn = getWindowTitle();
		rtn = rtn.replaceAll(":", "_");
		rtn=rtn.replaceAll(" ", "");
		return rtn;
	}
	
	private String getFilename() {
		String rtn = getDataType();
		if (hasFile) {
			if (file.getName() != null)
			rtn += ":" + file.getName();
			else {
				if (file.getURIAsString() != null) {
					String str = file.getURIAsString();
					int i = str.lastIndexOf("/");
					if (i != -1) {
						try {
							str = str.substring(i+1);
						}catch(IndexOutOfBoundsException ioe) {
							
						}
						rtn += ":" + str;
					}
				}else if (file.getResourceCatalogPath() != null) {
					rtn += ":" + file.getResourceCatalogPath();
				}

			}
		}else if (hasXnatFile) {
			if (xnatFile instanceof XnatResourceBean) {
				XnatResourceBean rsc = (XnatResourceBean)xnatFile;
				String uri = rsc.getUri();
				int i = uri.lastIndexOf("/");
				if (i != -1) {
					try {
						uri = uri.substring(i+1);
					}catch(IndexOutOfBoundsException ioe) {
						
					}
					rtn += ":" + uri;
				}

			}
		}
		return rtn;
	}

	/**
	 * @param string
	 */
	public void setWindowTitle(String string) {
		windowTitle = string;
	}

	/**
	 * @return
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @param string
	 */
	public void setProject(String string) {
		project = string;
	}

	/**
	 * @return
	 */
	public String getLoResType() {
		return loResType;
	}

	/**
	 * @param string
	 */
	public void setLoResType(String string) {
		loResType = string;
	}


	/**
	 * @return
	 */
	public int getHiResLayerNum() {
		return hiResLayerNum;
	}

	/**
	 * @param i
	 */
	public void setHiResLayerNum(int i) {
		hiResLayerNum = i;
	}

	/**
	 * @return
	 */
	public float getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * @param f
	 */
	public void setScaleFactor(float f) {
		scaleFactor = f;
	}

	/**
	 * @return
	 */
	public boolean canOverwrite() {
		return overwrite;
	}

	/**
	 * @param b
	 */
	public void setOverwrite(boolean b) {
		overwrite = b;
	}

	/**
	 * @return
	 */
	public Vector getAllFiles() {
		return allFiles;
	}

	/**
	 * @param vector
	 */
	public void setAllFiles(Vector vector) {
		allFiles = vector;
	}


	
	/**
	 * @return
	 */
	public String getExptId() {
		return exptId;
	}

	/**
	 * @param string
	 */
	public void setExptId(String string) {
		exptId = string;
	}

	/**
	 * @return
	 */
	public Vector getHiResLayerNos() {
		return hiResLayerNos;
	}

	/**
	 * @param vector
	 */
	public void setHiResLayerNos(Vector vector) {
		hiResLayerNos = vector;
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
