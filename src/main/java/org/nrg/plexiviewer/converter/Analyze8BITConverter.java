/*
 * org.nrg.plexiViewer.converter.Analyze8BITConverter
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */
package org.nrg.plexiviewer.converter;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.process.StackConverter;

import java.io.File;
import java.util.Enumeration;

import org.nrg.plexiviewer.Writer.AnalyzeWriter;
import org.nrg.plexiviewer.io.PlexiFileOpener;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.xml.HiRes;
import org.nrg.plexiviewer.lite.xml.LoRes;
import org.nrg.plexiviewer.lite.xml.ViewableItem;
import org.nrg.plexiviewer.utils.PlexiConstants;
import org.nrg.plexiviewer.manager.PlexiSpecDocReader;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.Transform.PlexiImageOrientor;
import org.nrg.plexiviewer.utils.Transform.TransformationTools;
public class Analyze8BITConverter implements plexiLoResConverterI {
	HiRes hRes;
	LoRes lRes;
	UserSelection options;
	FileInfo fi;
	String fromFileName, toFileName, fromPath, toPath, cachePath;
	String baseOrientation;
	double minIntensity, maxIntensity;
	boolean single=false;
	ViewableItem viewableItem ;
	

	public Analyze8BITConverter(UserSelection options) {
		this.options = options;
		viewableItem = PlexiSpecDocReader.GetInstance().getSpecDoc(options.getProject()).getViewableItem(options.getDataType());
		hRes = viewableItem.getHiRes();
		lRes = viewableItem.getLoRes(options.getLoResType());
	}
		
	public int convert() {
		if (options.hasFile() || options.getOrientation()!=null) {
			single=true;
		}
		System.out.println("Create for a single file: " + single);
		return doConvert();
	}	

	private ImagePlus getImage(Integer layerNum) {
			String sessionId = options.getSessionId();
			String scanNo = options.getScanNo();
			PlexiImageFile pf = FileUtils.getHiResFilePath(sessionId,options.getProject(),options.getDataType(), layerNum.intValue(), options.getScanNo());
			fromPath = pf.getPath();
			fromFileName = pf.getName(); 
			System.out.println("From File Name is " + fromFileName);
			cachePath = pf.getCachePath();
			ImagePlus image = openBaseFile();
			if (image==null) {
				//Build the image from IMA
			}
			if (image==null)
				image = openBaseFile();
			if (image==null) {
				System.out.println("Couldnt create HiRes image...Exiting\n");
				return null;
			}
			return image;
	}	


	private ImagePlus getImage() {
			String sessionId = options.getSessionId();
			String scanNo = options.getScanNo();
			PlexiImageFile pf = FileUtils.getHiResFilePath(sessionId,options.getProject(),options.getDataType(), options.getHiResLayerNum(), options.getScanNo()); 
			fromPath = pf.getPath();
			fromFileName = pf.getName();
			cachePath = pf.getCachePath(); 
			//System.out.println("From File Name " + fromFileName);
			ImagePlus image = openBaseFile();
			if (image==null) {
				//Build the image from IMA
			}
			if (image==null)
				image = openBaseFile();
			if (image==null) {
				System.out.println("Couldnt create HiRes image...Exiting\n");
				return null;
			}
			return image;
	}	

	private void setNamesAndCreate8Bit(ImagePlus image) {
		toFileName = FileUtils.getLoResFileName(fromFileName,options.getLoResType(),options.getOrientation());
		toPath = PlexiSpecDocReader.GetInstance().getSpecDoc(options.getProject()).getLoResArchiveLocation(options.getSessionId());
		if (!FileUtils.fileExists(toPath, toFileName) || options.canOverwrite()){
			ImagePlus adjust = new ImagePlus();
			adjust.setCalibration(image.getCalibration());
			adjust.setStack("",image.getStack());
			doPerform(adjust);
		}else {
			System.out.println("File already exists" + toPath + File.separator + toFileName);
		}
	}
	
	private int doConvert() {		
		String sessionId = options.getSessionId();
		int status =0;
		if (single) {
			ImagePlus image =  getImage();
			setNamesAndCreate8Bit(image);
		}else {
			for (Enumeration e = hRes.getLayersHash().keys(); e.hasMoreElements();) {
				Integer layerNum = (Integer)e.nextElement();
				ImagePlus image =  getImage(layerNum);
				String oldOri = options.getOrientation();
				options.setOrientation("Transverse"); setNamesAndCreate8Bit(image);
				options.setOrientation("Coronal"); setNamesAndCreate8Bit(image);
				options.setOrientation("Sagittal"); setNamesAndCreate8Bit(image);
				options.setOrientation(oldOri);
			}
		}
		System.gc();
		return status;
	}

	private ImagePlus openBaseFile() {
       PlexiImageFile pf = new PlexiImageFile();
       pf.setPath(fromFileName);
       pf.setPath(fromPath);
       pf.setXsiType(PlexiConstants.PLEXI_IMAGERESOURCE);
	   PlexiFileOpener pfo = new PlexiFileOpener(viewableItem.getHiRes().getFormat(),pf);
	   ImagePlus image = pfo.getImagePlus();

	   if (image==null) {
		   System.out.println("Image Converter....couldnt find the  Image File");
		   return null;
	   }
	   if (pfo.getFileInfo().fileType!=FileInfo.GRAY8) {
		  //Convert to 8 bit and send
		  new StackConverter(image).convertToGray8();
	   }
	   baseOrientation = pfo.getOrientation();
	   return image;		
	}
	
	private int doPerform(ImagePlus img) {
		try {
			if (lRes.getCropDetails().deleteSlices()) {
				img = TransformationTools.dropSlices(img, lRes.getCropDetails().getStartSlice(),lRes.getCropDetails().getEndSlice());
			}
			if (lRes.getCropDetails().crop()) {
				img = TransformationTools.crop(img, lRes.getCropDetails().getBoundingRectangle());
			}
			img = new PlexiImageOrientor(img).getImage(baseOrientation,options.getOrientation()+"F");
			if (!FileUtils.dirExists(toPath)) {
				FileUtils.createDirectory(toPath);	  
			}
			new FileSaver(img).saveAsRawStack(toPath +File.separator+toFileName);
			img.setFileInfo(getFileInfo(img.getCalibration(), img.getWidth(), img.getHeight(), img.getStackSize()));
			new AnalyzeWriter().save(img,toPath,toFileName,options.getOrientation()+"F");
			return 0;			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			return -1;
		}
		
	}
	
    public PlexiImageFile getFileLocationAndName() {
        PlexiImageFile pf = new PlexiImageFile();
        pf.setName(toFileName);
        pf.setPath(toPath);
        pf.setCachePath(cachePath);
        return pf;
    }
    
	private FileInfo getFileInfo(Calibration cal, int w, int h, int s) {
			FileInfo fi = new FileInfo();
			fi.fileType = FileInfo.GRAY8;
			fi.width = w;
			fi.height = h;
			fi.nImages = s;
			fi.pixelWidth = cal.pixelWidth;
			fi.pixelHeight = cal.pixelHeight;
			fi.pixelDepth = cal.pixelDepth;
			return fi;
		}
	
	public int convertAndSave(UserSelection u) {
		this.options = u;
		return convert();				
	}
}
