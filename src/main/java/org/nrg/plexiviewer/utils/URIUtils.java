/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import edu.sdsc.grid.io.srb.SRBFile;

public class URIUtils {
    String path=null, name=null;
    URI uri;
    private boolean isSrb = false;
    
    
    
    public URIUtils(URI u) {
        uri = u; init();
    }

    public URIUtils(String u) {
    	String u1= u;
        try {
        	u1 = u.replace("\\", "/");
            uri = new URI(u1); init();
        }catch(URISyntaxException urie) {System.out.println("URIUtils::constructor invalid uri " + u1);uri = null;}
    }

    private void init() {
        try {
            if (uri != null) set();
        }catch(IOException ioe) {System.out.println("URIUtils::init " + uri.toString());}
    }
    
    public boolean isRemote() {
        return isSrb;
    }
    
    public String getPath() throws IOException{
        if (path == null) 
            set();
        return path;
    }
    
    public String getName() throws IOException{
        if (name == null) 
            set();
        return name;
    }
    
    public static URI getURI(String rootStr) throws URISyntaxException{
        //System.out.println("String " + root);
    	String root = rootStr.replace("\\", "/");
        URI base = null;
        if (!root.startsWith("srb:")) {
            File f = null;
            if (root.startsWith("file:")) {
               f = new File(new URI(root)); 
            }else {
              f = new File(root);
            }
            base = f.toURI();
        }else 
            base = new URI(root);
        //System.out.println("URIUTILS Returning base " + base);
        return base;
    }
    
    public static String stripScheme(String uri) {
        String rtn = uri;
        try {
            URI u = new URI(uri);
            rtn = u.getPath();
            if (rtn.startsWith("/")) rtn = rtn.substring(1);
        }catch(Exception e){
            
        }
        return rtn;
    }
    
    private void set() throws IOException {
        if (uri.getScheme() != null && uri.getScheme().startsWith("file")) {
            File f = new File(uri);
            name = f.getName();
            path = f.getPath();
            int purePathIndex = path.indexOf(name);
            if (purePathIndex != -1 && purePathIndex > 1 ) {
                path = path.substring(0,purePathIndex-1);
            }
        }else if (uri.getScheme() != null && uri.getScheme().startsWith("srb")) {
            isSrb = true;
            path = uri.getPath();
            int nI =  path.lastIndexOf(SRBFile.PATH_SEPARATOR);
            if (nI != -1 && nI < path.length()) {
               name = path.substring(nI+1);
               path= path.substring(0,nI);
            }
        }
    }
    
    
    public static void main(String args[]) {
        String uri = "srb://dmarcus@wustl-nrg-gpop.nbirn.net/home/dmarcus.wustl-nrg/oasis/set1/disc1/OAS1_0001_MR1/PROCESSED/MPRAGE/T88_111/OAS1_0001_MR1_mpr_n4_anon_111_t88_gfc.img";
        try {
            URIUtils u = new URIUtils(uri);
            System.out.println("Is Remote " + u.isRemote());
        }catch(Exception e) {}
        
    }
    
}
