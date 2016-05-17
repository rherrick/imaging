/* Copyright Washington University in St Louis 2006
All rights reserved

@author Mohana Ramaratnam (Email: mramarat@wustl.edu) */

package org.nrg.plexiviewer.converter;
import ij.ImagePlus;
import ij.process.StackProcessor;

import org.nrg.plexiviewer.lite.io.*;
import org.nrg.plexiviewer.utils.Transform.*;

import java.io.*;

import org.nrg.plexiviewer.utils.FileUtils;
import org.nrg.plexiviewer.utils.PlexiConstants;
import org.nrg.plexiviewer.io.PlexiFileOpener;

import java.awt.*;

public class NonXnatConverter  {
    String fromFileName;
    String fromPath;
    String toFileName;
    String toPath;
    String baseOrientation;
    boolean isRadiologic;
    String format = null;
    float minIntensity=-1, maxIntensity=-1;
    
    public NonXnatConverter(String fPath, String fFileName, String tPath) {
        fromFileName = fFileName;
        fromPath = fPath;
        toPath = tPath;
        isRadiologic = false;
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
       baseOrientation = pfo.getOrientation();  
       //System.out.println("The in ori is " + baseOrientation);
       return image;        
    }
    
    private void setToFileName(String orientation, int sliceNo) {
        toFileName = FileUtils.getThumbnailFileName(fromFileName,orientation,sliceNo);
    }

    
    public int createThumbnail(String outOrientation, int slice) {
        try {
            PlexiImageFile pf = new PlexiImageFile();
            pf.setPath(fromPath); pf.setName(fromFileName);
            pf.setXsiType(PlexiConstants.PLEXI_IMAGERESOURCE);
            try {
                if (pf.getPath().endsWith("/"))
                    pf.setURIAsString(pf.getPath() + pf.getName() );
                 else
                     pf.setURIAsString(pf.getPath() + "/" + pf.getName());
            }catch(Exception e) {System.out.println("URI Exception " + pf.getPath() + " " + pf.getName());}
            ImagePlus img = openBaseFile(pf);
            if (img==null) { return -1;}
           /* if (lRes!=null) {
                if (lRes.getCropDetails().deleteSlices()) {
                    img = TransformationTools.dropSlices(img, lRes.getCropDetails().getStartSlice(),lRes.getCropDetails().getEndSlice());
                }
                if (lRes.getCropDetails().crop()) {
                    img = TransformationTools.crop(img, lRes.getCropDetails().getBoundingRectangle());
                }
            }*/
            PlexiImageOrientor pio=new PlexiImageOrientor(img,format);
            img = pio.getImage(baseOrientation,outOrientation+"F");
            if (minIntensity!=-1 && maxIntensity!=-1) {
                PlexiIntensitySetter piSetter = new PlexiIntensitySetter(minIntensity, maxIntensity);
                piSetter.setIntensities(img,true); 
            }
            
            if (!isRadiologic()) {
                if (img!=null && !isRadiologic()) {
                   StackProcessor sp = new StackProcessor(img.getStack(), img.getProcessor());
                   sp.flipHorizontal();
                   if (outOrientation.equalsIgnoreCase("sagittal")){
                    img=new plexiViewerImageRelayer(outOrientation).reverseStacks(img);
                   }    
                }   
            }


            if (!FileUtils.dirExists(toPath)) {
                FileUtils.createDirectory(toPath);    
            }
            //System.out.println("SliceNos " + sliceNos.size());
            String radMarker ="R";
            //for (int sliceNo = 0; sliceNo<sliceNos.size();sliceNo++) {
              //  int slice = ((Integer)sliceNos.get(sliceNo)).intValue();
                //System.out.println(options.getOrientation() + " " + slice);
                setToFileName(outOrientation,slice);
                img.setSlice(slice);
                img.updateImage();
                if (isRadiologic()) {
                    radMarker="L";
                }
                if (!outOrientation.equalsIgnoreCase("SAGITTAL")) {
                    img.getProcessor().setColor(new Color(50,200,25));
                    img.getProcessor().setFont(new Font("Serif", Font.BOLD, 12));
                    img.getProcessor().drawString(radMarker,img.getWidth()-20,15);
                    img.updateImage();
                }
                PlexiFileSaver fs =  new PlexiFileSaver(img.getImage());
                fs.saveImageAsGif(toPath+File.separator+toFileName);
                System.out.println(this.getClass().getName() + " Created file " + toPath +File.separator + toFileName);
            //}
            pio.clearImage();
            if (img!=null)img.flush();
            img=null;
            return 0;           
            
        }catch(Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
        
    }

    public boolean isRadiologic() {
        return isRadiologic;
    }

    public void setRadiologic(boolean isRadiologic) {
        this.isRadiologic = isRadiologic;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public float getMaxIntensity() {
        return maxIntensity;
    }

    public void setMaxIntensity(float maxIntensity) {
        this.maxIntensity = maxIntensity;
    }

    public float getMinIntensity() {
        return minIntensity;
    }

    public void setMinIntensity(float minIntensity) {
        this.minIntensity = minIntensity;
    }
    
    public static void main(String args[]) {
        NonXnatConverter converter = new NonXnatConverter("C:\\Archive\\disc1\\OAS1_0001_MR1\\PROCESSED\\MPRAGE\\T88_111","OAS1_0001_MR1_mpr_n4_anon_111_t88_gfc.img" ,"C:\\Archive\\disc1\\OAS1_0001_MR1");
        converter.setFormat("ANALYZE");
        converter.createThumbnail("CORONAL",66);
        System.out.println("Created file");
    }
    
}
