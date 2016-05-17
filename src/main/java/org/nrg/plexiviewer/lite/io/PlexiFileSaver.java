//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.io;


import ij.ImagePlus;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.nrg.plexiviewer.ij.GifEncoder;
import org.nrg.plexiviewer.lite.gui.PlexiSaveDialog;




public class PlexiFileSaver {

    private static String defaultDirectory = null;
    private String name;
    private String directory;
    private java.awt.Image image;


	public PlexiFileSaver(java.awt.Image img) {
		image=img;
	}

    public PlexiFileSaver() {
    }

    
    public void setImageFileName(String n) {
    	name = n;
    }
    
    String getPath(String type, String extension) {
		PlexiSaveDialog sd = new PlexiSaveDialog("Save as "+type, name, extension);
        name = sd.getFileName();
        if (name==null)
            return null;
        directory = sd.getDirectory();
        String path = directory+name;
        return path;
    }
    
    
	/** Save the image in GIF format using a save file
		   dialog. Returns false if the user selects cancel
		   or the image is not 8-bits. */
	   public boolean saveImageAsGif() {
		   String path = getPath("GIF", ".gif");
		   if (path==null)
			   return false;
		   else if (image==null)
		   		return false;
		   else	
			   return saveImageAsGif(path);
	   }

      public void saveAsJpeg(ImagePlus imp, String path, int quality) {
            //IJ.log("saveAsJpeg: "+path);
            int width = imp.getWidth();
            int height = imp.getHeight();
            BufferedImage   bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            try {
                FileOutputStream  f  = new FileOutputStream(path);                
                Graphics g = bi.createGraphics();
                g.drawImage(imp.getImage(), 0, 0, null);
                g.dispose();            
                //JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(f);
                //JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
                //param.setQuality((float)(quality/100.0), true);
                //encoder.encode(bi, param);
                ImageIO.write(bi, "jpg", f);
                f.close();
            }
            catch (Exception e) {
               e.printStackTrace();
            }
        }

       
       
    /** Save the image in GIF format using a save file
        dialog. Returns false if the user selects cancel
        or the image is not 8-bits. */
    public boolean saveAsGif() {
        String path = getPath("GIF", ".gif");
        if (path==null)
            return false;
        else
            return saveImageAsGif(path);
    }
    
	/** Save the image in Gif format using the specified path. Returns
		   false if the image is not 8-bits or there is an I/O error. */
	   public boolean saveImageAsGif(String path) {
		   OutputStream output = null;
		   boolean rtn = false;
		   try {
			   output = new BufferedOutputStream(new FileOutputStream(path));
			   GifEncoder gifE = new GifEncoder(image);
			   gifE.write(output);
			   rtn = true;
		   }
		   catch (IOException e) {
			   showErrorMessage(e);
		   }finally {
			   if (output != null) try {output.close();}catch(Exception e1) {e1.printStackTrace();}
		   }
		   return rtn;
	   }
	   
	   public boolean writeImageAsGif(String path, OutputStream output) {
		   try {
	           
			   //OutputStream output = new BufferedOutputStream(new FileOutputStream(path));
			   GifEncoder gifE = new GifEncoder(image);
			   gifE.write(output);
		   }
		   catch (IOException e) {
			   showErrorMessage(e);
			   return false;
		   }
		   return true;
	   }
	   

	void showErrorMessage(IOException e) {
		   System.out.println("An error occured writing the file.\n \n" + e);
	   }


 
}
