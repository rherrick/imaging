//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Writer;

import java.io.*;
import ij.measure.*;
import org.nrg.plexiviewer.Reader.ReaderUtils;
public class IFHWriter {

		public IFHWriter() {
		}

		public void writeHeader(String file, String orientation, String bytes, Calibration cal, int width, int height, int depth) {
			String name="";
			if (file.endsWith(".img")) name = file.substring(0, file.length()-4 ); 
			else if (file.endsWith(".ifh")) name = file.substring(0, file.length()-4 ); 
			else
				name = file;
			File out = new File(name+".ifh");
			if (out == null){
				System.out.println("Couldn't open header file: " + file);
				return;
			}
			try{
				FileWriter writer = new FileWriter(out);
				writer.write("INTERFILE			:=\n");
				writer.write("version of keys			:= 3.3\n");
				writer.write("number format			:= "+ ReaderUtils.getNumberFormat(bytes)+ "\n");
				writer.write("number of bytes per pixel	:= " + bytes +"\n");
				writer.write("orientation			:= " +ReaderUtils.getOrientationAsInt(orientation,false) + "\n");
				writer.write("number of dimensions		:= 4\n");
				writer.write("matrix size [1]			:= " + width + "\n");
				writer.write("matrix size [2]			:= " + height + "\n");
				writer.write("matrix size [3]			:= " + depth + "\n");
				writer.write("matrix size [4]			:= 1\n");
				writer.write("scaling factor (mm/pixel) [1]	:= " + cal.pixelWidth  + "\n");
				writer.write("scaling factor (mm/pixel) [2]	:= " + cal.pixelHeight + "\n");
				writer.write("scaling factor (mm/pixel) [3]	:= " + cal.pixelDepth + "\n");
				writer.close();
			} catch(IOException e){}
		}        

}
