/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.apps;

import java.util.ArrayList;
import java.util.Iterator;

import org.nrg.plexiviewer.converter.NiftiAsAnalyzeConverter;
import org.nrg.plexiviewer.utils.FileUtils;

public class ExplicitPlexiConverter extends PlexiConverter {

    public ExplicitPlexiConverter(String args[]) {
        super(args);
    }
    
    void parseFileAndCallConverter(String sessionFile) {
        ArrayList list = FileUtils.parseFile(sessionListFile);
        Iterator iter =list.iterator();
        while (iter.hasNext()) {
            String lineInFile = (String)iter.next();
            String[] lineCols = lineInFile.split("#");
            String fromPath = lineCols[0];
            String fromFile = lineCols[1];
            String toPath = lineCols[2];
            String format = lineCols[3];
            String outOrientation = lineCols[4];
            int sliceNo = Integer.parseInt(lineCols[5]);
            String inOrientation = lineCols[6];
            NiftiAsAnalyzeConverter converter = new NiftiAsAnalyzeConverter(fromPath, fromFile,toPath, inOrientation);
            converter.setFormat(format);
            if (minIntensity!=-1 && maxIntensity != -1) {
                converter.setMinIntensity(minIntensity);
                converter.setMaxIntensity(maxIntensity);
            }
            converter.createThumbnail(outOrientation,sliceNo);
        }
    }
    
      String getUsage() {
          String usage = "Usage: ExplicitPlexiConverter -file <path to file containing session ids> \n";
          usage += "-minIntensity <min intensity> [used only with -file]\n";
          usage += "-maxIntensity <max intensity> [used only with -file]\n";
          usage += "The file passed should have # separated information\n";
          usage += "<path to file>#<name of input file>#<path to write to>#<Image Format>#<orientation>#<slice no>#<Image Orientation>\n";
          return usage;
        }
    
    public static void main(String args[]) {
        try {
            int  status = new ExplicitPlexiConverter(args).create();
            System.out.println("All done");
            System.exit(status);
        }catch(Exception e ) {
            e.printStackTrace();
        }
    }
    
    
}
