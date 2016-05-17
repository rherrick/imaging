/*
 * org.nrg.plexiViewer.io.LoResLoader
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
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.display.MontageDisplay;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.utils.ImageDetails;
import org.nrg.plexiviewer.lite.xml.Layout;
import org.nrg.plexiviewer.lite.xml.MontageView;
import org.nrg.plexiviewer.utils.*;
import org.nrg.plexiviewer.utils.Transform.ReOrientMakeMontage;

import java.io.File;
import java.util.Hashtable;

public class LoResLoader implements ImageLoaderI {

	private UserSelection userOptions;
	private MontageView mView;
	private Layout layout;
	private MontageDisplay mDisplay;
	private PlexiImageFile openedImageFile;

	/**
	 * Constructor
	 * @param opt: Options for which the lo-res image is to be loaded
	 */
	public LoResLoader(UserSelection opt) {
		userOptions = opt;
	}
	
	public ImagePlus load()  throws  Exception {
		ImagePlus image = null;
		PlexiImageFile pf =		userOptions.getFile();
				layout = ImageDetails.getLayout(pf.getDimX(), pf.getDimY(), pf.getDimZ(), pf.getVoxelResX(), pf.getVoxelResY(), pf.getVoxelResZ(), pf.getFormat());
				String loresLocation = ArchivePathManager.GetInstance().getLoResLocation(userOptions.getProject(),userOptions.getSessionLabel());
				String loresType = ImageDetails.getDefaultLoresType();
                
				pf.setPath(URIUtils.getURI(loresLocation).toString());
				if (pf.getFormat()!=null && pf.getFormat().equals("IMA")) {
					pf.setName(FileUtils.getLoResFileName(pf.getName()+".4dfp.img",loresType, userOptions.getOrientation()));
					
				}else

				pf.setName(FileUtils.getLoResFileName(pf.getName(),loresType, userOptions.getOrientation()));
	            if (pf.getPath().endsWith("/"))
	                pf.setURIAsString(pf.getPath() + pf.getName());
	            else
	                pf.setURIAsString(pf.getPath() + "/" + pf.getName());
				pf.setCachePath(pf.getCachePath());
	            pf.setXsiType(PlexiConstants.PLEXI_IMAGERESOURCE);
				String format=ImageDetails.getDefaultLoresFormat();
				pf.setFormat(format);

				
				System.out.println("Looking out for the Lo Res file " + pf.getPath()+ File.separator + pf.getName());

				PlexiFileOpener pfo = new PlexiFileOpener(pf.getFormat(),pf); 
				image = pfo.getImagePlus();
                if (mView == null) {
                    mView = new MontageView();
                    mView.setScale(MontageView.SCALE);
                    Hashtable attribs = ImageUtils.getSliceIncrement(pf.getDimZ());
                    mView.addViewInfo(userOptions.getOrientation().toUpperCase(),((Integer)attribs.get("startslice")).intValue(),((Integer)attribs.get("endslice")).intValue(),((Integer)attribs.get("increment")).intValue());
                    mDisplay = new MontageDisplay(mView, userOptions.getOrientation().toUpperCase(), layout);
                }

                
                mDisplay = new MontageDisplay(mView, userOptions.getOrientation().toUpperCase(), layout);
				ReOrientMakeMontage rm = new ReOrientMakeMontage(userOptions,pf.getFormat(),getMontageDisplay());
				image = rm.doPerform(image);
				openedImageFile = pf;
				System.out.println("LoResLoader openedImageFIle " + getPlexiImageFile().toString());
                pf.flush();
                openedImageFile.flush();
                //pf = null;
                //openedImageFile = null;
		return image;
	}

	public PlexiImageFile getPlexiImageFile() {
		return openedImageFile;
	}

	/**
	 * @return
	 */
	public MontageView getMontageView() {
		return mView;
	}

	public Layout getLayout() {
		return layout;
	}
	
	public MontageDisplay getMontageDisplay() {
		return mDisplay;
	}
}
