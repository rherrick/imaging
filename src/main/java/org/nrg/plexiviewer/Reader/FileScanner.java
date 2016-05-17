//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Reader;

import java.io.*;

public class FileScanner extends java.lang.Object {
    String path;
    BufferedReader reader;
    int nBytes;
    static int scanLimit = 10000; //this seems big enough for now.
    
    /** Creates new Scanner */
    public FileScanner(String path) {
        this.path = path;
        nBytes = 0;
    }
    
    public void open() throws IOException {
		FileInputStream filein = new FileInputStream (path);
		DataInputStream input = new DataInputStream (filein);
        InputStreamReader streamReader = new InputStreamReader(input);
        reader = new BufferedReader(streamReader);
        reader.mark(scanLimit);  
    }
    
    public void close() throws IOException {
        reader.close();
    }
    
    public String getDelimitedString(String pattern, String delimiter) throws IOException {
        
        int ind;
        String line;
        String str;

        reader.reset();
        while ( (line = reader.readLine() ) != null ) {
            nBytes += line.getBytes().length;
            if (nBytes > scanLimit){
                return null;
            }
            if ( (ind = line.indexOf(pattern)) != -1){  //find line with pattern
                ind = line.indexOf(delimiter);          //find the delimiter
                if (ind == -1){
                    System.out.println("Scanner:getDelimitedString: found pattern but not delimiter.\n");
                    return null;
                }
                String totrim =  line.substring(ind + delimiter.length() + 1); 
                return totrim.trim();    //return trimmed text to right of delimiter
            }
        }
        return null;
        
    }
}