//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.gui;

import java.awt.*;
import java.io.*;


public class PlexiSaveDialog {

    private String dir;
    private String name;
    
    private String title;
    private String extension;
    
    
     /** Displays a file save dialog with 'title' as the 
        title, 'defaultName' as the initial file name, and
        'extension' (e.g. ".tif") as the default extension.
    */
    public PlexiSaveDialog(String title, String defaultName, String extension) {
        this.title = title;
        this.extension = extension;
        defaultName = addExtension(defaultName, extension);
        save(title, null, defaultName);
    }

    
    String addExtension(String name, String extension) {
        if (name!=null && extension!=null) {
            int dotIndex = name.lastIndexOf(".");
            if (dotIndex>=0)
                name = name.substring(0, dotIndex) + extension;
            else
                name += extension;
        }
        return name;
    }
    

    // Save using FileDialog
    void save(String title, String defaultDir, String defaultName) {
        Frame parent = new Frame();
        FileDialog fd = new FileDialog(parent, title, FileDialog.SAVE);
        if (defaultName!=null)
            fd.setFile(defaultName);            
        if (defaultDir!=null)
            fd.setDirectory(defaultDir);
        fd.show();
        name = addExtension(fd.getFile(),extension);
        dir = fd.getDirectory();
        if (name==null)
            System.out.println("File Name is null ");
        fd.dispose();
        parent.dispose();
    }
    
    /** Returns the selected directory. */
    public String getDirectory() {
        return dir;
    }
    
    /** Returns the selected file name. */
    public String getFileName() {
        return name;
    }
    
}


class customFilter implements FilenameFilter 
{ 
	private String extension; 
	public customFilter(String ext) 
	{ 
		this.extension = "." + ext; 
	} 
	public boolean accept(File dir, String file) 
	{
		return file.endsWith(extension); 
	}
}