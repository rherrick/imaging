//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.display;

public class PlexiNativeCoordinates {
	
	
	public static Point3d getNativeCoordinates(Point3d winPos, String fromView, String toView, boolean radiologic, Point3d imgDim, boolean fromRadiologic, int scaleFactor) {
		return getNativeCoordinates((int)winPos.getX(), (int)winPos.getY(), (int)winPos.getZ(),  fromView, toView, radiologic, imgDim, fromRadiologic, scaleFactor);
	}
	
	public static Point3d getTalCoordinates(Point3d talPos, String fromView, String toView, int radiologic, Point3d bboxDim, Point3d imgDim, Point3d origin, int voxelSize, int scaleFactor, boolean fromRadiologic) {
		Point3d rtn = new Point3d();
		int x = 0, y = 0, z=0;
		if (fromView.equalsIgnoreCase("SAGITTAL")) {
			if (toView.equalsIgnoreCase("CORONAL")) {
				x = (int)(radiologic * talPos.z/voxelSize + origin.x);
				y = (int)( (bboxDim.z - origin.z) - (talPos.y/voxelSize));
				z = (int)imgDim.z - (int)origin.getY() + (int)(talPos.x/voxelSize) ;
			}else if (toView.equalsIgnoreCase("TRANSVERSE")) {
				x =(int)(radiologic * talPos.z/voxelSize + origin.x);
				y = (int)(origin.y - talPos.x/voxelSize);
				z = (int)(imgDim.z - (int)( (bboxDim.z - origin.z) - (talPos.y/voxelSize)));
			}else if (toView.equalsIgnoreCase("SAGITTAL")) {
				x = (int)(origin.y - talPos.x/voxelSize);
				y = (int)( (bboxDim.z - origin.z) - (talPos.y/voxelSize));
				z = (int)origin.getX() + (int)(talPos.z/voxelSize);
			}
		}else  if (fromView.equalsIgnoreCase("TRANSVERSE")) {
			if (toView.equalsIgnoreCase("TRANSVERSE")) {
				x =(int) (radiologic * (talPos.x/voxelSize) + origin.x);
				y = (int) (origin.y - talPos.y/voxelSize);
				z = (int)(talPos.z/voxelSize) + (int)origin.getZ();
			}else if (toView.equalsIgnoreCase("CORONAL")) {
				x = (int)(radiologic * talPos.x/voxelSize + origin.x);
				y = (int)( (bboxDim.z - origin.z) - (talPos.z/voxelSize));
				z =  (int)imgDim.z - (int)origin.getY() + (int)(talPos.y/voxelSize) ;
			}else if (toView.equalsIgnoreCase("SAGITTAL")) {
				x = (int)(origin.y - talPos.y/voxelSize);
				y = (int)( (bboxDim.z - origin.z) - (talPos.z/voxelSize));
				z = (int)origin.getX() + (int)(talPos.x/voxelSize);
			}
		}else if (fromView.equalsIgnoreCase("CORONAL")) {
			if (toView.equalsIgnoreCase("CORONAL")) {
				x = (int)(radiologic * talPos.x/voxelSize + origin.x);
				y = (int)( (bboxDim.z - origin.z) - (talPos.y/voxelSize));
				z =  (int)origin.getY() - (int)(talPos.z/voxelSize)   ;
			}else if (toView.equalsIgnoreCase("SAGITTAL")) {
				x = (int)imgDim.x - (int)(origin.y - talPos.z/voxelSize);
				y = (int)( (bboxDim.z - origin.z) - (talPos.y/voxelSize));
				z = (int)origin.getX() + (int)(talPos.x/voxelSize);
			}else if (toView.equalsIgnoreCase("TRANSVERSE")) {
				x =(int) (radiologic * (talPos.x/voxelSize) + origin.x);
				y = (int)imgDim.y - (int) (origin.y - talPos.z/voxelSize);
				z = (int)(talPos.y/voxelSize) + (int)origin.getZ();
			}
		}
		
		rtn.set(x*scaleFactor,y*scaleFactor,z);
		//System.out.println(toView + " WIN COORDINATES " + rtn.toString());
		return rtn;
	}


	
	public static Point3d  getNativeCoordinates(int x, int y, int z, String fromView, String toView, boolean radiologic, Point3d imgDim, boolean fromRadiologic, int scaleFactor) {
		Point3d rtn = new Point3d(x,y,z);
		if (fromView.equalsIgnoreCase("SAGITTAL")) {
			if (toView.equalsIgnoreCase("CORONAL")) {
				rtn = new Point3d(z,y,imgDim.z-x);
				if (radiologic) {
					Point3d old = rtn;
					rtn.setX(imgDim.getX() - old.getX()); //img Width - old X as a flip is done for radiologic view
				}	
			}else if (toView.equalsIgnoreCase("TRANSVERSE")) {
				rtn = new Point3d(z,x,imgDim.z-y);
				if (radiologic) {
					Point3d old = rtn;
					rtn.setX(imgDim.getX() - old.getX()); //img Width - old X as a flip is done for radiologic view
				}	
			}//else if (toView.equalsIgnoreCase("SAGITTAL")) {
              //  if ((radiologic && !fromRadiologic) || (!radiologic && fromRadiologic))
               //     rtn = new Point3d(imgDim.getX() - x,y,imgDim.z-z);
            //}
		}else if (fromView.equalsIgnoreCase("TRANSVERSE")) {
			if (toView.equalsIgnoreCase("SAGITTAL")) {
				rtn = new Point3d(y,imgDim.y - z,x);
				if (fromRadiologic) {
					Point3d old = rtn;
					rtn.setZ(imgDim.getZ() - rtn.getZ());
				}
               // if (radiologic) {
                //    rtn.setZ(imgDim.getZ() - rtn.getZ());
                //    rtn.setX(imgDim.getX() - rtn.getX());
               // }
			}else if (toView.equalsIgnoreCase("CORONAL")) { 
				rtn = new Point3d(x,imgDim.y - z,imgDim.z-y);
				if ((radiologic  && !fromRadiologic) || (!radiologic  && fromRadiologic)) {
					Point3d old = rtn;
					rtn.setX(imgDim.getX() - old.getX()); //img Width - old X as a flip is done for radiologic view
				}
			} else {
				if ((radiologic  && !fromRadiologic) || (!radiologic  && fromRadiologic)) {
					Point3d old = rtn;
					rtn.setX(imgDim.getX() - old.getX()); //img Width - old X as a flip is done for radiologic view
				}
			 }
		}else if (fromView.equalsIgnoreCase("CORONAL")) {
			if (toView.equalsIgnoreCase("SAGITTAL")) { 
				rtn = new Point3d(imgDim.x-z,y,x);
				if (fromRadiologic) {
					Point3d old = rtn;
					rtn.setZ(imgDim.getZ() - rtn.getZ());
				}
                //if (radiologic) {
                 //   rtn.setZ(imgDim.getZ() - rtn.getZ());
                 //   rtn.setX(imgDim.getX() - rtn.getX());
               // }
            }	
			else if (toView.equalsIgnoreCase("TRANSVERSE")) {
				rtn = new Point3d(x,imgDim.y-z,imgDim.z-y);
				if ((radiologic  && !fromRadiologic) || (!radiologic  && fromRadiologic)) {
					Point3d old = rtn;
					rtn.setX(imgDim.getX() - old.getX()); //img Width - old X as a flip is done for radiologic view
				}
			}else {
				if ((radiologic  && !fromRadiologic) || (!radiologic  && fromRadiologic)) {
					Point3d old = rtn;
					rtn.setX(imgDim.getX() - old.getX()); //img Width - old X as a flip is done for radiologic view
				}
			}
		}
		Point3d old = rtn;
		rtn.set(old.getX()*scaleFactor, old.getY()*scaleFactor, old.getZ());
		return rtn;	
	}
	
	
}
