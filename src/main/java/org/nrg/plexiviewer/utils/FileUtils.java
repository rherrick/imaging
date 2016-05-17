/*
 * org.nrg.plexiViewer.utils.FileUtils
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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.xml.SchemaLink;
import org.nrg.plexiviewer.manager.PlexiSpecDocReader;
import org.nrg.xdat.bean.XnatAbstractresourceBean;
import org.nrg.xdat.bean.XnatDicomseriesBean;
import org.nrg.xdat.bean.XnatDicomseriesImageBean;
import org.nrg.xdat.bean.XnatImageresourceBean;
import org.nrg.xdat.bean.XnatImageresourceseriesBean;
import org.nrg.xdat.bean.XnatImagescandataBean;
import org.nrg.xdat.bean.XnatImagesessiondataBean;
import org.nrg.xdat.bean.XnatResourcecatalogBean;
import org.nrg.xdat.bean.base.BaseElement;
import org.nrg.xdat.bean.reader.XDATXMLReader;
import org.nrg.xft.XFTItem;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.search.CriteriaCollection;

import edu.sdsc.grid.io.GeneralFile;
import edu.sdsc.grid.io.local.LocalFile;

public class FileUtils {
	
	public static String getTempFolder() {
		String rtn =  System.getProperty("plexi.tmp");
		if (rtn == null) {
			rtn = System.getProperty("user.home");
		}
		return rtn;
	}

    public static URI ResolveURIAgainstArchivePath(String project, String fileUriStr) throws URISyntaxException {
        //System.out.println("File URI " + fileUriStr);
        URI fileUri = new URI(fileUriStr.replace("\\", "/"));
        return ResolveURIAgainstArchivePath(project,fileUri);
    }
    
    public static boolean deleteFile(LocalFile f) {
        if (f==null) return false;
        System.out.println("Deleting file " + f.getAbsolutePath());
        return recursiveDelete((GeneralFile)f);
    }
    
    public static boolean deleteFile(LocalFile f, boolean safeDelete) {
        if (f==null) return false;
        System.out.println("Deleting file " + f.getAbsolutePath());
        return recursiveDelete((GeneralFile)f);
    }

    public static boolean deleteFile(File f) {
        //System.out.println("Deleting file " + f.getAbsolutePath());
        return recursiveDelete(f);
    }
    
    public static boolean deleteFile(File f, boolean safeDelete) {
    	if (safeDelete) {
    		String temp =  getTempFolder();
    		File tempFolder = new File(temp);
    		if (f.getPath().startsWith(tempFolder.getPath())) {
    			 return recursiveDelete(f);
    		}else {
    			return false;
    		}
    	}else 
    		return recursiveDelete(f);
    }

    
    private static boolean  recursiveDelete(GeneralFile file)
    {
        if (file.isDirectory()) {
            //recursive delete
            GeneralFile fileList[] = file.listFiles();

            if (fileList != null) {
                for (int i=0;i<fileList.length;i++) {
                    recursiveDelete(fileList[i]);
                }
            }
        }
        return file.delete();
    }

    private static boolean  recursiveDelete(File file)
    {
        if (file.isDirectory()) {
            //recursive delete
            File fileList[] = file.listFiles();
            if (fileList != null) {
                for (int i=0;i<fileList.length;i++) {
                    recursiveDelete(fileList[i]);
                }
            }
        }
        return file.delete();
    }

    
    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }
    
    public static boolean deleteFile(String path, boolean safeDelete) {
        return deleteFile(new File(path), safeDelete);
    }
    
    public static boolean deleteFile(ArrayList files) {
        boolean rtn = true;
        int i=0;
        String path = null;
        for (;i < files.size(); i++) {
           if (path == null)
               path = ((File)files.get(i)).getParent();
           if (!((File)files.get(i)).delete()) {
               break;
           }
        }
        if (i<files.size()) rtn = false;
        if (rtn) new File(path).delete();
        return rtn;
    }

    
    public static URI ResolveURIAgainstCachePath(String projectPath,String fileUriStr) throws URISyntaxException {
        URI fileUri = new URI(fileUriStr.replace("\\", "/"));
        return ResolveURIAgainstCachePath(projectPath,fileUri);
    }

  
    
    public static URI ResolveURIAgainstArchivePath(String projectPath,URI fileUri) throws URISyntaxException {
        //URI base = URIUtils.getURI("srb://dmarcus.wustl-nrg@wustl-nrg-gpop.nbirn.net:5825/home/dmarcus.wustl-nrg/oasis/set1/");
        //System.out.println("FILEUTILS:: XFT ARCHIVE PATH " + XFT.GetArchiveRootPath());
        URI base = URIUtils.getURI(projectPath.replace("\\", "/"));
        URI rtn =  base.resolve(fileUri);
        //System.out.println("Resolved URI " + rtn);
        return rtn;
    }
    

    
    public static URI ResolveURIAgainstCachePath(String projectPath, URI fileUri) throws URISyntaxException {
        URI base = URIUtils.getURI(projectPath.replace("\\", "/"));
        URI rtn =  base.resolve(fileUri);
        return rtn;
    }
    
	public static PlexiImageFile fileExists(PlexiImageFile pf) {
		boolean rtn=false;
		rtn=fileExists(pf.getPath(),pf.getName());
		if (rtn) {
			return pf;
		}else {
            //String p = URIUtils.stripScheme(pf.getCachePath());
			rtn=fileExists(pf.getCachePath(),pf.getName());
			if (rtn){pf.setPath(pf.getCachePath()); return pf; }
			else {
				int indexOfDot = pf.getName().indexOf(".");
                if (indexOfDot != -1) {
                    pf.setName(pf.getName().substring(0,indexOfDot)+".4dfp.img");
                    rtn=fileExists(pf.getCachePath(),pf.getName());
                    if (rtn) {pf.setPath(pf.getCachePath()); return pf;}
                    else {
    					rtn=fileExists(pf.getPath(),pf.getName());
    					if (rtn) {return pf;}
    					else 
    						return null;				
                    }
                }else 
                    return null;                
			}
		}
	}

	public static boolean fileExists(String path, String fileName) {
	    if (path==null && fileName == null) return false;
        String uri = path;
        if (!uri.endsWith(File.separator))
            uri += File.separator +fileName;
        else
            uri += fileName;
        //System.out.println("FileUtils::fileExists uri " + uri);
        URI u = null;
            try {
                u = new URI (uri);
            }catch (Exception e){
                u = null;
               // System.out.println("FileUtils::fileExists Invalid URI " + uri);
                File f = new File(uri);
                u = f.toURI();
            }
            if (u != null) {
                File fileOrDir = null;
                try{
                    fileOrDir = new File(u);
                }catch (Exception e){System.out.println("FileUtils::fileExists Invalid URI " + uri);}
                if (fileOrDir != null) return fileOrDir.exists();
            }
			try {
                File fileOrDir = new File(uri);
                boolean rtn = fileOrDir.exists();
                //System.out.println("FileUtils::FileExists Looking for file " + fileOrDir.getAbsolutePath() + " " + rtn);
                return rtn;
            }catch(Exception e) {e.printStackTrace();}
			return false;
		}
    
	public static boolean createDirectory(String path) {
		boolean success=false;
        File dir = null;
        try {
            URI u = new URI(path);
            dir = new File(u);
        }catch (Exception e) {}
		if (dir == null) dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			return true;
		}
		success=dir.mkdirs();
		return success;
	}
	
	public static boolean dirExists(String path) {
		boolean success= false;
        File dir = null;
        try {
            URI u = new URI(path);
            dir = new File(u);
        }catch (Exception e) {}
        if (dir == null) dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			return true;
		}
		return success;
	}

	public static String getLoResFileName(String baseFileName, String loResType, String orientation) {
		String rtn=null;
		int indexOfDot = baseFileName.lastIndexOf(".");
        if (indexOfDot != -1)
            rtn = baseFileName.substring(0,indexOfDot) + "_" + loResType + "_" + orientation.substring(0,3).toLowerCase().trim() + ".img";
        else 
            rtn = baseFileName + "_" + loResType + "_" + orientation.substring(0,3).toLowerCase().trim() + ".img";
		return rtn;
	}    


	public static String getThumbnailFileName(String fromFileName,String orientation, int sliceNo) {
		String rtn = null;
		int indexOfDot = fromFileName.indexOf(".");
		if (indexOfDot == -1)
            rtn = fromFileName + "_" + orientation.substring(0,3).toLowerCase()+"_" +sliceNo + ".gif";
        else     
            rtn = fromFileName.substring(0,indexOfDot) + "_" + orientation.substring(0,3).toLowerCase()+"_" +sliceNo + ".gif";
		return rtn;
		 
	}

	
	public static PlexiImageFile getHiResFilePath(String sessionId, String sessionType, String viewableItemType, int layerNum, String scanNumber){
		PlexiImageFile pf = null;
		SchemaLink s = (SchemaLink)PlexiSpecDocReader.GetInstance().getSpecDoc(sessionType).getViewableItem(viewableItemType).getHiRes().getLayersHash().get(new Integer(layerNum));
		//System.out.println("Schema Link " + s.toString());
		try {
			ArrayList files = FileUtils.getFiles(s,sessionId,sessionType, scanNumber);
			if (files!=null && files.size()>0) {
				pf = (PlexiImageFile)files.get(0);
			}else {
				pf = getScanFile(sessionId, scanNumber);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return pf;		
	}
	
	private static PlexiImageFile getScanFile(String sessionId, String scanNumber) {
		PlexiImageFile pf = null;
		try {
            ItemCollection mrs = org.nrg.xft.search.ItemSearch.GetItems("xnat:imagesessionData.ID",sessionId,null,false);
			//ImageSession is here
			//Get the scan corresponding to the number
			XFTItem item = (XFTItem)mrs.getFirst();
            XDATXMLReader xdatXmlReader = new XDATXMLReader();
            BaseElement baseElement = xdatXmlReader.parse(new ByteArrayInputStream(item.toXML_BOS(null).toByteArray()));
            XnatImagesessiondataBean imageSession = (XnatImagesessiondataBean)baseElement;
            //Get the scan corresponding to the scanNumber
            List mrScans = imageSession.getScans_scan();
            XnatImagescandataBean mrScan = null;
            for (int i = 0; i < mrScans.size(); i++) {
            	XnatImagescandataBean aScan = (XnatImagescandataBean)mrScans.get(i);
                if (aScan.getId().equals(scanNumber)) {
                	mrScan = aScan;
                	break;
                }
             }
            if (mrScan != null) {
            	List files = mrScan.getFile();
            	if (files != null && files.size() > 0 ) {
            		if (files.size()== 1) {
            			XnatAbstractresourceBean abs = (XnatAbstractresourceBean) files.get(0);
            			if (abs instanceof XnatImageresourceBean) {
            				XnatImageresourceBean rsc = (XnatImageresourceBean)abs;
            					pf = getImageDetails(rsc, imageSession.getProject(), imageSession.getId());
            			}else if (abs instanceof XnatImageresourceseriesBean) {
            				XnatImageresourceseriesBean rsc = (XnatImageresourceseriesBean)abs;
            					pf = getImageDetails(rsc, imageSession.getProject(), imageSession.getId());
            			}else if (abs instanceof XnatDicomseriesBean) {
            				XnatDicomseriesBean rsc = (XnatDicomseriesBean)abs;
            					pf = getImageDetails(rsc, imageSession.getProject(), imageSession.getId(), mrScan.getId());
            		}else if (abs instanceof XnatResourcecatalogBean) {
        				XnatResourcecatalogBean rsc = (XnatResourcecatalogBean)abs;
        					pf = getImageDetails(rsc, imageSession.getProject(), imageSession.getId(), mrScan.getId());
        			}
            		}else {
            		for (int i =0; i < files.size(); i++) {
            			XnatAbstractresourceBean abs = (XnatAbstractresourceBean) files.get(i);
            			if (abs instanceof XnatImageresourceBean) {
            				XnatImageresourceBean rsc = (XnatImageresourceBean)abs;
            				if (rsc.getContent().equalsIgnoreCase("RAW") || rsc.getContent().equalsIgnoreCase(mrScan.getType()+"_RAW")) {
            					pf = getImageDetails(rsc, imageSession.getProject(), imageSession.getId());
            					break;
            				}         				
            			}else if (abs instanceof XnatImageresourceseriesBean) {
            				XnatImageresourceseriesBean rsc = (XnatImageresourceseriesBean)abs;
            				if (rsc.getContent().equalsIgnoreCase("RAW") || rsc.getContent().equalsIgnoreCase(mrScan.getType()+"_RAW") ) {
            					pf = getImageDetails(rsc, imageSession.getProject(), imageSession.getId());
            					break;
            				}         
            			}else if (abs instanceof XnatDicomseriesBean) {
            				XnatDicomseriesBean rsc = (XnatDicomseriesBean)abs;
            				if (rsc.getContent().equalsIgnoreCase("RAW")|| rsc.getContent().equalsIgnoreCase(mrScan.getType()+"_RAW")) {
            					pf = getImageDetails(rsc, imageSession.getProject(), imageSession.getId(), mrScan.getId());
            					break;
            				}         
            		}else if (abs instanceof XnatResourcecatalogBean) {
        				XnatResourcecatalogBean rsc = (XnatResourcecatalogBean)abs;
        				if (rsc.getContent().equalsIgnoreCase("RAW")|| rsc.getContent().equalsIgnoreCase(mrScan.getType()+"_RAW")) {
        					pf = getImageDetails(rsc, imageSession.getProject(), imageSession.getId(), mrScan.getId());
        					break;
        				}         
        			}
            	}
            }}
            }
		}catch(Exception e) {
			e.printStackTrace();
		}
		return pf;
	}
	
	
	
	private static PlexiImageFile getImageDetails(XnatImageresourceBean rsc, String project, String sessionId ) throws Exception {
		PlexiImageFile pf = new PlexiImageFile();
        String archivePathLocation = ArchivePathManager.GetInstance().getArchivePathLocation(project);
        String cachePathLocation = ArchivePathManager.GetInstance().getCachePathLocation(project, sessionId);

		pf.setURIAsString(ResolveURIAgainstArchivePath(archivePathLocation,rsc.getUri()).toString());
        pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCE);
        pf.setFormat(rsc.getFormat());
        if (rsc.getCachepath()!=null)
            pf.setCachePath(ResolveURIAgainstCachePath(cachePathLocation, rsc.getCachepath()).toString());
        else 
            pf.setCachePath(URIUtils.getURI(cachePathLocation + "/"+ sessionId).toString());
            if (rsc.getDimensions_x()!= null && rsc.getDimensions_y()!= null && rsc.getDimensions_z() != null)
                pf.setDimensions(rsc.getDimensions_x().intValue(),rsc.getDimensions_y().intValue(),rsc.getDimensions_z().intValue());
            if (rsc.getVoxelres_x() != null) {
                pf.setVoxelResX(rsc.getVoxelres_x().floatValue());
            }
            if (rsc.getVoxelres_y() != null) {
                pf.setVoxelResY(rsc.getVoxelres_y().floatValue());
            }
            if (rsc.getVoxelres_z() != null) {
                pf.setVoxelResZ(rsc.getVoxelres_z().floatValue());
            }

            if (rsc.getDimensions_volumes()==null)
                pf.setVolumes(1);
            else    
                pf.setVolumes(rsc.getDimensions_volumes().intValue());
		return pf;
	}

	private static PlexiImageFile getImageDetails(XnatImageresourceseriesBean rsc, String project, String sessionId ) throws Exception {
		PlexiImageFile pf = new PlexiImageFile();
        String archivePathLocation = ArchivePathManager.GetInstance().getArchivePathLocation(project);
        String cachePathLocation = ArchivePathManager.GetInstance().getCachePathLocation(project, sessionId);

        pf.setURIAsString(ResolveURIAgainstArchivePath(archivePathLocation,rsc.getPath()).toString());
        pf.setPath(org.nrg.xft.utils.FileUtils.AppendRootPath(archivePathLocation,rsc.getPath()));
        pf.setPattern(rsc.getPattern());
        pf.setFormat(rsc.getFormat());
        pf.setName(rsc.getName());
        pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCESERIES);

        if (rsc.getCachepath()!=null)
            pf.setCachePath(ResolveURIAgainstCachePath(cachePathLocation, rsc.getCachepath()).toString());
        else 
            pf.setCachePath(URIUtils.getURI(cachePathLocation + "/"+ sessionId).toString());
            if (rsc.getDimensions_x()!= null && rsc.getDimensions_y()!= null && rsc.getDimensions_z() != null)
                pf.setDimensions(rsc.getDimensions_x().intValue(),rsc.getDimensions_y().intValue(),rsc.getDimensions_z().intValue());
            if (rsc.getVoxelres_x() != null) {
                pf.setVoxelResX(rsc.getVoxelres_x().floatValue());
            }
            if (rsc.getVoxelres_y() != null) {
                pf.setVoxelResY(rsc.getVoxelres_y().floatValue());
            }
            if (rsc.getVoxelres_z() != null) {
                pf.setVoxelResZ(rsc.getVoxelres_z().floatValue());
            }

            if (rsc.getDimensions_volumes()==null)
                pf.setVolumes(1);
            else    
                pf.setVolumes(rsc.getDimensions_volumes().intValue());
		return pf;
	}

	private static PlexiImageFile getImageDetails(XnatDicomseriesBean rsc, String project, String sessionId, String scanNumber ) throws Exception {
		PlexiImageFile pf = new PlexiImageFile();
        String archivePathLocation = ArchivePathManager.GetInstance().getArchivePathLocation(project);
        String cachePathLocation = ArchivePathManager.GetInstance().getCachePathLocation(project, sessionId);
        List imageSet = rsc.getImageset_image();
        for (int j = 0; j < imageSet.size(); j++ ) {
            pf.addToFiles(ResolveURIAgainstArchivePath(archivePathLocation,((XnatDicomseriesImageBean)imageSet.get(j)).getUri()).toString());
        }
        pf.setXsiType(PlexiConstants.XNAT_DICOMSERIES);
        pf.setFormat(rsc.getFormat());
        if (scanNumber != null)
            pf.setName(sessionId + "_" + scanNumber);

        
        if (rsc.getCachepath()!=null)
            pf.setCachePath(ResolveURIAgainstCachePath(cachePathLocation, rsc.getCachepath()).toString());
        else 
            pf.setCachePath(URIUtils.getURI(cachePathLocation + "/"+ sessionId).toString());
            if (rsc.getDimensions_x()!= null && rsc.getDimensions_y()!= null && rsc.getDimensions_z() != null)
                pf.setDimensions(rsc.getDimensions_x().intValue(),rsc.getDimensions_y().intValue(),rsc.getDimensions_z().intValue());
            if (rsc.getVoxelres_x() != null) {
                pf.setVoxelResX(rsc.getVoxelres_x().floatValue());
            }
            if (rsc.getVoxelres_y() != null) {
                pf.setVoxelResY(rsc.getVoxelres_y().floatValue());
            }
            if (rsc.getVoxelres_z() != null) {
                pf.setVoxelResZ(rsc.getVoxelres_z().floatValue());
            }

            if (rsc.getDimensions_volumes()==null)
                pf.setVolumes(1);
            else    
                pf.setVolumes(rsc.getDimensions_volumes().intValue());
		return pf;
	}

	private static PlexiImageFile getImageDetails(XnatResourcecatalogBean rsc, String project, String sessionId, String scanNumber ) throws Exception {
		PlexiImageFile pf = new PlexiImageFile();
        String archivePathLocation = ArchivePathManager.GetInstance().getArchivePathLocation(project);
        String cachePathLocation = ArchivePathManager.GetInstance().getCachePathLocation(project, sessionId);
        String catalogPath = ResolveURIAgainstArchivePath(archivePathLocation,rsc.getUri()).toString();
        pf.setResourceCatalogPath(catalogPath);
        pf.setFormat(rsc.getFormat());
        pf.setXsiType(PlexiConstants.XNAT_RESOURCECATALOG);
        if (scanNumber != null)
            pf.setName(sessionId + "_" + scanNumber);
        
        if (rsc.getCachepath()!=null)
            pf.setCachePath(ResolveURIAgainstCachePath(cachePathLocation, rsc.getCachepath()).toString());
        else 
            pf.setCachePath(URIUtils.getURI(cachePathLocation + "/"+ sessionId).toString());
		return pf;
	}

	
	public static ArrayList parseFile(String pathToFile) {
		ArrayList rtn = new ArrayList();
		try {
			 BufferedReader in = new BufferedReader(new FileReader(pathToFile));
			 String str;
			 while ((str = in.readLine()) != null) {
				rtn.add(str);
			 }
			 in.close();
			} catch (IOException e) {
				System.out.println("Unable to read from file " + pathToFile);
			}
		return rtn;
	}
    
    
    
	public static ArrayList getFiles(SchemaLink s, String sessionId, String project, String scanNumber) {
		//This method should be overridden to take care of the fact that 
		ArrayList rtn = null;
		try {
			CriteriaCollection cc = new CriteriaCollection("AND");
			int elementNameIndex = s.getElementName().indexOf(".");
			System.out.println(sessionId + " " + scanNumber + " Files are required for " + s.toString());
			String elementName = s.getElementName().substring(0,elementNameIndex);
			cc.addClause(elementName+"." + XFTUtils.getSessionFieldName(elementName),sessionId);
			
			if (scanNumber!=null && scanNumber!="" && elementName.equals(XFTUtils.getXMLPathToScan())) {
				cc.addClause(elementName+".ID",scanNumber);
			}
            //System.out.println("FileUtils::getFiles:: " + s.getElementName() + " " + s.getValue() + " SessionID = " + sessionId + " SessionType = " + sessionType + " Scan Number " + scanNumber );
            String archivePathLocation = ArchivePathManager.GetInstance().getArchivePathLocation(project);
            String cachePathLocation = ArchivePathManager.GetInstance().getCachePathLocation(project, sessionId);
			ItemCollection ic = org.nrg.xft.search.ItemSearch.GetItems(cc,null,true);
			ArrayList parentList = ic.getItems();
			if (parentList!=null) {
				ArrayList filesList =  XFTUtils.getChildItems(parentList,s.getElementName(),s.getValue());
				if (filesList!=null) {
					rtn = new ArrayList();
					for (int i=0;i<filesList.size();i++) {
						XFTItem f = (XFTItem)filesList.get(i);
						PlexiImageFile pf= new PlexiImageFile();
                        if (f.getXSIType().equals(PlexiConstants.XNAT_IMAGERESOURCE)) {
                            pf.setURIAsString(ResolveURIAgainstArchivePath(archivePathLocation,f.getStringProperty("URI")).toString());
                            pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCE);
                            pf.setFormat(f.getStringProperty("format"));
                        }else if (f.getXSIType().equals(PlexiConstants.XNAT_IMAGERESOURCESERIES)) {
                            pf.setURIAsString(ResolveURIAgainstArchivePath(archivePathLocation,f.getStringProperty("path")).toString());
                            pf.setPath(org.nrg.xft.utils.FileUtils.AppendRootPath(archivePathLocation,f.getStringProperty("path")));
                            pf.setPattern(f.getStringProperty("pattern"));
                            pf.setName(f.getStringProperty("name"));
                            pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCESERIES);
                            pf.setFormat(f.getStringProperty("format"));
                        }else if (f.getXSIType().equals(PlexiConstants.XNAT_DICOMSERIES)) {
                           ArrayList imageSet = ((XFTItem)f).getChildItems("xnat:dicomSeries.imageSet.image");
                           for (int j = 0; j < imageSet.size(); j++ ) {
                               pf.addToFiles(ResolveURIAgainstArchivePath(archivePathLocation,((XFTItem)imageSet.get(j)).getStringProperty("URI")).toString());
                           }
                           pf.setXsiType(PlexiConstants.XNAT_DICOMSERIES);
                           pf.setFormat(f.getStringProperty("format"));
                           if (scanNumber != null)
                               pf.setName(sessionId + "_" + scanNumber);
                           //System.out.println("dicomSeries encountered " + pf.toString());
                        }else if (f.getXSIType().equals(PlexiConstants.XNAT_RESOURCECATALOG)) {
                            String catalogPath = ResolveURIAgainstArchivePath(archivePathLocation,f.getStringProperty("URI")).toString();
                            pf.setResourceCatalogPath(catalogPath);
                            pf.setFormat(f.getStringProperty("format"));
                            pf.setXsiType(PlexiConstants.XNAT_RESOURCECATALOG);
                            if (scanNumber != null)
                                pf.setName(sessionId + "_" + scanNumber);
                         }
                        if (f.getStringProperty("cachepath")!=null)
                            pf.setCachePath(ResolveURIAgainstCachePath(cachePathLocation, f.getStringProperty("cachepath")).toString());
                        else 
                            pf.setCachePath(URIUtils.getURI(cachePathLocation + "/"+ sessionId).toString());
                        try {
                            if (f.getIntegerProperty("dimensions.x") != null && f.getIntegerProperty("dimensions.y") != null && f.getIntegerProperty("dimensions.z") != null)
                                pf.setDimensions(f.getIntegerProperty("dimensions.x").intValue(),f.getIntegerProperty("dimensions.y").intValue(),f.getIntegerProperty("dimensions.z").intValue());
                            if (f.getStringProperty("voxelRes.x") != null) {
                                pf.setVoxelResX(Float.parseFloat(f.getStringProperty("voxelRes.x")));
                            }
                            if (f.getStringProperty("voxelRes.y") != null) {
                                pf.setVoxelResY(Float.parseFloat(f.getStringProperty("voxelRes.y")));
                            }
                            if (f.getStringProperty("voxelRes.z") != null) {
                                pf.setVoxelResZ(Float.parseFloat(f.getStringProperty("voxelRes.z")));
                            }
                            if (f.getIntegerProperty("dimensions.volumes")==null)
                                pf.setVolumes(1);
                            else    
                                pf.setVolumes(f.getIntegerProperty("dimensions.volumes").intValue());
                        }catch(FieldNotFoundException e) {
                          
                        }
                        System.out.println("FileUtils:: " + pf.toString());
						rtn.add(pf);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;		
		}
		return rtn;
	}
	
	public static ArrayList getFiles(SchemaLink s, String sessionId, String project) {
		ArrayList rtn = null;
		try {
            rtn = getFiles(s, sessionId, project, null);
		}catch(Exception e) {
			return null;		
		}
		return rtn;
	}
    
  
	
	public static void main(String[] args) {
		try {
			//XFT.init("C:\\jakarta-tomcat-5.5.4\\webapps\\cnda_xnat",true,false);
			//SchemaLink s = new SchemaLink();s.setElementName("xnat:reconstructedImageData.out.file"); s.setValue("T88");
			//ArrayList pfList = FileUtils.getFiles(s,"000111_vc1550");
			//System.out.println("Image File details are " + pf.getName() + " " + pf.getPath());
            //System.out.println(URIUtils.stripScheme("file:/C:/Archive/cache/cla"));
            /*BasicConfigurator.configure();
            try {
                XFTManager.GetInstance();
            }catch (XFTInitException xftInitE) {
                try {
                    XDAT.init("C:\\jakarta-tomcat-5.5.4\\webapps\\oasis");
                    System.out.println("XDAT Inited");
                    System.out.println("Archive LOcation " + XFT.GetArchiveRootPath());
                    ResolveURIAgainstArchivePath(new URI("disc1/clas"));
                }catch (Exception e) {
                     e.printStackTrace();
                }
            } */
            
            
		}catch(Exception e ) {
				e.printStackTrace();
			}	
	}

	
}
