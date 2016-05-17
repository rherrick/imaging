//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.manager;

import java.util.*;

import org.nrg.plexiviewer.utils.PlexiPublisher;
import org.nrg.plexiviewer.utils.PlexiSubscriberProxy;
public class PlexiStatusPublisherManager {
	
	private Hashtable optionsPublisherHash = null;
	private Hashtable optionsSubscriberHash = null;
	private Hashtable optionsHandledHash = null;
	private static PlexiStatusPublisherManager self = null;
	private PlexiStatusPublisherManager() {
		optionsPublisherHash = new Hashtable();
		optionsSubscriberHash = new Hashtable();
		optionsHandledHash = new Hashtable();
	}
	
	public static PlexiStatusPublisherManager GetInstance() {
		if (self==null)
			self = new PlexiStatusPublisherManager();
		return self;		
	}
	
	public  void createPublisher(String opt) {
		if (optionsPublisherHash==null || optionsPublisherHash.size()==0) {
			optionsPublisherHash.put(opt,new PlexiPublisher());	
		}else if (!optionsPublisherHash.containsKey(opt)) {
				optionsPublisherHash.put(opt,new PlexiPublisher());
		}
	}

	public  void createSubscriber(String opt) {
		if (optionsSubscriberHash==null || optionsSubscriberHash.size()==0) {
			optionsSubscriberHash.put(opt,new PlexiSubscriberProxy(opt));	
		}else if (!optionsSubscriberHash.containsKey(opt)) {
			optionsSubscriberHash.put(opt,new PlexiSubscriberProxy(opt));
		}
		optionsHandledHash.put(opt,new Boolean(false));
	}

		
	public  PlexiPublisher getPublisher(String options) {
		//createPublisher(options);
		return (PlexiPublisher)	optionsPublisherHash.get(options);
	}
	
	public void removePublisher(String opt) {
		if (optionsPublisherHash.containsKey(opt)) 
			optionsPublisherHash.remove(opt);
	}
	
	public  PlexiSubscriberProxy getSubscriberProxy(String opt) {
		PlexiSubscriberProxy rtn = (PlexiSubscriberProxy)optionsSubscriberHash.get(opt);
		return rtn;
	}
	
	public  void setHandled(String opt) {
		if (optionsHandledHash.containsKey(opt))
			optionsHandledHash.remove(opt);
		optionsHandledHash.put(opt,new Boolean(true)); 
	}
	
	public  Object isHandled(String opt) {
		return optionsHandledHash.get(opt);
	}
	
	
	public  void clearHandledStatus() {
		optionsHandledHash = new Hashtable();
	}

	public  void removeSubscriberProxy(String opt) {
		if (optionsSubscriberHash.containsKey(opt)) {
			optionsSubscriberHash.remove(opt);
		}
		if (optionsHandledHash.containsKey(opt))
			optionsHandledHash.remove(opt);
	}
	
	
}
