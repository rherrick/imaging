//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.lite.utils;

public class BrowserVersionInfo {
	boolean isMac;
	boolean isWin;
	String java_version;
	String mrj_version;
	
	public BrowserVersionInfo() {
		String osname = System.getProperty("os.name");
		isWin = osname.startsWith("Windows");
		isMac = !isWin && osname.startsWith("Mac");
	}
	
	public String getMRJVersion() {
		return System.getProperty("mrj.version");	
	}
	
	public String getJavaVersion() {
		return System.getProperty("java.version");
	}
	
	public boolean isMac() {
		return isMac;
	}
	
	public boolean isWin() {
		return isWin;
	}
	
	
}
