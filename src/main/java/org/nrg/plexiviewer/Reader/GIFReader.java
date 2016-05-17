//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Reader;

import ij.io.*;
import java.io.*;

import org.nrg.plexiviewer.utils.FileUtils;
public class GIFReader extends ImageReaderPreparer implements ImageReader {
	String directory, fileName;
    
	public GIFReader(String directory, String fileName) {
			this.directory = directory;
			this.fileName = fileName;
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
		FileInfo fi = null;
		String path="";
		
			String name="";
			if (fileName.endsWith(".gif")) name = fileName.substring(0, fileName.length()-4 );
			else if  (fileName.endsWith(".GIF")) name = fileName.substring(0, fileName.length()-4 );
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
				System.out.println("GIFReader:: Directory string is empty or null\n");
				throw new IOException();
			}
			fi = new FileInfo();
			fi.fileFormat= fi.GIF_OR_JPG;
			fi.fileName = name + ".gif";
			fi.directory = directory;
			return fi;
	}
		public String getOrientation()  {
			return null;
		}	
		public int getOrientationForWriter() {
			return -1;
		}	
        
        public int getVolumes() {
            return 0;
        }
        
}
