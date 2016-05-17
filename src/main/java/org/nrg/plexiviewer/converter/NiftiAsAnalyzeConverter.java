/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.converter;

import ij.ImagePlus;

import org.nrg.plexiviewer.io.PlexiFileOpener;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;

public class NiftiAsAnalyzeConverter extends NonXnatConverter{
    
    public NiftiAsAnalyzeConverter(String fPath, String fFileName, String tPath,String inOrientation) {
        super(fPath,fFileName,tPath);
        baseOrientation = inOrientation;  
    }
    
    protected ImagePlus openBaseFile(PlexiImageFile pf) {
        if (format == null) {
            System.out.println("Missing format");
            return null;
        }
        PlexiFileOpener pfo = new PlexiFileOpener(format,pf);
        ImagePlus image = null;
        image = pfo.getImagePlus();   
               
        if (image==null) {
            System.out.println("Image Converter....couldnt find the  Image File");
            return null;
        }
        return image;        
   }


}
