/*
 * org.nrg.plexiViewer.converter.OasisDefaultConverter
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.converter;

/**
 * @author Mohana
 *
 */

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileSaver;
import org.nrg.plexiviewer.Writer.AnalyzeWriter;
import org.nrg.plexiviewer.io.PlexiFileOpener;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.xml.HiRes;
import org.nrg.plexiviewer.lite.xml.LoRes;
import org.nrg.plexiviewer.lite.xml.ViewableItem;
import org.nrg.plexiviewer.manager.PlexiSpecDocReader;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.ImageUtils;
import org.nrg.plexiviewer.utils.Transform.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Enumeration;
public class OasisDefaultConverter implements plexiLoResConverterI {
	HiRes hRes;
	LoRes lRes=null;
	UserSelection options;
	FileInfo fi;
	String fromFileName, toFileName=null, fromPath, toPath=null, cachePath;
	String baseOrientation;
	double minIntensity, maxIntensity;
	boolean single=false;
	ViewableItem viewableItem ;

	public OasisDefaultConverter(UserSelection options) {
		this.options = options;
		viewableItem = PlexiSpecDocReader.GetInstance().getSpecDoc(options.getProject()).getViewableItem(options.getDataType());
		hRes = viewableItem.getHiRes();
		if (options.getLoResType()!=null)
			lRes = viewableItem.getLoRes(options.getLoResType());
	}
		
	public int convert() {
		if (options.getHiResLayerNum()>=0 && options.getOrientation()!=null) {
			single=true;
		}else if (options.hasFile() && options.getOrientation()!=null)
			single=true;
		System.out.println("Create for a single file: " + single);
		return doConvert();
	}	

	private ImagePlus getImage(Integer layerNum) {
			String sessionId = options.getSessionId();
			String scanNo = options.getScanNo();
			PlexiImageFile pf = FileUtils.getHiResFilePath(sessionId,options.getProject(),options.getDataType(), layerNum.intValue(),  options.getScanNo());
			fromPath = pf.getPath();
			fromFileName = pf.getName(); 
			System.out.println("From File Name is " + fromFileName);
			cachePath = pf.getCachePath();
			ImagePlus image=null;
			image = openBaseFile(pf);
			if (image==null) {
				System.out.println("Couldnt create HiRes image...Exiting\n");
				return null;
			}
			return image;
	}	


	private ImagePlus getImage() {
			String sessionId = options.getSessionId();
			String scanNo = options.getScanNo();
			PlexiImageFile pf = options.getFile();
			if (pf==null) //Perform the SQL query 
			 pf = FileUtils.getHiResFilePath(sessionId,options.getProject(),options.getDataType(), options.getHiResLayerNum(), options.getScanNo()); 
			fromPath = pf.getPath();
			fromFileName = pf.getName();
			cachePath = pf.getCachePath(); 
			ImagePlus image=null;
			image = openBaseFile(pf);
			if (image==null) {
				System.out.println("Couldnt create HiRes image...Exiting\n");
				return null;
			}
			return image;
	}	

	private String getToFileName() {
		return toFileName;
	}	
	
	private String setToFileName() {
		String ext="HiRes";
		if (options.getLoResType()!=null) {
			ext=options.getLoResType();
		}
		toFileName = FileUtils.getLoResFileName(fromFileName,ext,options.getOrientation());
		return toFileName;
	}

	private void setNames(ImagePlus image) {
		setToFileName();
		if (toPath==null) toPath = toPath = PlexiSpecDocReader.GetInstance().getSpecDoc(options.getProject()).getLoResArchiveLocation(options.getSessionId());
		if (!FileUtils.fileExists(toPath, toFileName) || options.canOverwrite()){
			ImagePlus adjust = new ImagePlus();
			adjust.setCalibration(image.getCalibration());
			adjust.setStack("",image.getStack());
            adjust.setFileInfo(image.getOriginalFileInfo());
			doPerform(adjust);
			image.flush();			image=null;
			adjust.flush(); adjust=null;
		}else {
			System.out.println("File already exists" + toPath + File.separator + toFileName);
		}
	}
	
	private int doConvert() {		
		String sessionId = options.getSessionId();
		int status =0;
		if (single) {
			ImagePlus image =  getImage();
			if (image==null){System.out.println("DefaultConvereter::doConvert image is null"); return 1; }
			setNames(image);
			if (image!=null) image.flush(); image=null;
		}else {
			for (Enumeration e = hRes.getLayersHash().keys(); e.hasMoreElements();) {
				Integer layerNum = (Integer)e.nextElement();
				ImagePlus image =  getImage(layerNum);
				if (image==null) return 1;
				String oldOri = options.getOrientation();
				options.setOrientation("Transverse"); setNames(image);
				image =  getImage(layerNum);
				options.setOrientation("Coronal"); setNames(image);
				image =  getImage(layerNum);
				options.setOrientation("Sagittal"); setNames(image);
				options.setOrientation(oldOri);
				if (image!=null) image.flush(); image=null;
			}
		}
		System.gc();
		return status;
	}

	

	private ImagePlus openBaseFile(PlexiImageFile pf) {
	   PlexiFileOpener pfo = new PlexiFileOpener(viewableItem.getHiRes().getFormat(),pf);
       ImagePlus image = null;
       image = pfo.getImagePlus();   
	   if (image==null) {
		   System.out.println("Image Converter....couldnt find the  Image File");
		   return null;
	   }
	   baseOrientation = pfo.getOrientation();	
	   System.out.println("The in ori is " + baseOrientation);
	   if (viewableItem.getHiRes().getFormat().equalsIgnoreCase("IFH")) {
		   image=new plexiViewerImageRelayer(baseOrientation).Format4dfpImageForImageJ(image);
		   baseOrientation = pfo.getOrientation()+"F";
	   }	
	   return image;		
	}
	
	private int doPerform(ImagePlus img) {
		try {
			if (lRes!=null) {
				if (lRes.getCropDetails().deleteSlices()) {
					img = TransformationTools.dropSlices(img, lRes.getCropDetails().getStartSlice(),lRes.getCropDetails().getEndSlice());
				}
				if (lRes.getCropDetails().crop()) {
					img = TransformationTools.crop(img, lRes.getCropDetails().getBoundingRectangle());
				}
			}
            AffineTransformer affineTransformer = new AffineTransformer();
            img = affineTransformer.transform(img);
            
			PlexiImageOrientor pio=new PlexiImageOrientor(img,viewableItem.getHiRes().getFormat());
			ImagePlus img1 = pio.getImage(baseOrientation,options.getOrientation()+"F");
			PlexiIntensitySetter piSetter = new PlexiIntensitySetter(hRes);
			piSetter.setIntensities(img1,true);

			if (!FileUtils.dirExists(toPath)) {
				FileUtils.createDirectory(toPath);	  
			}
			new FileSaver(img1).saveAsRawStack(toPath +File.separator+toFileName);
			img1.setFileInfo(ImageUtils.getFileInfo(img1.getCalibration(), img1.getWidth(), img1.getHeight(), img1.getStackSize()));
			//new IFHWriter().writeHeader(toPath+File.separator+toFileName,options.getOrientation(),"4",img1.getCalibration(),img1.getWidth(), img1.getHeight(), img1.getStackSize());
			new AnalyzeWriter().save(img1,toPath,toFileName,options.getOrientation()+"F");
			pio.clearImage();
			img1.flush();
			img1=null;
			if (img!=null) 
				img.flush();
			img=null;
			System.out.println(this.getClass().getName() + " Created file " + toPath +File.separator + toFileName);
			return 0;			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			return -1;
		}
		
	}
	
	public PlexiImageFile getFileLocationAndName() throws URISyntaxException {
		PlexiImageFile pf = new PlexiImageFile();
		pf.setName(toFileName);
		pf.setPath(toPath);
		pf.setCachePath(cachePath);
		return pf;
	}
	
	
	public int convertAndSave(UserSelection u) {
		this.options = u;
		return convert();				
	}
}
