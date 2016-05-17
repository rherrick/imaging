//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.lite.viewer.MiriadEmSeg;

public class SegVolumeCounter implements Runnable {

	int[] volCounts;
	String sessionId;
	boolean isDone;
	private String host;

	public SegVolumeCounter(String sessionId, String host) {
		this.sessionId=sessionId;
		this.host = host;
		isDone=false;
//		volCounts = new int[140];
		volCounts = new int[1];
	}

	public void setVolumeCounts() {
		Thread vCounter = new Thread(this);
		vCounter.start();
	}

	public void run() {
		getCounts();	
		isDone=true;
	}
    
	private void getCounts() {			
//				String suffix = HTTPDetails.getSuffix("getAsegRegionVolumes");
//				URL populateServlet = null;
//				URLConnection servletConnection;
//				try {
//					suffix+="?sessionId=" + sessionId;
//					populateServlet = HTTPDetails.getURL(host,suffix);
//					servletConnection = populateServlet.openConnection();
//					//Don't use a cached version of URL connection.
//					servletConnection.setUseCaches (false);
//					InputStream is = servletConnection.getInputStream();
//					ObjectInputStream inputStreamFromServlet =  new ObjectInputStream(is);
//					for (int i=0;i<140;i++) 
//						volCounts[i] = inputStreamFromServlet.readInt();
//					inputStreamFromServlet.close();
//					is.close();
//				} catch (MalformedURLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (IOException e2) {
//					// TODO Auto-generated catch block
//					e2.printStackTrace();
//				}
			for (int i=0;i<volCounts.length;i++) 
				volCounts[i] = 0;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public int[] getVolumeCount() {
		return volCounts;
	}

	
}
