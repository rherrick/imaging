/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.utils.imageformats;

public class DicomImage implements Comparable {
   float distFromNormal;
   int instanceNumber;
   String filePath;
   float LIKE_NULL = Float.NaN; 
   
   public DicomImage(String filePath,String i) {
       this.filePath = filePath;
       distFromNormal = LIKE_NULL;
       instanceNumber = Integer.parseInt(i.trim());
   }

   public DicomImage(String filePath,String i, float d) {
       this.filePath = filePath;
       instanceNumber = Integer.parseInt(i.trim());
       distFromNormal = d;
   }
   
   /**
    * @return Returns the distFromNormal.
    */
   public float getDistFromNormal() {
    return distFromNormal;
   }

    /**
 * @return Returns the filePath.
 */
public String getFilePath() {
    return filePath;
}

/**
 * @param filePath The filePath to set.
 */
public void setFilePath(String filePath) {
    this.filePath = filePath;
}

    /**
     * @param distFromNormal The distFromNormal to set.
     */
   public void setDistFromNormal(float distFromNormal) {
    this.distFromNormal = distFromNormal;
   }

    /**
     * @return Returns the instanceNumber.
     */
    public int getInstanceNumber() {
        return instanceNumber;
    }

    /**
     * @param instanceNumber The instanceNumber to set.
     */
    public void setInstanceNumber(String instanceNumber) {
        this.instanceNumber = Integer.parseInt(instanceNumber);
    }

    public int compareTo(Object o) throws ClassCastException {
       if (!(o instanceof DicomImage))
           throw new ClassCastException("A DicomImage object expected.");
        float distB = ((DicomImage)o).getDistFromNormal();
        int instanceNumber = ((DicomImage)o).getInstanceNumber();
        int rtn;
        if (this.distFromNormal != this.LIKE_NULL ) {
            if (Math.abs(this.distFromNormal - distB) > 0.000001)
                rtn= ((this.distFromNormal < distB)? -1 : 1);
            else
                rtn= ((this.instanceNumber < instanceNumber)?-1:1);
        }else
            rtn= ((this.instanceNumber < instanceNumber)?-1:1);
        //System.out.println(this.toString() + " " + ((DicomImage)o).toString() + " " + rtn);
        return rtn;
   }
    
    public String toString() {
        return "File=" +this.getFilePath() + " DISTANCE=" + this.getDistFromNormal() + " Instance Number=" + this.getInstanceNumber();
    }
}
