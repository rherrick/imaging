/*
 * org.nrg.plexiViewer.io.HiResLoader
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.io;

import ij.*;
import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.manager.*;
import org.nrg.plexiviewer.lite.display.*;
import org.nrg.plexiviewer.lite.xml.*;
import org.nrg.plexiviewer.exceptions.*;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.Transform.ReOrientMakeMontage;
import org.nrg.plexiviewer.utils.Transform.PlexiImageOrientor;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.converter.ConverterUtils;
import java.io.*;
public class HiResLoader implements ImageLoaderI{
		private UserSelection userOptions;
		MontageView mView;
		Layout layout;
		MontageDisplay mDisplay;
		PlexiImageFile openedImageFile;
		
		public HiResLoader(UserSelection opt) {
			userOptions = opt;
		}
	
		public ImagePlus load()  throws  InvalidParameterValueException {
			ImagePlus image = null;
			ViewableItem vi = PlexiSpecDocReader.GetInstance().getSpecDoc(userOptions.getProject()).getViewableItem(userOptions.getDataType());
			mView = vi.getHiRes().getMontageView();
			layout = vi.getHiRes().getLayout();
			String format=vi.getHiRes().getFormat();
			mDisplay = new MontageDisplay(mView, userOptions.getOrientation().toUpperCase(), layout);
			PlexiImageFile hpf = userOptions.getFile();
			if (hpf==null)
				hpf = FileUtils.getHiResFilePath(userOptions.getSessionId(),userOptions.getProject(), userOptions.getDataType(),userOptions.getHiResLayerNum(), userOptions.getScanNo());
			System.out.println("HIResLoader file details are " + hpf.toString()); 	
			PlexiImageFile pf = new PlexiImageFile();
			if (format.equalsIgnoreCase("DICOM")) {
				pf=hpf;		
				System.out.println("The MontageView is " + mView);		
				PlexiFileOpener pfo = new PlexiFileOpener(format,pf);
				image = pfo.getImagePlus();
				PlexiImageOrientor pio=new PlexiImageOrientor(image,"DICOM");
				image = pio.getImage(pfo.getOrientation(),userOptions.getOrientation());
				ReOrientMakeMontage rm = new ReOrientMakeMontage(userOptions,format,getMontageDisplay());
				image = rm.doPerform(image);
			}else {
				pf.setPath(hpf.getCachePath());
				pf.setName(FileUtils.getLoResFileName(hpf.getName(),"HiRes", userOptions.getOrientation()));
				pf =  FileUtils.fileExists(pf);
				if (pf==null) {
					System.out.println("File doesnt exists " );
					//PlexiPublisher publisher = PlexiStatusPublisherManager.GetInstance().getPublisher(userOptions.toString());
					//if (publisher!=null)
					//	publisher.setValue("Building required Image....Please wait");
					userOptions.setFile(hpf);
					pf = ConverterUtils.convert(userOptions);
					if (pf==null) {
						System.out.println("Couldnt launch the conveter");	
						return null;
					}
				}
				System.out.println("Looking out for the Hi Res Oriented file " + pf.getPath()+ File.separator + pf.getName());
				if (format.equalsIgnoreCase("IMA"))
					format="IFH"; 
				PlexiFileOpener pfo = new PlexiFileOpener(format,pf);
				image = pfo.getImagePlus();
				ReOrientMakeMontage rm = new ReOrientMakeMontage(userOptions,format,getMontageDisplay());
				image = rm.doPerform(image);
			}
			openedImageFile = pf;
			return image;
		}
	
		public PlexiImageFile getPlexiImageFile() {
			return openedImageFile;
		}
	
		public MontageDisplay getMontageDisplay() {
			return mDisplay;
		}
}
