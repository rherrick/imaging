//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Reader;

import java.io.*;

public class ReadFileContents {

	  /**
	  * Fetch the entire contents of a text file, and return it in a String.
	  * This style of implementation does not throw Exceptions to the caller.
	  *
	  * @param aFile is a file which already exists and can be read.
	  */
	  static public String getContents(File aFile) {
		
		StringBuffer contents = new StringBuffer();
        contents.append("");	
		//declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
		  //use buffering
		  //this implementation reads one line at a time
		  input = new BufferedReader( new FileReader(aFile) );
		  String line = null; //not declared within while loop
		  while (( line = input.readLine()) != null){
			contents.append(line);
			contents.append(System.getProperty("line.separator"));
		  }
		}
		catch (FileNotFoundException ex) {
		  System.out.println("ReadFileContents::getContents coulnt find file " + aFile.getAbsolutePath());
		}
		catch (IOException ex){
		  ex.printStackTrace();
		}
		finally {
		  try {
			if (input!= null) {
			  //flush and close both "input" and its underlying FileReader
			  input.close();
			}
		  }
		  catch (IOException ex) {
			ex.printStackTrace();
            
		  }
		}
		return contents.toString();
	  }

}
