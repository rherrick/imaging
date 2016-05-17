/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.utils;

import ij.ImagePlus;

import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.FileReader;

import org.nrg.plexiviewer.io.PlexiFileOpener;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;

public class LUTApplier {
 
    PlexiImageFile plexifile;
    String lkupfile;
    boolean radiologic;
    

    /*
     * This class applies a lkup file to the image file
     * lkupfile is an ASCII file of RGB values 
     */
    public LUTApplier(PlexiImageFile file, String lkupfile) {
        plexifile = file;
        this.lkupfile = lkupfile;
        radiologic = false;
    }

    public LUTApplier(String lkupfile) {
        this.lkupfile = lkupfile;
        radiologic = false;
    }

    
    /**
     * @return Returns the radiologic.
     */
    public boolean isRadiologic() {
        return radiologic;
    }



    /**
     * @param radiologic The radiologic to set.
     */
    public void setRadiologic(boolean radiologic) {
        this.radiologic = radiologic;
    }

    public ImagePlus applyLUT(ImagePlus img) throws Exception{
        IndexColorModel cm = getColorModel();
        img.getProcessor().setColorModel(cm);
        if (img.getStackSize()>1)
            img.getStack().setColorModel(cm);
        img.updateImage();
        return img;
    }
    
    
    public ImagePlus getImagePlus() throws Exception{
        ImagePlus baseimage = PlexiFileOpener.openBaseFile(plexifile, radiologic);
        IndexColorModel cm = getColorModel();
        baseimage.getProcessor().setColorModel(cm);
        if (baseimage.getStackSize()>1)
            baseimage.getStack().setColorModel(cm);
        baseimage.updateImage();

        return baseimage;
    }
    
    private IndexColorModel getColorModel() throws Exception {
        int[][] rgb = readLkupFile();
        int size = rgb.length;
        byte [] reds = new byte[size];
        byte [] greens = new byte[size];
        byte [] blues = new byte[size];
        //byte [] alphas = new byte[size];
        for (int i=0; i<256; i++){
            reds[i] =   (byte)(rgb[i][0] & 255);
            greens[i] = (byte)(rgb[i][1] & 255);
            blues[i] =  (byte)(rgb[i][2] & 255);
        }
        return new IndexColorModel(8,256,reds,greens,blues);
    }
    
    private int[][] readLkupFile() throws Exception {
        BufferedReader in = null ;
        int[][] rgbs = new int[256][3];
        int[][] rtn = new int[256][3];
        try {
           in = new BufferedReader(new FileReader(lkupfile));
            String str;
            int lineCnt = 0;
            while ((str = in.readLine()) != null) {
                lineCnt ++;
                String[] rgb = str.split(" ");
                if (rgb.length != 3) {
                    throw new Exception("Inavlid file format expecting space separated three col input from " + lkupfile + " found " + str + " on line " + lineCnt); 
                }
                for (int j = 0; j < 3; j++)
                    rgbs[lineCnt - 1][j] = Integer.parseInt(rgb[j]);
            }
            if (lineCnt == 0) {
                throw new Exception("Incomplete file...expecting space separated three col input from " + lkupfile); 
            }
            if (lineCnt < 256) {
                int bins = (int)Math.round(256.0/lineCnt);
                for (int i = 0; i < 256; i++ ) {
                    int binRow = i/bins;
                    for (int j = 0; j < 3; j++)
                        rtn[i][j] = rgbs[binRow][j];
                }
            }else {
                rtn = rgbs;
            }
        }catch(Exception e) {
           throw e;
        }finally {
            if (in != null)
                in.close();
        }
        return rtn;
    }
    
    
}
