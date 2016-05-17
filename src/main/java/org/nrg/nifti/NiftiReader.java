/*
 * org.nrg.nifti.NiftiReader
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */
package org.nrg.nifti;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;

import java.io.File;
import java.io.IOException;

import org.nrg.plexiviewer.Reader.ImageReader;
import org.nrg.plexiviewer.Reader.ReaderUtils;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.Transform.PlexiImageOrientor;

public class NiftiReader implements ImageReader{
	private boolean zipped = false;
	private String  name, directory;

	private boolean littleEndian = false;
	private boolean isNiftiData = false;
  
	private double cal_min = 0.0; 
	private double cal_max = 0.0; 

	private int nChannels = 1;
	private int depth = 1;
	private int frames = 1;
	private boolean complex;
	private FileInfo fi;
	
	public NiftiReader(String dir, String filename) {
		directory = dir; this.name = filename;
	}

	public NiftiReader(String filePath) {
		int i = filePath.lastIndexOf(File.separator);
		if (i != -1) {
			directory = filePath.substring(0, i);
			name = filePath.substring(i+1);
		}else {
			name = filePath;
			directory = "";
		}
	}

	public ImagePlus getImagePlus() {
		Nifti_Reader nifti_reader = new Nifti_Reader();
		ImagePlus img = nifti_reader.load(directory, name);
		nifti_reader.doPostLoadingActions(img);
		fi = img.getOriginalFileInfo();
		return img;
	}
	
	public FileInfo getFileInfo() {
		if (fi != null)
			return fi;
		else { 
			Nifti_Reader nifti_reader = new Nifti_Reader();
			String hdrName = name; 
			String imgName = name; 

			String suffix = "";
			if (name.endsWith(".gz") || name.endsWith(".GZ")) { 
				suffix = name.substring( name.length()-3 );
				name = name.substring(0, name.length()-3 );
			}
			
			if ((name.endsWith(".img")) || (name.endsWith(".hdr"))) { 
				name = name.substring(0, name.length()-4 ); 
				hdrName = name+".hdr"+suffix; 
				imgName = name+".img"+suffix;
			} else { 
				hdrName = name+suffix; 
				imgName = name+suffix; 
			}
			
			if (!directory.endsWith(File.separator) && !directory.equals("")) 
			 	directory += File.separator;
			try {
				fi = nifti_reader.readHeader( directory+hdrName );
				if (fi==null) return null; 	
			} catch (IOException e) { 
				IJ.log("FileLoader: "+ e.getMessage()); 
			}
			fi.fileName = imgName;
			fi.directory = directory;
			fi.fileFormat = FileInfo.RAW;
			return fi;
		}
	}

	
	  public boolean isZipped() {
	    	return zipped;
	    }


	 public void clearTempFolder() {
	    	if (zipped) {
	    		FileUtils.deleteFile(directory, true);
	    	}
	    }
	 
		public String getOrientation() {
			return PlexiImageOrientor.AS_ACQUIRED_TXT;
		}
		
		public int getOrientationForWriter() {
			return ReaderUtils.getOrientationAsInt(getOrientation(),false);
	   }
		
		 public int getVolumes() {
			 if (fi != null) {
				 return fi.nImages;
			 }else return 1;
		 }

		 
		 public static void main(String args[]) {
			 String dir = args[0];
			 String name = args[1];
			 NiftiReader nr = new NiftiReader(dir,name);
			 ImagePlus img = nr.getImagePlus();
			 img.show();
		 }
}
