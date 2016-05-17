//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.converter;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.process.StackConverter;
import org.nrg.plexiviewer.Writer.AnalyzeWriter;
import org.nrg.plexiviewer.io.PlexiFileOpener;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.xml.HiRes;
import org.nrg.plexiviewer.lite.xml.LoRes;
import org.nrg.plexiviewer.lite.xml.ViewableItem;
import org.nrg.plexiviewer.manager.PlexiSpecDocReader;
import org.nrg.plexiviewer.manager.PlexiStatusPublisherManager;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.PlexiPublisher;
import org.nrg.plexiviewer.utils.Transform.PlexiImageOrientor;
import org.nrg.plexiviewer.utils.Transform.PlexiIntensitySetter;
import org.nrg.plexiviewer.utils.Transform.TransformationTools;
import org.nrg.plexiviewer.utils.Transform.plexiViewerImageRelayer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;

public class CNL4dfp8BITConverter implements plexiLoResConverterI {
	HiRes hRes;
	LoRes lRes=null;
	UserSelection options;
	FileInfo fi;
	String fromFileName, toFileName=null, fromPath, toPath=null, cachePath;
	String baseOrientation;
	double minIntensity, maxIntensity;
	boolean single=false;
	ViewableItem viewableItem ;
	

	public CNL4dfp8BITConverter(UserSelection options) {
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
			ImagePlus image=null;
			if (viewableItem.getHiRes().getFormat().equalsIgnoreCase("IMA")) {
				String tmp = fromFileName+".4dfp.img";
				toPath = pf.getCachePath();
				if (!FileUtils.fileExists(toPath, tmp)) {
					image=buildImaImage(pf);
				}
			}else {
				image = openBaseFile(pf);
			}
			if (image==null) {
				System.out.println("Couldnt create HiRes image...Exiting\n");
				return null;
			}
			return image;
	}	

	private ImagePlus buildImaImage(PlexiImageFile pf) {
			ImagePlus image=null;
			String loResPath = PlexiSpecDocReader.GetInstance().getSpecDoc(options.getProject()).getLoResArchiveLocation(options.getSessionId()); 
			String launchProcess = "arc-build-RawMprage -d " + fromPath + " -f " +fromFileName + " -o " + cachePath ;
			System.out.println("Attempting to launch script..." +launchProcess);

			fromPath = cachePath;
			int rtnValue=1;
			try {
				Process p = Runtime.getRuntime().exec(launchProcess);
				rtnValue=p.waitFor();
			}catch (IOException ioe) {
				ioe.printStackTrace();
			}catch(InterruptedException ie) {
				ie.printStackTrace();
			}  
			System.out.println("Return value from process is " + rtnValue);
			if (rtnValue!=0) {
				System.out.println("Unable to launch script...quitting");
				return null;
			}
			fromFileName+=".4dfp.img";
            PlexiImageFile pfCopy = (PlexiImageFile)pf.clone();
            pfCopy.setPath(fromPath); pfCopy.setName(fromFileName);
			image = openBaseFile("IFH",false,pfCopy);
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
			if (viewableItem.getHiRes().getFormat().equalsIgnoreCase("IMA")) {
				String tmp = fromFileName+".4dfp.img";
				toPath = pf.getCachePath();
				if (!FileUtils.fileExists(toPath, tmp)) {
					image=buildImaImage(pf);
				}
			}else {
				image = openBaseFile(pf);
			}
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

	private void setNamesAndCreate8Bit(ImagePlus image) {
		setToFileName();
		if (toPath==null) toPath = PlexiSpecDocReader.GetInstance().getSpecDoc(options.getProject()).getLoResArchiveLocation(options.getSessionId());
		if (!FileUtils.fileExists(toPath, toFileName) || options.canOverwrite()){
			ImagePlus adjust = new ImagePlus();
			adjust.setCalibration(image.getCalibration());
			adjust.setStack("",image.getStack());
            adjust.setFileInfo(image.getOriginalFileInfo());
			doPerform(adjust);
			image.flush(); image=null;
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
			if (image==null) return 1;
			setNamesAndCreate8Bit(image);
			if (image!=null)image.flush(); image=null;
		}else {
			for (Enumeration e = hRes.getLayersHash().keys(); e.hasMoreElements();) {
				Integer layerNum = (Integer)e.nextElement();
				ImagePlus image =  getImage(layerNum);
				if (image==null) return 1;
				String oldOri = options.getOrientation();
				options.setOrientation("Transverse"); setNamesAndCreate8Bit(image);
				image =  getImage(layerNum);
				options.setOrientation("Coronal"); setNamesAndCreate8Bit(image);
				image =  getImage(layerNum);
				options.setOrientation("Sagittal"); setNamesAndCreate8Bit(image);
				options.setOrientation(oldOri);
			}
		}
		System.gc();
		return status;
	}

	private ImagePlus openBaseFile(String format, boolean convert,PlexiImageFile pf) {
	   PlexiFileOpener pfo = new PlexiFileOpener(format,pf);
	   ImagePlus image = pfo.getImagePlus();
       
	   if (image==null) {
		   System.out.println("Image Converter....couldnt find the  Image File");
		   return null;
	   }
	   if (convert) {
		if (pfo.getFileInfo().fileType!=FileInfo.GRAY8) {
		   //Convert to 8 bit and send
		   new StackConverter(image).convertToGray8();
		}
	   }
	   baseOrientation = pfo.getOrientation();	
	   if (format.equalsIgnoreCase("IFH")) {
			image=new plexiViewerImageRelayer(baseOrientation).Format4dfpImageForImageJ(image);
			baseOrientation = pfo.getOrientation()+"F";
	   }
	   return image;		
	}

	private ImagePlus openBaseFile(PlexiImageFile pf) {
       System.out.println("\n\n CONVERTER Want to get the file " + pf); 
	   PlexiFileOpener pfo = new PlexiFileOpener(viewableItem.getHiRes().getFormat(),pf);
       ImagePlus image = null;
       image = pfo.getImagePlus();   

       
	   if (image==null) {
		   System.out.println("Image Converter....couldnt find the  Image File");
		   return null;
	   }
	   if (pfo.getFileInfo().fileType!=FileInfo.GRAY8) {
		   //Convert to 8 bit and send
		   new StackConverter(image).convertToGray8();
	   }
	   baseOrientation = pfo.getOrientation();	
	   if (viewableItem.getHiRes().getFormat().equalsIgnoreCase("IFH") || viewableItem.getHiRes().getFormat().equalsIgnoreCase("DICOM")) {
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
            //AffineTransformer affineTransformer = new AffineTransformer();
            //img = affineTransformer.transform(img);

			PlexiImageOrientor pio=new PlexiImageOrientor(img,viewableItem.getHiRes().getFormat());
			img = pio.getImage(baseOrientation,options.getOrientation()+"F");
			PlexiIntensitySetter piSetter = new PlexiIntensitySetter(hRes);
			piSetter.setIntensities(img,true);
			
			if (!FileUtils.dirExists(toPath)) {
				FileUtils.createDirectory(toPath);	  
			}
			PlexiPublisher publisher = PlexiStatusPublisherManager.GetInstance().getPublisher(options.toString());
			if (publisher!=null)
				publisher.setValue("Delivering requested Image");
			new FileSaver(img).saveAsRawStack(toPath +File.separator+toFileName);
			img.setFileInfo(getFileInfo(img.getCalibration(), img.getWidth(), img.getHeight(), img.getStackSize()));
			new AnalyzeWriter().save(img,toPath,toFileName,options.getOrientation()+"F");
			pio.clearImage();
			if (img!=null)img.flush();
			img=null;
			System.out.println(this.getClass().getName() + " Created file " + toPath +File.separator + toFileName);
			return 0;			
		}catch(Exception e) {
            e.printStackTrace();
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
