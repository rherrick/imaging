/*
 * org.nrg.plexiViewer.io.ImageLocator
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */
package org.nrg.plexiviewer.io;

import ij.io.FileInfo;

import org.nrg.plexiviewer.Reader.PlexiImageHeaderReader;
import org.nrg.plexiviewer.converter.ConverterUtils;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.utils.ImageDetails;
import org.nrg.plexiviewer.lite.utils.LiteFileUtils;
import org.nrg.plexiviewer.manager.PlexiStatusPublisherManager;
import org.nrg.plexiviewer.utils.ArchivePathManager;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.PlexiConstants;
import org.nrg.plexiviewer.utils.URIUtils;

public class ImageLocator {
 public PlexiImageFile getImage(UserSelection options) throws Exception {
		PlexiImageFile hpf = new PlexiImageFile();
		PlexiImageFile pf = new PlexiImageFile();
		String cloneWithoutFiles = options.toString();
		boolean build = false;
		String format=null;
	

        String cachePathLocation = ArchivePathManager.GetInstance().getCachePathLocation(options.getProject(), options.getSessionLabel());
		String loresLocation = ArchivePathManager.GetInstance().getLoResLocation(options.getProject(),options.getSessionLabel());
		String loresType = ImageDetails.getDefaultLoresType();
    	if (options.hasFile()) { 
			hpf = options.getFile();
		}else if (options.hasXnatFile()) {
			hpf = IOHelper.getPlexiImageFile(options.getXnatFile(), cachePathLocation, options.getFormattedWindowTitle() + "_" +  LiteFileUtils.getFileName(options.getXnatFile()));
		}
		System.out.println("ImageLocator HIRes File Details are " + hpf);
		//Try to look for LORES FILE first
		options.setLoResType(loresType);
		System.out.println("ImageLocator LoRes Type to look out for are " + options.getLoResType());
			pf.setPath(URIUtils.getURI(loresLocation).toString());
			if (hpf.getFormat()!=null && hpf.getFormat().equals("IMA")) {
				pf.setName(FileUtils.getLoResFileName(hpf.getName()+"_" + options.getScanNo()+".4dfp.img",loresType, options.getOrientation()));
				
			}else
			pf.setName(FileUtils.getLoResFileName(hpf.getName()+"_"+options.getScanNo(),loresType, options.getOrientation()));
            if (pf.getPath().endsWith("/"))
                pf.setURIAsString(pf.getPath() + pf.getName());
            else
                pf.setURIAsString(pf.getPath() + "/" + pf.getName());
			pf.setCachePath(hpf.getCachePath());
            pf.setXsiType(PlexiConstants.PLEXI_IMAGERESOURCE);
			format=ImageDetails.getDefaultLoresFormat();
			System.out.println("ImageLocator LoRes File to be looked out for " + pf.toString() + " Format " + format);
		pf =  FileUtils.fileExists(pf);
        if (pf == null) {    
			pf=hpf;                     
            format= pf.getFormat();
            build = true;
        }

            System.out.println("ImageLocator will look for " + pf.toString());
//           	options.setFile(hpf);
            if (build) {
            	PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Building requested Image");
            	pf  = ConverterUtils.convert(options);
    			format = pf.getFormat();
            }else {
            	PlexiStatusPublisherManager.GetInstance().getPublisher(cloneWithoutFiles).setValue("Delivering requested Image");
            }
			PlexiImageHeaderReader xIReader = new PlexiImageHeaderReader(format);//Returns the header type ie ANALYZE or IFH etc
            String path = pf.getPath();
            if (path.startsWith("file:")) path = path.replaceAll("file:","");
			//READ THE LORES FILE DETAILS
            FileInfo fi = xIReader.getFileInfo(path,pf.getName()); //directory and filename
            pf = hpf;
            //ON THE SUBSEQUENT CALL THE HIRES DETAILS ARE AVAILABLE
     /*       pf.setURIAsString(hpf.getURIAsString());
			pf.setFormat(hpf.getFormat());
			if (hpf.getResourceCatalogPath()!=null) pf.setResourceCatalogPath(hpf.getResourceCatalogPath());
			else {
				pf.setPath(hpf.getPath());
				pf.setName(hpf.getName());
			}
            pf.setXsiType(hpf.getXsiType());
            if (hpf.getCachePath() != null)
                pf.setCachePath(hpf.getCachePath()); */
			//pf.setDimX(ImageUtils.getWidth(fi.width, fi.height, fi.nImages, xIReader.getOrientation(), options.getOrientation()));
			//pf.setDimY(ImageUtils.getHeight(fi.width, fi.height, fi.nImages, xIReader.getOrientation(), options.getOrientation()));
			//pf.setDimZ(ImageUtils.getStackSize(fi.width, fi.height, fi.nImages, xIReader.getOrientation(), options.getOrientation()));
            //SEND DIMENSIONS OF THE LORES FILE WHICH IS TO BE DISPLAYED
            pf.setDimX(fi.width);
            pf.setDimY(fi.height);
            pf.setDimZ(fi.nImages);
            pf.setFileType(fi.fileType);
            pf.setVoxelResX(fi.pixelWidth);
            pf.setVoxelResY(fi.pixelHeight);
            pf.setVoxelResZ(fi.pixelDepth);
			
			
			return pf;
 }
}
