/*
 * org.nrg.plexiViewer.io.PlexiFileOpener
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.io;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.process.StackProcessor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.nrg.nrrd.NRRDReader;
import org.nrg.plexiviewer.Reader.PlexiImageHeaderReader;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.PlexiConstants;
import org.nrg.plexiviewer.utils.URIUtils;
import org.nrg.plexiviewer.utils.Transform.ImaTo4dfpBuilder;
import org.nrg.plexiviewer.utils.Transform.PlexiImageOrientor;
import org.nrg.plexiviewer.utils.Transform.PlexiMontageMaker;
import org.nrg.plexiviewer.utils.Transform.plexiViewerImageRelayer;
import org.nrg.plexiviewer.utils.imageformats.DicomFileOpener;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatEntryBean;
import org.nrg.xdat.bean.base.BaseElement;
import org.nrg.xdat.bean.reader.SRBXDATXMLReader;
import org.nrg.xnat.srb.XNATDirectory;
import org.nrg.xnat.srb.XNATSrbSearch;

import edu.sdsc.grid.io.local.LocalFile;
import edu.sdsc.grid.io.srb.SRBFileSystem;


public class PlexiFileOpener {

	String orientation;
	String fileType;
	
	FileInfo fi;
    PlexiImageFile pf;
    String fileSystemPath = null, fromFileName;
    boolean remote = false;
    LocalFile tempdir = null;
    
    public PlexiFileOpener(String format,PlexiImageFile p) {
            fileType = format;
            pf=p;
           // System.out.println(fileType + " PlexiFileOpener Constructor " + pf.toString());
            remote = isRemote(pf);
    }
    
    public LocalFile getDir() throws IOException {
        String suffix =  "_" + new Random().nextInt();
        LocalFile tempDir = new LocalFile(new File(FileUtils.getTempFolder()));
        LocalFile dir = (LocalFile) LocalFile.createTempFile( "NRG", suffix,  tempDir);
        if (dir.exists()) dir.delete();
        dir.mkdir();
        return dir;
    }
    
    public  boolean isRemote(PlexiImageFile pf) {
        boolean rtn = false;
        if (PlexiConstants.PLEXI_IMAGERESOURCE.equals(pf.getXsiType()) ||PlexiConstants.XNAT_IMAGERESOURCE.equals(pf.getXsiType()) ) {
            try {
                rtn = new URIUtils(new URI(pf.getURIAsString())).isRemote();
            }catch(URISyntaxException urie) {System.out.println("PlexiFileOpener::isRemote " + urie.getMessage() + " " + pf.getURIAsString());}
        }else if (PlexiConstants.XNAT_DICOMSERIES.equalsIgnoreCase(pf.getXsiType())) {
            if (pf.getFiles() != null && pf.getFiles().size() > 0) {
                rtn = new URIUtils((String)pf.getFiles().get(0)).isRemote(); 
            }
        }else if (PlexiConstants.XNAT_IMAGERESOURCESERIES.equals(pf.getXsiType())) {
            if (pf.getPath() != null) {
                try {
                    rtn = new URIUtils(new URI(pf.getPath())).isRemote();
                }catch(Exception e) {System.out.println("PlexiImageFile:: isRemote Recd malformed URI " + pf.getPath());}
            }
        }else if (PlexiConstants.XNAT_RESOURCECATALOG.equals(pf.getXsiType())) {
            if (pf.getResourceCatalogPath() != null) {
                try {
                    rtn = new URIUtils(new URI(pf.getResourceCatalogPath())).isRemote();
                }catch(Exception e) {System.out.println("PlexiImageFile:: isRemote Recd malformed URI " + pf.getResourceCatalogPath());}
            }
        }
        return rtn;
    }

    
    
    public ArrayList copyLocal(ArrayList fileList) throws IOException,URISyntaxException {
        ArrayList rtn = new ArrayList();
        if (remote) {
            LocalFile dir = getDir();
            fileSystemPath = dir.getPath();
            for (int i = 0; i < fileList.size(); i++) {
                copyLocal((URI)fileList.get(i), dir);
                rtn.add(new File(fileSystemPath+File.separator+fromFileName));
            }
            tempdir = dir;
        }else {
            for (int i = 0; i < fileList.size(); i++) {
            	File f = new File((String)fileList.get(i));
            	if (!f.exists()) {
            		URI uri = URIUtils.getURI((String)fileList.get(i));
            		f = new File(uri);
            	}
                rtn.add(f);
            }
        }
        return rtn;
    }
    
    
    public boolean copyLocal(String pfUriAsString,String pfPath,String pfName, String pfPattern, LocalFile dir) {
        boolean success = true;
        String path = null;
        String name = null;
        try {
            URI pfUri = new URI(pfUriAsString);
            URIUtils uUtils = new URIUtils(pfUri);
            if (remote) {
                XNATDirectory srbDir = XNATSrbSearch.getFilesLike(pfPath,pfPattern);
                if (srbDir == null) return !success;
                srbDir.importFiles(dir);
                if (fileSystemPath == null)
                    fileSystemPath = dir.getPath();
                fromFileName = pfName;
                tempdir = dir;
            }    
            if (fileSystemPath == null)
                fileSystemPath = path;
            fromFileName = name;
            return success;
        }catch (Exception e) {
            fromFileName = null; fileSystemPath = null;
            return !success;
        }
   }

    public boolean copyLocal(URI pathUri, LocalFile dir) {
        URIUtils uUtils = new URIUtils(pathUri);
        boolean success = true;
        String path = null;
        String name = null;
        try {
            path = uUtils.getPath();
            name = uUtils.getName();
            if (path == null || name == null) {
                fromFileName = null; fileSystemPath = null;
                return !success;
            }
            if (remote) {
                if (dir == null) {
                 dir = getDir();
                 tempdir = dir;
                }
                path = StringUtils.replace(path,SRBFileSystem.PATH_SEPARATOR + name,"");
                //System.out.println("SRB SEARCH FOR " + path + "  " + " NAME:: " + name);
                XNATDirectory srbDir = XNATSrbSearch.getFilesAssociatedWith(path,name);
                if (srbDir == null) return !success;
                srbDir.importFiles(dir);
                //System.out.println("SRBDIR contents " + srbDir);
                if (fileSystemPath == null)
                    fileSystemPath = dir.getPath();
            }else {
                path = StringUtils.replace(path,File.separator + name,"");
            }
            if (fileSystemPath == null)
                 fileSystemPath = path;   
            fromFileName = name;
           // System.out.println("PlexiFileOpener::CopyLocal:: Remote " + remote + " " + fileSystemPath + " " + fromFileName);
            return success;
        }catch (Exception e) {
                fromFileName = null; fileSystemPath = null;
                return !success;
        }    
    }
    
    
    private ArrayList readResourceCatalog(String fullPath) throws Exception {
            String path = fullPath;
            try {
               File f = new File(fullPath); 
               if (f.exists()) {
                   path = f.getAbsolutePath();
               }else {
                   f = new File(new URI(fullPath));
                   path = f.getAbsolutePath();
               }
            }catch(Exception e) {e.printStackTrace();}
            SRBXDATXMLReader reader = new SRBXDATXMLReader();
            BaseElement base = reader.parse(path);
            ArrayList rtn =  new ArrayList();
            URI parentURI = new URI(fullPath);
            if (base instanceof CatCatalogBean){
            	List entries = ((CatCatalogBean)base).getEntries_entry();
                for(int j = 0; j < entries.size() ; j++){
                    CatEntryBean entry = (CatEntryBean)entries.get(j);
                    //COMBINE parent path and entry path to obtain absolute path.
                    rtn.add(parentURI.resolve(entry.getUri()).toString());
                }
            }
            return rtn;
    }

    
    private ArrayList getResourceCatalogEntries(String fullPath) throws Exception {
        String path = fullPath;
        try {
           File f = new File(fullPath); 
           if (f.exists()) {
               path = f.getAbsolutePath();
           }else {
               f = new File(new URI(fullPath));
               path = f.getAbsolutePath();
           }
        }catch(Exception e) {e.printStackTrace();}
        SRBXDATXMLReader reader = new SRBXDATXMLReader();
        BaseElement base = reader.parse(path);
        ArrayList rtn =  new ArrayList();
        URI parentURI = new URI(fullPath);
        if (base instanceof CatCatalogBean){
            List entries = ((CatCatalogBean)base).getEntries_entry();
            for(int j = 0; j < entries.size() ; j++){
                CatEntryBean entry = (CatEntryBean)entries.get(j);
                //COMBINE parent path and entry path to obtain absolute path.
                entry.setUri(parentURI.resolve(entry.getUri()).toString());
                rtn.add(entry);
            }
        }
        return rtn;
}

    public ImagePlus getImagePlus() {
        try {
            if (fileType.equalsIgnoreCase("DICOM")) {
                if (pf.getFiles() != null && pf.getFiles().size() > 0) {
                    ArrayList localFiles = copyLocal(pf.getFiles());
                    DicomFileOpener dcmSeq = new DicomFileOpener(localFiles);
                    //DicomSequence dcmSeq = new DicomSequence(localFiles);
                    ImagePlus img= dcmSeq.getImagePlus();
                    orientation = dcmSeq.getOrientation();
                    fi=img.getOriginalFileInfo();
                    //System.out.println("Images Obtained Remote " + remote + " tempdir " + tempdir);
                    if (remote) FileUtils.deleteFile(tempdir);
                    return img;
                }else if (pf.getPath() != null){
                    copyLocal(new URI(pf.getPath()), null);
                    if (fileSystemPath == null) return null;
                    DicomFileOpener dcmSeq = new DicomFileOpener(fileSystemPath);
                    //DicomSequence dcmSeq = new DicomSequence(fileSystemPath);
                    ImagePlus img= dcmSeq.getImagePlus();
                    orientation = dcmSeq.getOrientation();
                    fi=img.getOriginalFileInfo();
                    FileUtils.deleteFile(tempdir);
                    return img;
                }else if (pf.getXsiType().equals(PlexiConstants.XNAT_RESOURCECATALOG)) {
                    //Read the Resource Catalog 
                    ArrayList localFilePaths = readResourceCatalog(pf.getResourceCatalogPath());
                    ArrayList localFiles = copyLocal(localFilePaths);
                    DicomFileOpener dcmSeq = new DicomFileOpener(localFiles);
                    //DicomSequence dcmSeq = new DicomSequence(localFiles);
                    ImagePlus img= dcmSeq.getImagePlus();
                    orientation = dcmSeq.getOrientation();
                    fi=img.getOriginalFileInfo();
                    //System.out.println("Images Obtained Remote " + remote + " tempdir " + tempdir);
                    if (remote) FileUtils.deleteFile(tempdir);
                    return img;
                }else return null;
            }else if (fileType.equalsIgnoreCase("NRRD")) {
            	NRRDReader nrrdReader = null;
            	if (pf.getXsiType().equals(PlexiConstants.XNAT_RESOURCECATALOG)) {
                    ArrayList localFilePaths = readResourceCatalog(pf.getResourceCatalogPath());
                    ArrayList localFiles = copyLocal(localFilePaths);
                    File file = ((File)localFiles.get(0));
                    nrrdReader = new NRRDReader(file.getParent(), file.getName());
                    orientation = nrrdReader.getOrientation();
                    ImagePlus img =  nrrdReader.getImagePlus();
                    nrrdReader.clearTempFolder();
                    return img;
            	}
            }/*else if (fileType.equalsIgnoreCase("NIFTI")) {
                ArrayList localFilePaths = readResourceCatalog(pf.getResourceCatalogPath());
                ArrayList localFiles = copyLocal(localFilePaths);
                File file = ((File)localFiles.get(0));
            	NiftiReader nr = new NiftiReader(file.getParent(), file.getName());
            	ImagePlus img = nr.getImagePlus();
                orientation = nr.getOrientation();
                nr.clearTempFolder();
                return img;
            }*/
            URI fileUri = null;
            if (pf.getXsiType().equals(PlexiConstants.XNAT_RESOURCECATALOG) ) {
                ArrayList localFilePaths = readResourceCatalog(pf.getResourceCatalogPath());
                ArrayList localFiles = copyLocal(localFilePaths);
                File file = ((File)localFiles.get(0));
                fileSystemPath = file.getParent();
                fromFileName = file.getName();
            }else if (pf.getXsiType().equals(PlexiConstants.XNAT_IMAGERESOURCE) ) {
                try {
                    fileUri = new URI(pf.getURIAsString());
                }catch(URISyntaxException urie) {System.out.println("PlexiFileOpenere::getImagePlus invalid uri " + pf.getURIAsString());}
                if (fileUri != null) {
                    copyLocal(fileUri,null);
                }
                //System.out.println("PF is " + PlexiConstants.XNAT_IMAGERESOURCE);
            }else if (pf.getXsiType().equals(PlexiConstants.PLEXI_IMAGERESOURCE) ) {
                fileSystemPath = pf.getPath();
                if (fileSystemPath.startsWith("file:")) fileSystemPath = fileSystemPath.substring(5);
                fromFileName = pf.getName();
               // System.out.println("PF is " + PlexiConstants.PLEXI_IMAGERESOURCE);
            }else if (pf.getXsiType().equals(PlexiConstants.XNAT_IMAGERESOURCESERIES)) {
                if (pf.getPattern() != null)
                    copyLocal(pf.getURIAsString(),pf.getPath(),pf.getName(), pf.getPattern(), getDir());
               // System.out.println("PF is " + PlexiConstants.XNAT_IMAGERESOURCESERIES);
            } else {
                fileSystemPath = pf.getPath();
                fromFileName = pf.getName();
                System.out.println("PF is not an xnat type image" );
            }
            //System.out.println("FILE SYSTEM PATH " + fileSystemPath);
            //System.out.println("FILE NAME " + fromFileName);
            PlexiImageHeaderReader xIReader =
                new PlexiImageHeaderReader(fileType);
            //Returns the header type ie ANALYZE or IFH etc
            fi = xIReader.getFileInfo(fileSystemPath, fromFileName);
            //directory and filename
            if (fi == null) {
                System.out.println(
                    "The Image File Reader returned a null FileInfo object\n");
                return null;
            }
            if (!xIReader.isGIF_JPG_Others()) {
                FileOpener fo = new FileOpener(fi);
                ImagePlus image = fo.open(false);
                /*System.out.println(
                    "PlexiFileOPener::Image was opened "
                        + fi.directory
                        + File.separator
                        + fi.fileName);*/
                if (image == null) {
                    System.out.println(
                        "PlexiFileOpener::Couldnt open file: "
                            + fileSystemPath
                            + fromFileName);
                    return null;
                }
                orientation = xIReader.getOrientation();
                Calibration c = new Calibration();
                c.pixelWidth = fi.pixelWidth;
                c.pixelHeight = fi.pixelHeight;
                c.pixelDepth = fi.pixelDepth;
                c.setUnit(fi.unit);
                image.setCalibration(c);
                image.setFileInfo(fi);
                if (remote) FileUtils.deleteFile(tempdir);
                xIReader.clearTempFolder();
                return image;
            } else {
                Opener fo = new Opener();
                ImagePlus image = fo.openImage(fileSystemPath, fromFileName);
                if (remote) FileUtils.deleteFile(new File(fileSystemPath));
                xIReader.clearTempFolder();
                return image;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
 
    
    

	/**
	 * @return
	 */
	public String getOrientation() {
		return orientation;
	}

	/**
	 * @param string
	 */
	public void setOrientation(String string) {
		orientation = string;
	}

	public FileInfo getFileInfo() {
		return fi;
	}
    
    
   
    public static ImagePlus openBaseFile(String format, PlexiImageFile pf, boolean radiologic) {
       PlexiFileOpener pfo = new PlexiFileOpener(format,pf);
        ImagePlus image = null;
          image = pfo.getImagePlus();   
        
       if (image==null) {
           System.out.println("Image Converter....couldnt find the  Image File");
           return null;
       }
       String baseOrientation = pfo.getOrientation();
       
       if (format != null && format.equalsIgnoreCase("IFH") ) {
            image=new plexiViewerImageRelayer(baseOrientation).Format4dfpImageForImageJ(image);
            baseOrientation = pfo.getOrientation()+"F";
            pf.setOrientation(baseOrientation);
       }else if (format != null && format.startsWith("ANALYZE")) {
            if (!radiologic) {
                if (image!=null && !radiologic) {
                   StackProcessor sp = new StackProcessor(image.getStack(), image.getProcessor());
                   sp.flipHorizontal();
                   if (pfo.getOrientation().equalsIgnoreCase("sagittal")){
                    image=new plexiViewerImageRelayer(baseOrientation).reverseStacks(image);
                   }    
                }   
           }
            PlexiImageOrientor pio=new PlexiImageOrientor(image,format);
            image = pio.getImage(baseOrientation,baseOrientation+"F");
            pf.setOrientation(baseOrientation+"F");
        }else {
            pf.setOrientation(baseOrientation);
        }

       return image;        
    }
    
    public static ImagePlus openBaseFile(PlexiImageFile pf, boolean radiologic) {
       ImagePlus image = null;
       ImaTo4dfpBuilder imato4dfp = new ImaTo4dfpBuilder(pf);
       if (imato4dfp.create()) 
           image = openBaseFile(pf.getFormat(), pf, radiologic);
       return image;
    }  
    
  

	public static void main(String[] args) {
       try {
           PlexiImageFile pf = new PlexiImageFile();
         
           //pf.setURIAsString("file:/Y:/data2/CNDA_HOME/CURRENT_ARC/051009_3915-2/PROCESSED/MPRAGE/T88_111/051009_3915-2_mpr_n2_111_t88.4dfp.img");
           //pf.setURIAsString("srb://dmarcus@wustl-nrg-gpop.nbirn.net/home/Public/OASIS/cross-sectional/disc12/OAS1_0454_MR1/FSL_SEG/OAS1_0454_MR1_mpr_n3_anon_111_t88_masked_gfc_fseg.img");
           pf.setURIAsString("file:/C:/Users/NRG/Downloads/MRH0001001_5_10_2011_14_19_39/MRH0001001/SCANS/1/MPRAGE/struc.nii");
           pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCE);
           PlexiFileOpener pfo = new PlexiFileOpener("NIFTI",pf);
           
           ImagePlus img = pfo.getImagePlus();
           System.out.println("Image is " + img.getProcessor().getClass().getName());
           PlexiMontageMaker mm = new PlexiMontageMaker();
           ImagePlus montage = mm.makeMontage(img,2,1,1,54,56,1,true,false);
           
           montage.getProcessor().drawString("Mohana",50,50);
           montage.show();
           System.out.println(
                "Found the orientation to be " + pfo.getOrientation());
           //System.exit(0);
       }catch(Exception e) {
           e.printStackTrace();
       }
		//PlexiImageOrientor pio = new PlexiImageOrientor(img, "DICOM");
        //ImagePlus img1 = pio.getImage(pfo.getOrientation(), "SAGITTAL");
        //img1=new plexiViewerImageRelayer("CORONAL").Format4dfpImageForImageJ(img1);
		//ImagePlus i = new plexiViewerImageRelayer("SAGITTAL").Format4dfpImageForImageJ(img1);
		//i = org.nrg.plexiviewer.lite.utils.Transform.PlexiReOrient.reOrient(i, pfo.getOrientation(),"TRANSVERSE", null, true, false);
		//org.nrg.plexiviewer.utils.Transform.PlexiImageOrientor pio = new org.nrg.plexiviewer.utils.Transform.PlexiImageOrientor(i);
		//i = pio.getImage(pfo.getOrientation(), "SAGITTAL");
		//show();
        //img1.show();
	}

}