//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Reader;

import ij.io.FileInfo;

import java.io.IOException;

import org.nrg.nifti.NiftiReader;
import org.nrg.nrrd.NRRDReader;

public class PlexiImageHeaderReader {

		String type;
		String orientation;
		ImageReader imgReader=null;

		public PlexiImageHeaderReader(String type) {
			this.type = type;
		}
		
		public FileInfo getFileInfo(String directory, String fileName) throws IOException{
			if (type.startsWith("ANALYZE")) {
				imgReader = new AnalyzeReader(directory, fileName);
			}else if (type.equalsIgnoreCase("IFH"))	{
				imgReader = new IFHReader(directory, fileName);
			}else if (type.equalsIgnoreCase("DICOM")) {
				imgReader = new DICOMReader(directory, fileName);
			}else if  (type.equalsIgnoreCase("GIF")) {
				imgReader = new GIFReader(directory,fileName);	
			}else if  (type.equalsIgnoreCase("NRRD")) {
				imgReader = new NRRDReader(directory,fileName);	
			}else if  (type.equalsIgnoreCase("NIFTI")) {
				imgReader = new NiftiReader(directory,fileName);	
			}else {
				System.out.println("Do not know how to read image file type " + type +"\n");
				return null;
			}
			return imgReader.getFileInfo();
		}

		 public void clearTempFolder() {
			 imgReader.clearTempFolder();
		 }
		/**
		 * @return
		 */
		public String getOrientation() {
			orientation = null;
			if (imgReader!=null)
				orientation = imgReader.getOrientation();
			else {
				System.out.println("PlexiImageHeaderReader::ImgReader is blank");	
			}
			//System.out.println("PlexiImageHeaderReader orientation " + orientation);
			return orientation;
		}
		
        public int getVolumes() {
            int rtn = 0;
            if (imgReader!=null) {
                return imgReader.getVolumes();
            }
            return rtn;
        }
        
		public int  getOrientationForWriter() {
			int orientation = -1;
			if (imgReader!=null)
				orientation = imgReader.getOrientationForWriter();
			else {
				System.out.println("PlexiImageHeaderReader::ImgReader is blank");	
			}
			//System.out.println("PlexiImageHeaderReader orientation " + orientation);
			return orientation;
		}
		
		public boolean isGIF_JPG_Others() {
			boolean rtn=false;
			if (type.equalsIgnoreCase("GIF"))
				rtn = true;
			return rtn;	
		}
}
