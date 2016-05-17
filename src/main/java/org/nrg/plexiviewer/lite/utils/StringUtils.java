//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.utils;

import java.text.*;
import java.util.Locale;

public class StringUtils {
	
	public static String cleanUp(String input, String toFind, String toReplace) {
		String rtn=input;
		int indexOfToFind;
		do {
			 indexOfToFind = rtn.indexOf(toFind);
			 if (indexOfToFind==-1) break;
			 if (indexOfToFind==0)
				rtn=toReplace + rtn.substring(indexOfToFind+toFind.length(), rtn.length());
			else
				rtn = rtn.substring(0,indexOfToFind) + toReplace + rtn.substring(indexOfToFind+toFind.length(), rtn.length());
		}while(indexOfToFind!=-1);
		return rtn;			
	}
	
	public static String peelSpaces(String inStr) {
		String outStr="";
		if (inStr==null)
			return null;
		inStr.trim();
		for (int i=0; i<inStr.length();i++) {
			if (inStr.charAt(i)!=' ')
				outStr += inStr.charAt(i);
		}
		return outStr;
	}
	
	
	// FROM IJ.java
	 
	private static DecimalFormat df =
		   new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
	
	private static int dfDigits = 2;


	/** Converts a number to a rounded formatted string.
		   The 'precision' argument specifies the number of
		   digits to the right of the decimal point. */
	public static String d2s(double n, int precision) {
		   if (n==Float.MAX_VALUE) // divide by 0 in FloatProcessor
			   return "3.4e38";
		   boolean negative = n<0.0;
		   if (negative)
			   n = -n;
		   double whole = Math.round(n * Math.pow(10, precision));
		   double rounded = whole/Math.pow(10, precision);
		   if (negative)
			   rounded = -rounded;
		   if (precision!=dfDigits)
			   switch (precision) {
				   case 0: df.applyPattern("0"); dfDigits=0; break;
				   case 1: df.applyPattern("0.0"); dfDigits=1; break;
				   case 2: df.applyPattern("0.00"); dfDigits=2; break;
				   case 3: df.applyPattern("0.000"); dfDigits=3; break;
				   case 4: df.applyPattern("0.0000"); dfDigits=4; break;
				   case 5: df.applyPattern("0.00000"); dfDigits=5; break;
				   case 6: df.applyPattern("0.000000"); dfDigits=6; break;
				   case 7: df.applyPattern("0.0000000"); dfDigits=7; break;
				   case 8: df.applyPattern("0.00000000"); dfDigits=8; break;
			   }
		   String s = df.format(rounded);
		   return s;
	   }

	
}
