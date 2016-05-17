//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.utils;

import java.util.Observer;
import java.util.Observable;

import org.nrg.plexiviewer.manager.*;

public class PlexiSubscriberProxy  implements  Observer{
	boolean isRegistered = false;
	String options;
	boolean hasUpdate = false;
	Object message;
	
	public PlexiSubscriberProxy(String opt) {
		options=opt;
		register();
	}
	public void update(Observable publisher, Object args) {
		message = ((PlexiPublisher)publisher).getValue();
		hasUpdate = true;						
	}
	
	public void register() {
		if (!isRegistered) {
			PlexiPublisher publisher = PlexiStatusPublisherManager.GetInstance().getPublisher(options);  
			if (publisher!=null) {
				publisher.addObserver(this);
				isRegistered = true;
			}
		}
	}
	
	public boolean hasUpdate() {
		return hasUpdate;
	}
	
	public Object getMessage() {
		return message;
	}
}
