/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.converter;

import java.io.File;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import org.nrg.plexiviewer.Writer.AnalyzeWriter;
import org.nrg.plexiviewer.io.PlexiFileOpener;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.utils.ImageUtils;
import org.nrg.plexiviewer.utils.PlexiConstants;

public class RoiCreator {
	
	String infileURI;
	String outdir;
	String imageType="ANALYZE";
	String baseOrientation;
	
	public void createRoi() throws Exception {
		ImagePlus img = getImage();
		extractRoi(img);	
	}

	
	private void extractRoi(ImagePlus img) {
		for (int i = 1; i <=255; i++) {
			ImageStack istack = img.createEmptyStack();
			ImageStack stack = img.getImageStack();
			for (int j=1; j <=stack.getSize(); j++) {
				ImageProcessor ip = stack.getProcessor(j);
				ImageProcessor newip = new ByteProcessor(img.getWidth(), img.getHeight());
				if (ip != null) {
					for (int y = 0; y < img.getHeight(); y++) {
						int pixelvalues[] = new int[img.getWidth()];
						ip.getRow(0, y, pixelvalues, img.getWidth());
						for (int k = 0; k < pixelvalues.length; k++) {
							if (pixelvalues[k] != i) {
								pixelvalues[k] = 0;
							}
						}
						newip.putRow(0, y, pixelvalues, img.getWidth());
					}
					istack.addSlice("j", newip);
				}
			}
			ImagePlus roiImage = new ImagePlus("ROI_" + i, istack);
			saveAsAnalyze(i,roiImage);
		}
	}
	
	private void saveAsAnalyze(int label, ImagePlus img) {
		String toFileName = "roi_"+label + ".img";
        boolean created = new FileSaver(img).saveAsRawStack(outdir + toFileName);
        if (created) {
            img.setFileInfo(ImageUtils.getFileInfo(img.getCalibration(), img.getWidth(), img.getHeight(), img.getStackSize()));
            new AnalyzeWriter().save(img,outdir,toFileName,baseOrientation+"F");
            System.out.println(" Created file " + outdir +File.separator + toFileName);
        }
	}
	

	private ImagePlus getImage() throws Exception {
		  PlexiImageFile pf = new PlexiImageFile();
          pf.setURIAsString(infileURI);
          pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCE);
          PlexiFileOpener pfo = new PlexiFileOpener(imageType,pf);
          ImagePlus img = pfo.getImagePlus();
          baseOrientation = pfo.getOrientation();
          return img;
	}
	
	/**
	 * @return the infile
	 */
	public String getInfile() {
		return infileURI;
	}

	/**
	 * @param infile the infile to set
	 */
	public void setInfile(String infile) {
		this.infileURI = infile;
	}

	/**
	 * @return the outdir
	 */
	public String getOutdir() {
		return outdir;
	}

	/**
	 * @param outdir the outdir to set
	 */
	public void setOutdir(String outdir) {
		if (!outdir.endsWith(File.separator)) outdir += File.separator;
		this.outdir = outdir;
	}
 
	
	public static void main(String args[]) {
		String infilepath = args[0];
		String outdir = args[1];
		RoiCreator roiCreator = new RoiCreator();
		roiCreator.setInfile(infilepath);
		roiCreator.setOutdir(outdir);
		try {
			roiCreator.createRoi();
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	}
