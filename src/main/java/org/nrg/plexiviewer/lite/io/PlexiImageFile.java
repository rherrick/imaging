/*
 * org.nrg.plexiViewer.lite.io.PlexiImageFile
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.io;

/**
 * @author Mohana
 *
 */
import ij.io.FileInfo;


import java.util.ArrayList;

import org.nrg.plexiviewer.utils.URIUtils;
public class PlexiImageFile extends java.lang.Object implements java.io.Serializable, Cloneable{	
	String path = null;
    String uri = null;
    String xsiType = null;
	String name = null;
    String pattern = null;
	String cachePathURIAsString;
    String resourceCatalogPath = null;
    ArrayList fileList;
	int dimX=-1, dimY=-1, dimZ=-1, volumes=1;
	double voxelResX=-1,voxelResY=-1,voxelResZ=-1;
	private int fileType=FileInfo.GRAY8; 
    String format;
    String orientation;

    /**
     * @return Returns the resourceCatalogPath.
     */
    public String getResourceCatalogPath() {
        return resourceCatalogPath;
    }

    /**
     * @param resourceCatalogPath The resourceCatalogPath to set.
     */
    public void setResourceCatalogPath(String resourceCatalogPath) {
        this.resourceCatalogPath = resourceCatalogPath;
    }

    /**
     * @return Returns the orientation.
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * @param orientation The orientation to set.
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    /**
     * @return Returns the format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format The format to set.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return Returns the pattern.
     */
    public String getPattern() {
        return pattern;
    }
    
    public String getXsiType () {
        return xsiType;
    }
    
    public void setXsiType(String xsiType) {
        this.xsiType = xsiType;
    }

    /**
     * @param pattern The pattern to set.
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public PlexiImageFile() {
       initFileList();
    }
    
    public void initFileList() {
        fileList = new ArrayList();
    }
    
    public void addToFiles(String fileURI) {
        fileList.add(fileURI);
    }
    
    public ArrayList getFiles() {
        return fileList;
    }
    
    public String getURIAsString() {
        return uri;
    }
    
    
   
    

   

    

    private void setPathAndName() throws Exception{
        //System.out.println("URI is " + uri);
        URIUtils uUtils = new URIUtils(uri);
        String t = uUtils.getPath();
        if (t!=null)
            path = t;
        t = uUtils.getName();
        if (t!=null)
            name = t;
    }
    
    public void flush() {
        if (fileList != null) {
            fileList.clear();
        }
    }
    

    
    public void setURIAsString(String u) throws Exception{
        if (u!= null)uri = u.replace("\\", "/");
        if (uri != null) setPathAndName();
     }
    

    
	/**
	 * @return
	 */
	public String getCachePath() {
        if (cachePathURIAsString == null) return "";
        return cachePathURIAsString.toString();
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getPath() {
		return path;
	}

    
    public void setCachePath(String cache){
        cachePathURIAsString = cache;
    }

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setPath(String string) {
		path = string;
	}
	
	public void setDimensions(int dimX, int dimY, int dimZ) {
		this.dimX=dimX;
		this.dimY=dimY;
		this.dimZ=dimZ;
	}
	
	public void setVoxels(double x, double y, double z) {
		voxelResX = x; voxelResY=y; voxelResZ=z;
	}
	/**
	 * @return
	 */
	public int getDimX() {
		return dimX;
	}

	/**
	 * @return
	 */
	public int getDimY() {
		return dimY;
	}

	/**
	 * @return
	 */
	public int getDimZ() {
		return dimZ;
	}

	/**
	 * @return
	 */
	public int getVolumes() {
		return volumes;
	}

	/**
	 * @return
	 */
	public double getVoxelResX() {
		return voxelResX;
	}

	/**
	 * @return
	 */
	public double getVoxelResY() {
		return voxelResY;
	}

	/**
	 * @return
	 */
	public double getVoxelResZ() {
		return voxelResZ;
	}

	/**
	 * @param i
	 */
	public void setDimX(int i) {
		dimX = i;
	}

	/**
	 * @param i
	 */
	public void setDimY(int i) {
		dimY = i;
	}

	/**
	 * @param i
	 */
	public void setDimZ(int i) {
		dimZ = i;
	}

	/**
	 * @param i
	 */
	public void setVolumes(int i) {
		volumes = i;
	}

	/**
	 * @param f
	 */
	public void setVoxelResX(double f) {
		voxelResX = f;
	}

	/**
	 * @param f
	 */
	public void setVoxelResY(double f) {
		voxelResY = f;
	}

	/**
	 * @param f
	 */
	public void setVoxelResZ(double f) {
		voxelResZ = f;
	}
	
	public boolean hasDimensions() {
		boolean rtn=false;
		if (dimX!=-1 && dimY!=-1 && dimZ!=-1 && volumes!=-1) {
			rtn=true;
		}
		return rtn;
	}
	
	public void resetDimensions() {
		dimX=-1;dimY=-1;dimZ=-1;volumes=-1;
	}

	public void resetVoxelRes() {
		voxelResX=-1;voxelResY=-1;voxelResZ=-1;
	}

	
	public Object clone() {
		try {
			PlexiImageFile deepClone = (PlexiImageFile)super.clone();
            deepClone.initFileList();    

            /*if (uri != null) {
                deepClone.setURI(uri);
            }

            if (cachePathURIAsString != null) {
                deepClone.setCachePath(cachePathURIAsString);
            }*/
            
            try {
                for (int i = 0; i < fileList.size(); i++) {
                        deepClone.addToFiles((String)fileList.get(i));
                }
            }catch (Exception e){e.printStackTrace();}

			return deepClone;
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}catch(Exception e) {
            e.printStackTrace();
            return null;
        }	
	}
	
	public int getFileType() {
		return fileType;	
	}
	
	public void setFileType(int type) {
		fileType = type;
		//System.out.println("PlexiImageFile::SetFileType called " + type);
	}
	
	public String toString() {
		String rtn = "";
		rtn += " URI: " + uri;
        rtn +=" Name: " + name;  
		rtn +=" Path: " + path ;
        rtn += " ResourceCatalog " + this.resourceCatalogPath;
		rtn += " Cache Path:" + getCachePath();
		rtn += " Dimensions: " + getDimX()+"x"+getDimY() +"x" + getDimZ() +"x" + getVolumes();
		rtn += " VoxelRes: " + getVoxelResX()+"x" + getVoxelResY() + "x" + getVoxelResZ();
		rtn += " FileType: "  + getFileType() ;
        rtn += " XsiType " + xsiType;
        rtn += " Format " + format;
        if (fileList != null)
        rtn += " No of files " + fileList.size();
		return rtn;
	}
	
    public static void main(String args[]) {
        PlexiImageFile p = new PlexiImageFile();
        p.clone();
        System.out.println("Cloned");
    }
}
