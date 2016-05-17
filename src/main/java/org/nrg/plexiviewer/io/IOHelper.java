/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.io;

import java.util.List;

import org.nrg.pipeline.xmlbeans.xnat.AbstractResource;
import org.nrg.pipeline.xmlbeans.xnat.ImageResource;
import org.nrg.pipeline.xmlbeans.xnat.ImageResourceSeries;
import org.nrg.pipeline.xmlbeans.xnat.ResourceCatalog;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.utils.PlexiConstants;
import org.nrg.plexiviewer.utils.URIUtils;
import org.nrg.xdat.bean.XnatAbstractresourceBean;
import org.nrg.xdat.bean.XnatDicomseriesBean;
import org.nrg.xdat.bean.XnatDicomseriesImageBean;
import org.nrg.xdat.bean.XnatImageresourceBean;
import org.nrg.xdat.bean.XnatImageresourceseriesBean;
import org.nrg.xdat.bean.XnatResourcecatalogBean;

public class IOHelper {

	
	
	   public static PlexiImageFile getPlexiImageFileFromResourceCatalog(AbstractResource file, String cachepath, String name) {
	        PlexiImageFile pf = new PlexiImageFile();
	        ResourceCatalog catalog = (ResourceCatalog)file.changeType(ResourceCatalog.type);
	        pf.setResourceCatalogPath(catalog.getURI());
	        pf.setXsiType(PlexiConstants.XNAT_RESOURCECATALOG);
	        if (catalog.getFormat() != null)
	        	pf.setFormat(catalog.getFormat());
	        else	
	        	pf.setFormat("DICOM");
	        pf.setCachePath(cachepath);
	        pf.setName(name);
	        return pf;
	        
	    }
	   
	   
	   public static PlexiImageFile getPlexiImageFileFromImageResource(AbstractResource file) throws Exception {
	        ImageResource imageResource = (ImageResource)file.changeType(ImageResource.type);
	        PlexiImageFile pf = new PlexiImageFile();
	        pf.setFormat(imageResource.getFormat());
	        pf.setURIAsString(URIUtils.getURI(imageResource.getURI()).toString());
	        pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCE);
	        if (imageResource.getDimensions() != null) {
	            ImageResource.Dimensions dims = imageResource.getDimensions();
	            pf.setDimensions(dims.getX().intValue(), dims.getY().intValue(), dims.getZ().intValue());
	            pf.setVolumes(dims.getVolumes().intValue());
	        }
	        if (imageResource.getVoxelRes() != null) {
	            ImageResource.VoxelRes voxelRes = imageResource.getVoxelRes();
	            pf.setVoxelResX(voxelRes.getX());
	            pf.setVoxelResY(voxelRes.getY());
	            pf.setVoxelResZ(voxelRes.getZ());
	        }
	        return pf;
	    }
	   
	   public static PlexiImageFile getPlexiImageFileFromImageResourceSeries(AbstractResource file, String cachepath) throws Exception {
	        ImageResourceSeries imageResourceSeries = (ImageResourceSeries)file.changeType(ImageResourceSeries.type);
	        PlexiImageFile pf = new PlexiImageFile();
	        pf.setFormat(imageResourceSeries.getFormat());
	        pf.setURIAsString(imageResourceSeries.getPath());
	        pf.setPath(imageResourceSeries.getPath());
	        pf.setPattern(imageResourceSeries.getPattern());
	        pf.setName(imageResourceSeries.getName());
	        pf.setCachePath(cachepath);
	        pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCESERIES);
	        if (imageResourceSeries.getDimensions() != null) {
	            ImageResourceSeries.Dimensions dims = imageResourceSeries.getDimensions();
	            pf.setDimensions(dims.getX().intValue(), dims.getY().intValue(), dims.getZ().intValue());
	            pf.setVolumes(dims.getVolumes().intValue());
	        }
	        if (imageResourceSeries.getVoxelRes() != null) {
	            ImageResourceSeries.VoxelRes voxelRes = imageResourceSeries.getVoxelRes();
	            pf.setVoxelResX(voxelRes.getX());
	            pf.setVoxelResY(voxelRes.getY());
	            pf.setVoxelResZ(voxelRes.getZ());
	        }
	        return pf;
	    }
    public static PlexiImageFile getPlexiImageFile(XnatAbstractresourceBean file,  String cachepath, String name) throws Exception{
        PlexiImageFile pf = new PlexiImageFile();
        if (file instanceof XnatImageresourceBean) {
            pf = IOHelper.getPlexiImageFileFromImageResource(file);
        }else if (file instanceof XnatImageresourceseriesBean) {
            pf =IOHelper.getPlexiImageFileFromImageResourceSeries(file, cachepath);
        }else if (file instanceof XnatDicomseriesBean) {
            pf = IOHelper.getPlexiImageFileFromDicomSeries(file, cachepath, name);
        }else if (file instanceof XnatResourcecatalogBean) {
            pf = IOHelper.getPlexiImageFileFromResourceCatalog(file, cachepath, name);
        }
        return pf;
    }

    
    public static PlexiImageFile getPlexiImageFileFromResourceCatalog(XnatAbstractresourceBean file, String cachepath, String name) {
        PlexiImageFile pf = new PlexiImageFile();
        XnatResourcecatalogBean catalog = (XnatResourcecatalogBean)file;
        pf.setResourceCatalogPath(catalog.getUri());
        pf.setXsiType(PlexiConstants.XNAT_RESOURCECATALOG);
        pf.setFormat(catalog.getFormat());
        pf.setCachePath(cachepath);
        pf.setName(name);
        return pf;
        
    }
    
    

    public static PlexiImageFile getPlexiImageFileFromDicomSeries(XnatAbstractresourceBean file, String cachepath, String name) {
        XnatDicomseriesBean dicomFiles = (XnatDicomseriesBean)file;
        PlexiImageFile pf = new PlexiImageFile();
        List  images = dicomFiles.getImageset_image();
        if (images != null && images.size() > 0) {
            for (int j = 0; j < images.size(); j++) {
                pf.addToFiles(((XnatDicomseriesImageBean)images.get(j)).getUri());
            }
        }
        if (dicomFiles.getDimensions_x() != null && dicomFiles.getDimensions_y() != null && dicomFiles.getDimensions_z() != null && dicomFiles.getDimensions_volumes() != null) {
            pf.setDimensions(dicomFiles.getDimensions_x().intValue(), dicomFiles.getDimensions_y().intValue(), dicomFiles.getDimensions_z().intValue());
            pf.setVolumes(dicomFiles.getDimensions_volumes().intValue());
        }
        if (dicomFiles.getVoxelres_x() != null && dicomFiles.getVoxelres_y() != null && dicomFiles.getVoxelres_z() != null ) {
            pf.setVoxelResX(dicomFiles.getVoxelres_x().doubleValue());
            pf.setVoxelResY(dicomFiles.getVoxelres_y().doubleValue());
            pf.setVoxelResZ(dicomFiles.getVoxelres_z().doubleValue());
        }
        pf.setFormat("DICOM");
        pf.setXsiType(PlexiConstants.XNAT_DICOMSERIES);
        pf.setCachePath(cachepath);
        pf.setName(name);
        return pf;
    }

    

    public static PlexiImageFile getPlexiImageFileFromImageResourceSeries(XnatAbstractresourceBean file, String cachepath) throws Exception {
        XnatImageresourceseriesBean imageResourceSeries = (XnatImageresourceseriesBean)file;
        PlexiImageFile pf = new PlexiImageFile();
        pf.setFormat(imageResourceSeries.getFormat());
        pf.setURIAsString(imageResourceSeries.getPath());
        pf.setPath(imageResourceSeries.getPath());
        pf.setPattern(imageResourceSeries.getPattern());
        pf.setName(imageResourceSeries.getName());
        pf.setCachePath(cachepath);
        pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCESERIES);
        if (imageResourceSeries.getDimensions_x() != null && imageResourceSeries.getDimensions_y() != null && imageResourceSeries.getDimensions_z() != null && imageResourceSeries.getDimensions_volumes() != null) {
            pf.setDimensions(imageResourceSeries.getDimensions_x().intValue(), imageResourceSeries.getDimensions_y().intValue(), imageResourceSeries.getDimensions_z().intValue());
            pf.setVolumes(imageResourceSeries.getDimensions_volumes().intValue());
        }
        if (imageResourceSeries.getVoxelres_x() != null && imageResourceSeries.getVoxelres_y() != null && imageResourceSeries.getVoxelres_z() != null ) {
            pf.setVoxelResX(imageResourceSeries.getVoxelres_x().doubleValue());
            pf.setVoxelResY(imageResourceSeries.getVoxelres_y().doubleValue());
            pf.setVoxelResZ(imageResourceSeries.getVoxelres_z().doubleValue());
        }

        
        return pf;
    }

    

    public static PlexiImageFile getPlexiImageFileFromImageResource(XnatAbstractresourceBean file) throws Exception {
        XnatImageresourceBean imageResource = (XnatImageresourceBean)file;
        PlexiImageFile pf = new PlexiImageFile();
        pf.setFormat(imageResource.getFormat());
        pf.setURIAsString(URIUtils.getURI(imageResource.getUri()).toString());
        pf.setXsiType(PlexiConstants.XNAT_IMAGERESOURCE);
        if (imageResource.getDimensions_x() != null && imageResource.getDimensions_y() != null && imageResource.getDimensions_z() != null && imageResource.getDimensions_volumes() != null) {
            pf.setDimensions(imageResource.getDimensions_x().intValue(), imageResource.getDimensions_y().intValue(), imageResource.getDimensions_z().intValue());
            pf.setVolumes(imageResource.getDimensions_volumes().intValue());
        }
        if (imageResource.getVoxelres_x() != null && imageResource.getVoxelres_y() != null && imageResource.getVoxelres_z() != null ) {
            pf.setVoxelResX(imageResource.getVoxelres_x().doubleValue());
            pf.setVoxelResY(imageResource.getVoxelres_y().doubleValue());
            pf.setVoxelResZ(imageResource.getVoxelres_z().doubleValue());
        }
        return pf;
    }

    
}
