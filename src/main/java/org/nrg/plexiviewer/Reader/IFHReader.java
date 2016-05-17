//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Reader;

import ij.io.*;

import java.io.*;

import org.nrg.plexiviewer.utils.FileUtils;

public class IFHReader  extends ImageReaderPreparer implements ImageReader {
	String directory, fileName;
	private String orientation;
    int volumes = 1;
	
	public IFHReader(String directory, String fileName) {
		this.directory = directory;
		this.fileName = fileName;
		orientation = null;
	}
	
	  public boolean isZipped() {
	    	return zipped;
	    }
	  
	  public void clearTempFolder() {
	    	if (zipped) {
	    		FileUtils.deleteFile(directory, true);
	    	}
	    }
	  
	public FileInfo getFileInfo() throws IOException {
		//System.out.println("IFH Reader lanched...." + this.toString() +"\n");
		FileInfo fi = null;
		String path="";
		try {
		
			String name="";
			if (fileName.endsWith(".img")) name = fileName.substring(0, fileName.length()-4 ); 
			else if (fileName.endsWith(".ifh")) name = fileName.substring(0, fileName.length()-4 ); 
			else
				name = fileName;
			
			String destDir = unzip(directory, fileName);
			if (zipped) {
				directory = destDir;
			}
			
			if (directory!=null && !directory.equals("")) {
				if (!directory.endsWith(""+File.separatorChar)) { 
						directory += File.separatorChar;
				}		
			}	
			else {
				System.out.println("IFHReader:: Directory string is empty or null\n");
				return fi;
			}

			
			path += directory  + name + ".ifh";
			
			//System.out.println("PlexiFileOpener:: IFH File is " + path + "\n");	
			fi = new FileInfo();
			fi.unit = "pixels";
			fi.fileFormat = fi.RAW;
			FileScanner fscanner = new FileScanner(path); 
			fscanner.open();
			orientation = fscanner.getDelimitedString("orientation",":=");    
			if (fscanner.getDelimitedString("imagedata byte order",":=")!=null && fscanner.getDelimitedString("imagedata byte order",":=").equalsIgnoreCase("LITTLEENDIAN")) {
				fi.intelByteOrder = true;
			}else 
				fi.intelByteOrder = false;
			fi.width =   Integer.parseInt(fscanner.getDelimitedString("matrix size [1]",":="));
			fi.height =  Integer.parseInt(fscanner.getDelimitedString("matrix size [2]",":="));
			fi.nImages = Integer.parseInt(fscanner.getDelimitedString("matrix size [3]",":="));
            volumes = Integer.parseInt(fscanner.getDelimitedString("matrix size [4]",":="));
			fi.pixelWidth = Double.parseDouble(fscanner.getDelimitedString("scaling factor (mm/pixel) [1]",":="));	// pixdim[1]
			fi.pixelHeight = Double.parseDouble(fscanner.getDelimitedString("scaling factor (mm/pixel) [2]",":="));    // pixdim[2]
			fi.pixelDepth = Double.parseDouble(fscanner.getDelimitedString("scaling factor (mm/pixel) [3]",":=")); 	// pixdim[3]
			fi.unit="mm";
			String bPerPixel = fscanner.getDelimitedString("number of bytes per pixel",":=");
			if (bPerPixel!=null) {
				if (Integer.parseInt(bPerPixel) == 4)
					 fi.fileType = FileInfo.GRAY32_FLOAT;
				else if (Integer.parseInt(bPerPixel) == 1)
					 fi.fileType = FileInfo.GRAY8;
				else {
					 fi.fileType = FileInfo.GRAY8;
				 }
			}  
			fi.fileName = name + ".img";
			fi.directory = directory;
			fscanner.close();
			//System.out.println("IFH READER Finished reading the IFH file at " + path + " Orientation::" +orientation);
		}catch(FileNotFoundException fne) {
			System.out.println(fne.getMessage());
			System.out.println("Didnt find the file at path " + path + "\n");
			return null;
		}
		return fi;
	}
	
	public String toString() {
		return "Directory: " + directory + "\t Filename: " + fileName + " " ;
	}

    public int getVolumes() {
        return volumes;
    }
    
	/**
	 * @return
	 */
	public String getOrientation() {
		return ReaderUtils.getOrientationLabel(orientation);
	}
	public int getOrientationForWriter() {
			return ReaderUtils.getOrientationAsInt(getOrientation(),false);
	}
}
