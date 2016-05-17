/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.utils.Transform;

import java.io.IOException;

import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.PlexiConstants;

public class ImaTo4dfpBuilder {
    
    PlexiImageFile pf; 
  //  String filename;
    
    public ImaTo4dfpBuilder(PlexiImageFile pf) { //, String fname) {
        this.pf = pf;
 /*       this.filename = fname;
        if (filename.endsWith(".img")) {
        	int i = filename.lastIndexOf(".img");
        	if (i !=-1) {
        		filename = filename.substring(0,i);
        	}
        }
 */   }
    
    public boolean create() {
        boolean success = true;
        if (pf.getFormat() != null && pf.getFormat().equalsIgnoreCase("IMA")) {
            if (!exists4dfpVersionForIma()) 
                success = buildImaImage();
            pf.setPath(pf.getCachePath());
 //pf.setName(filename+".4dfp.img");
  pf.setName(pf.getName()+".4dfp.img");
            pf.setXsiType(PlexiConstants.PLEXI_IMAGERESOURCE);
            pf.setFormat("IFH");
            try {
                if (pf.getPath().endsWith("/"))
                    pf.setURIAsString(pf.getPath() + pf.getName() );
                 else
                     pf.setURIAsString(pf.getPath() + "/" + pf.getName());
            }catch(Exception e) {
                System.out.println("URI Exception " + pf.getPath() + " " + pf.getName());
            }
        }
        return success;
    }
    
    private boolean exists4dfpVersionForIma() {
        return FileUtils.fileExists(pf.getCachePath(), pf.getName()+".4dfp.img" ); 
    }
    
    private boolean buildImaImage() {
        String cachePath = pf.getCachePath();
        if (cachePath.startsWith("file:"))
            cachePath = cachePath.substring(5);
//        String launchProcess = "arc-build-RawMprage -d " + pf.getPath() + " -f " + pf.getName() + " -o " + cachePath +  "  -w " + filename;
        String launchProcess = "arc-build-RawMprage -d " + pf.getPath() + " -f " + pf.getName() + " -o " + cachePath ;
        
        System.out.println("Attempting to launch script..." +launchProcess);
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
            System.out.println("Imato4dfpBuilder::Unable to build Ima launch script..." + launchProcess );
        }
        return ((rtnValue == 0)?true:false);
    }
}
