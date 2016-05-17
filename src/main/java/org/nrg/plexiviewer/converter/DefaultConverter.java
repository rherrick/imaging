//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.converter;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.io.FileSaver;
import ij.process.ImageProcessor;
import org.nrg.plexiviewer.Writer.AnalyzeWriter;
import org.nrg.plexiviewer.io.IOHelper;
import org.nrg.plexiviewer.io.PlexiFileOpener;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.utils.LiteFileUtils;
import org.nrg.plexiviewer.utils.ArchivePathManager;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.ImageUtils;
import org.nrg.plexiviewer.utils.PlexiConstants;
import org.nrg.plexiviewer.utils.Transform.*;

import java.io.File;

public class DefaultConverter implements plexiLoResConverterI {
	UserSelection options;
	FileInfo fi;
	String fromFileName, toFileName=null, fromPath, toPath=null, cachePath;
	String baseOrientation;
	double minIntensity, maxIntensity;
	boolean single=false;
	String cachePathLocation;
	String loResArchiveLocation;
	final String IMAGE_FORMAT = "ANALYZE"; 

	public DefaultConverter(UserSelection options) {
		this.options = options;
        cachePathLocation = ArchivePathManager.GetInstance().getCachePathLocation(options.getProject(), options.getSessionLabel());
        loResArchiveLocation = ArchivePathManager.GetInstance().getLoResLocation(options.getProject(), options.getSessionLabel());
	}
		
	public int convert() {
		if (options.getHiResLayerNum()>=0 && options.getOrientation()!=null) {
			single=true;
		}else if (options.hasFile() && options.getOrientation()!=null)
			single=true;
		System.out.println("Create for a single file: " + single);
		int rtn = 0;
		try {
			rtn= doConvert();
		}catch (Exception e){e.printStackTrace();rtn =1;}
		return rtn;
	}	

	protected ImagePlus getImage(Integer layerNum) throws Exception {
			String sessionId = options.getSessionId();
            PlexiImageFile pf = null;
	    	if (options.hasFile()) { 
				pf = options.getFile();
			}else if (options.hasXnatFile()) {
				pf = IOHelper.getPlexiImageFile(options.getXnatFile(), cachePathLocation, options.getFormattedWindowTitle() + "_" + LiteFileUtils.getFileName(options.getXnatFile()));
			}
			fromPath = pf.getPath();
			fromFileName = pf.getName(); 
			System.out.println("From File Name is " + fromFileName);
			cachePath = pf.getCachePath();
			ImagePlus image=null;
			if (pf.getFormat().equalsIgnoreCase("IMA")) {
				System.out.println(" Will look at cachepath " + pf.getCachePath());
                ImaTo4dfpBuilder imato4dfp = new ImaTo4dfpBuilder(pf);
                imato4dfp.create();
                image = openBaseFile("IFH", pf);
			}else {
				image = openBaseFile(pf);
			}
			if (image==null) {
				System.out.println("Couldnt create HiRes image...Exiting\n");
				return null;
			}
			return image;
	}	




	protected ImagePlus getImage() throws Exception {
			String sessionId = options.getSessionId();
			PlexiImageFile pf = null;
 	    	if (options.hasFile()) { 
				pf = options.getFile();
			}else if (options.hasXnatFile()) {
				pf = IOHelper.getPlexiImageFile(options.getXnatFile(), cachePathLocation,options.getFormattedWindowTitle() + "_" + LiteFileUtils.getFileName(options.getXnatFile()));
			}

			fromPath = pf.getPath();
			
			cachePath = pf.getCachePath(); 
			ImagePlus image=null;
            System.out.println("DefaultConveter::getImage " + pf.getFormat() + "  " +  fromFileName);
			if (pf.getFormat().equalsIgnoreCase("IMA")) {
                pf.setFormat("IMA");
			    ImaTo4dfpBuilder imato4dfp = new ImaTo4dfpBuilder(pf);
                imato4dfp.create();
            
                image = openBaseFile("IFH", pf);
			}else {
				image = openBaseFile(pf);
			}
			fromFileName = pf.getName();
			if (image==null) {
				System.out.println("Couldnt create HiRes image...Exiting\n");
				return null;
			}
			return image;
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
	   if (toPath==null) toPath = loResArchiveLocation;
       System.out.println("DefaultConverter::setNames ToPath " + toPath + " " + toFileName);
       boolean exists = FileUtils.fileExists(toPath, toFileName); 
		if (!exists || options.canOverwrite()){
            ImagePlus adjust = new ImagePlus();
			try {
                adjust.setCalibration(image.getCalibration());
                adjust.setFileInfo(image.getOriginalFileInfo());
				if (image.getStackSize() > 1 ) {
					adjust.setStack("",image.getStack());
				}else 
	     			adjust.setProcessor(image.getTitle(), image.getProcessor());
                doPerform(adjust);
            }catch(Exception e) {e.printStackTrace();}
            finally {   
                image.flush();image=null;
                adjust.flush(); adjust=null;
            }
		}else {
			System.out.println("File already exists" + toPath + "/" + toFileName);
		}
	}
	
	private int doConvert() throws Exception {		
		String sessionId = options.getSessionId();
		int status =0;
		if (single) {
			ImagePlus image =  getImage();
			if (image==null){System.out.println("DefaultConvereter::doConvert image is null"); return 1; }
			setNames(image);
			if (image!=null) image.flush(); image=null;
		}else {
				ImagePlus image =  getImage();
				if (image==null) return 1;
				String oldOri = options.getOrientation();
				options.setOrientation("Transverse"); setNames(image);
				image = getImage();
				options.setOrientation("Coronal"); setNames(image);
				image =getImage();
				options.setOrientation("Sagittal"); setNames(image);
				options.setOrientation(oldOri);
				if (image!=null) image.flush(); image=null;
		}
		System.gc();
		return status;
	}

	private ImagePlus openBaseFile(String format, PlexiImageFile pf) {
	   PlexiFileOpener pfo = new PlexiFileOpener(format,pf);
       ImagePlus image = null;
         image = pfo.getImagePlus();   
        // image.show();
       
	   if (image==null) {
		   System.out.println("Image Converter: couldnt find the  Image File");
		   return null;
	   }
	   baseOrientation = pfo.getOrientation();	
	   if (format.equalsIgnoreCase("IFH") ) {
			image=new plexiViewerImageRelayer(baseOrientation).Format4dfpImageForImageJ(image);
			baseOrientation = pfo.getOrientation()+"F";
	   }
	  // image.show();
       System.out.println("Default Converter:: File Opened");
	   return image;		
	}

	private ImagePlus openBaseFile(PlexiImageFile pf) {
	   return openBaseFile(pf.getFormat(), pf);		
	}
	

	private void resize (ImagePlus img1) {
        if (img1.getWidth() >=800 || img1.getHeight() >=800) {
        	double scale = 0.75;
        	int stackWidth = img1.getWidth();
            int stackHeight = img1.getHeight();
            int width = (int)(stackWidth*scale);
            int height = (int)(stackHeight*scale);;
            ImageStack stack = img1.getStack();
            ImageProcessor aSlice;
            int slice = 1;
            ImageStack stack1 = new ImageStack(width,height);
			if (scale!=1.0) {
	            while (slice<=img1.getStackSize()) {
	                aSlice = stack.getProcessor(slice);
	    			aSlice = aSlice.resize(width, height);
	                stack1.addSlice(""+slice, aSlice);
	    			slice++;   	
	            }
	            img1.setStack(img1.getTitle(), stack1);
	        }
            FileInfo fi = img1.getOriginalFileInfo();
            if (fi != null) {
            	fi.width = width;
            	fi.height = height;
            	img1.setFileInfo(fi);
            }
        }
	}
	
	private int doPerform(ImagePlus img) {
		try {
			/*if (lRes!=null) {
				if (lRes.getCropDetails().deleteSlices()) {
					img = TransformationTools.dropSlices(img, lRes.getCropDetails().getStartSlice(),lRes.getCropDetails().getEndSlice());
				}
				if (lRes.getCropDetails().crop()) {
					img = TransformationTools.crop(img, lRes.getCropDetails().getBoundingRectangle());
				}
			}*/
            
            //AffineTransformer affineTransformer = new AffineTransformer();
            //img = affineTransformer.transform(img);
			PlexiImageFile pf = null;
 	    	if (options.hasFile()) { 
				pf = options.getFile();
			}else if (options.hasXnatFile()) {
				pf = IOHelper.getPlexiImageFile(options.getXnatFile(), cachePathLocation,options.getFormattedWindowTitle() + "_"+ LiteFileUtils.getFileName(options.getXnatFile()));
			}

 	    	if (!options.getDataType().equals("RAW")) {
 	    		IntensitySetter piSetter = new IntensitySetter(img, true);
 	    		piSetter.autoAdjust(img, img.getProcessor());
 	    	}

 	    	
            PlexiImageOrientor pio=new PlexiImageOrientor(img,pf.getFormat());
            ImagePlus img1 = pio.getImage(baseOrientation,options.getOrientation()+"F");
 
 
            
			if (!FileUtils.dirExists(toPath)) {
            	FileUtils.createDirectory(toPath);	  
			}
            String strippedScheme = toPath;
            if (toPath.startsWith("file:")) {
                strippedScheme = toPath.substring(5);
            }
            if (!strippedScheme.endsWith(File.separator)) {
                strippedScheme += File.separator;
            }
            resize (img1);
//Generate an 8bit image
            BitConverter converter = new BitConverter();
            converter.convertToGrayscale(img1);


            boolean created = false;
            if (img1.getStackSize() == 1) 
            	created = new FileSaver(img1).saveAsRaw(strippedScheme +toFileName);
            else 
            	created =  new FileSaver(img1).saveAsRawStack(strippedScheme +toFileName);
            if (created) {
                img1.setFileInfo(ImageUtils.getFileInfo(img1.getCalibration(), img1.getWidth(), img1.getHeight(), img1.getStackSize()));
                //new IFHWriter().writeHeader(toPath+File.separator+toFileName,options.getOrientation(),"4",img1.getCalibration(),img1.getWidth(), img1.getHeight(), img1.getStackSize());
                new AnalyzeWriter().save(img1,strippedScheme,toFileName,options.getOrientation()+"F");
                System.out.println(this.getClass().getName() + " Created file " + toPath +File.separator + toFileName);
            }else {
                System.out.println("Couldnt create file " + toPath + File.separator + toFileName);
                pio.clearImage();
                img1.flush();
                img1=null;
                if (img!=null) 
                    img.flush();
                img=null;
                return -1;
            }
            pio.clearImage();
            img1.flush();
            img1=null;
            if (img!=null) 
                img.flush();
            img=null;
            return 0;			
		}catch(Exception e) {
			System.out.println(e.getMessage());
			return -1;
		}
		
	}
	
	public PlexiImageFile getFileLocationAndName() throws Exception {
		PlexiImageFile pf = new PlexiImageFile();
		pf.setName(toFileName);
		pf.setPath(toPath);
		pf.setFormat(this.IMAGE_FORMAT);
        try {
            if (pf.getPath().endsWith("/"))
                pf.setURIAsString(pf.getPath() + pf.getName());
            else
                pf.setURIAsString(pf.getPath() + "/" + pf.getName());
        }catch(Exception e) {System.out.println("DefaultConverter::getFileLocationAndName() couldnt set URI "+ pf.getPath() + File.separator + pf.getName()); }
        pf.setXsiType(PlexiConstants.PLEXI_IMAGERESOURCE);

		pf.setCachePath(cachePath);
		return pf;
	}
	
	public int convertAndSave(UserSelection u) {
		this.options = u;
		return convert();				
	}
}
