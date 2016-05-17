//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.utils;
import java.util.Observable;

public class PlexiPublisher extends Observable {
	private Object msg;
	
	public PlexiPublisher() {
		super();
	}
	
	public void setValue(Object message)
	   {
		  this.msg = message;
		  setChanged();
		  notifyObservers();
	   }

	   public Object getValue()
	   {
		  return msg;
	   }	
		
}
