/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.Reader;

import ij.io.FileInfo;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.image.IndexColorModel;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AnalyzeObjectMapReader {
    
    String objMap;
    FileInfo fi;
    boolean littleEndian = false;
    int numberOfObjects = -1;
    
    String[] objNames;
    
    public AnalyzeObjectMapReader(String objmapFile) {
        objMap = objmapFile;
        if (!objMap.endsWith(".obj")) {
            objMap += ".obj";
        }
        fi = new FileInfo();
        parseObjMap(false,null);
    }
    
    private void parseObjMap(boolean createImageFiles, String outdirectory) {
        try {
            FileInputStream filein = new FileInputStream (objMap);
            DataInputStream input = new DataInputStream (filein);
            input.readInt();
            fi.width = input.readInt();
            fi.height = input.readInt();
            fi.nImages = input.readInt();
            numberOfObjects = input.readInt();
            objNames = new String[numberOfObjects];
            for (int i = 0; i < numberOfObjects; i++) {
                byte[] b = new byte[152];
                input.read(b);
                int j =0;
                for (; j < b.length; j++) {
                    if ((char)b[j]=='\0') {
                        break;
                    }
                }
                String objName = new String(b,0,j);
                String[] parts = objName.split("\\.");
                if (parts.length == 1)
                    objNames[i] = parts[0];
                else 
                    objNames[Integer.parseInt(parts[0])] = parts[1];
            }
            //if (createImageFiles) {
            //  boolean success =  createAnalyzeImageFile(input, outdirectory);
            //  System.out.println("Success " + success);
           // }
            
            try {input.close();}catch(Exception e) {};
            filein.close();
            
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public String[] getRegionLabels() {
        return objNames;
    }
    
    public int getNoOfRegions() {
        return objNames.length;
    }
    
    public FileInfo getFileInfo() {
        return fi;
    }
    
    private boolean createAnalyzeImageFile(DataInputStream input, String outdirectory) throws IOException {
        boolean failed = true;
        int indexOfSlash = objMap.lastIndexOf(File.separator);
        String rootName = objMap;
        if (indexOfSlash != -1) {
            rootName = objMap.substring(indexOfSlash+1);
        }
        int indexOfDot = rootName.lastIndexOf(".");
        if (indexOfDot != -1) {
            rootName = rootName.substring(0,indexOfDot);
        }
        
        ArrayList values = new ArrayList();
        ArrayList length = new ArrayList();
        
        int i = 1; boolean insert = false; int cnt = 0;
        while (input.available() != 0) {
            Byte mByte = new Byte(input.readByte());
            int value = unsignedByteToInt(mByte.byteValue());
            if (i == 1) {
               if (value > 0 ) {
                   length.add(new Integer(value));
                   i = 0;
                   insert = true;
               }else cnt++;
            }else {
                if (insert) {
                    values.add(mByte);
                    insert = false;
                }
               i=1;
            }
        }
        
        System.out.println("Skipped " + cnt + " Picked " + length.size() + " " + length.size());
        int[] lengthCummulativeSum = cummulativeSum(length);
        //print(lengthCummulativeSum);
        int[] lengthTemp = new int[lengthCummulativeSum[lengthCummulativeSum.length-1]];
        for ( i = 0; i < lengthTemp.length; i++) {
           if(i == 0) lengthTemp[i] = 1;
           else lengthTemp[i] = 0;
        }
        //print(lengthTemp);
        for ( i = 0; i < lengthCummulativeSum.length-1; i++) {
            lengthCummulativeSum[i] += 1;
            lengthTemp[lengthCummulativeSum[i]-1] = 1;
        }  
        //print(lengthTemp);
        lengthCummulativeSum = cummulativeSum(lengthTemp);
        //print(lengthCummulativeSum);        
        byte[] valueArray = new byte[lengthCummulativeSum.length];

        for ( i = 0; i < lengthCummulativeSum.length; i++) {
           valueArray[i] = ((Byte)values.get(lengthCummulativeSum[i]-1)).byteValue();
           System.out.print(valueArray[i]);
        }
        
        System.out.println("Pixels length " + valueArray.length);
        ImageProcessor ip = new ByteProcessor(fi.width, fi.height, valueArray,getDefaultColorModel());
        //ImagePlus imp = new ImagePlus(rootName,ip);
        //boolean created = new FileSaver(imp).saveAsRawStack(outdirectory + File.separator + rootName + ".img");
        //new AnalyzeWriter().save(imp,outdirectory,rootName +".hdr","TRANSVERSE");
        return !failed;
    }
    
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
     }
    
    
    private int[] cummulativeSum(ArrayList array) {
        if (array == null || array.size() == 0) return null;
        int[] arrayToInt = new int[array.size()];
        for (int i = 0; i < array.size(); i++) {
            arrayToInt[i] = ((Integer)array.get(i)).intValue();
        }
        return cummulativeSum(arrayToInt);
    }
    
    
    private int[] cummulativeSum(int[] array) {
        if (array == null || array.length == 0) return null;
        int[] rtn = new int[array.length];
        rtn[0] = array[0];
        for (int i = 1; i < array.length; i++) {
            rtn[i]=0;
            for (int j =0; j <= i; j++) {
                rtn[i] += array[j];
            }
            
        }
        return rtn;
    }
    
    private void print(int[] obj) {
        for (int i = 0; i < obj.length; i++)
            System.out.print(obj[i] + " ");
        System.out.println("******");
    }
    
    
    
    
    /*
    VisAD system for interactive analysis and visualization of numerical
    data.  Copyright (C) 1996 - 2002 Bill Hibbard, Curtis Rueden, Tom
    Rink, Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and
    Tommy Jasmin.

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Library General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Library General Public License for more details.

    You should have received a copy of the GNU Library General Public
    License along with this library; if not, write to the Free
    Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
    MA 02111-1307, USA
    */

    private int[] decodeRLE(int[] array) {
        // compute size of decoded array
        int count = 0;
        int i = 0;
        final int RLE_ESCAPE = Integer.MIN_VALUE;
        while (i < array.length) {
          if (array[i] == RLE_ESCAPE) {
            count += array[i + 2];
            i += 3;
          }
          else {
            count++;
            i++;
          }
        }

        // allocate decoded array
        int[] decoded = new int[count];
        int p = 0;

        // decode RLE sequence
        for (i = 0; i < array.length; i++) {
          int q = array[i];
          if (q == RLE_ESCAPE) {
            int val = array[++i];
            int cnt = array[++i];
            for (int z = 0; z < cnt; z++) decoded[p++] = val;
          }
          else decoded[p++] = q;
        }
        return decoded;
      }

    
   

    
    /** Returns the default grayscale IndexColorModel. */
    public IndexColorModel getDefaultColorModel() {
            byte[] r = new byte[256];
            byte[] g = new byte[256];
            byte[] b = new byte[256];
            for(int i=0; i<256; i++) {
                r[i]=(byte)i;
                g[i]=(byte)i;
                b[i]=(byte)i;
            }
            return new IndexColorModel(8, 256, r, g, b);
    }

  
   public static void main(String args[]) {
       AnalyzeObjectMapReader objReader = new AnalyzeObjectMapReader("Y:\\data2\\WORK\\PIPELINE_TEST\\jc_roi\\jc_cerebellum_mr_1.obj");
       String[] regions = objReader.getRegionLabels();
       for (int i = 0; i < regions.length; i++) {
           System.out.println("Obj Lable " + i + " " + regions[i]);
       }
       
   }
}
