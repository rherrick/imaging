//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.gui;

import java.awt.*;
import java.awt.event.*;

public class PlexiMessagePanel extends java.awt.Panel {
		StatusPanel statusPanel;
		int width, height;
	 
	   public PlexiMessagePanel(int w, int h) {
	   		  super();
	   		  width = w;
	   		  height = h;
			  initAddComponents();
	   }
	 
		  private void initAddComponents() {
				statusPanel = new StatusPanel(width,height);
	  			add(statusPanel);
		   }
    
    		public void showProgress(int currentValue) {
    		}

			public synchronized void setMessage(String d) {
				statusPanel.setStatus(d);
			}
    
			public Dimension getPreferredSize(){
				return new Dimension (width,height);
			}
			
			public void clear() {
				statusPanel.reset();
			}
			public Insets getInsets() {
				return new Insets(0,0,0,0);
			}
	
			public void finish() {
				statusPanel.stop();
			}

			public synchronized void resetMessages() {
				statusPanel.setStatus("");
				statusPanel.stop();
			}
			
			/**
			 * @param args
			 */
			public static void main(String[] args) {
				final Frame f = new Frame();
				f.setSize(new Dimension(220,100));
				final PlexiMessagePanel s = new PlexiMessagePanel(210,30);
				s.setMessage("");
				f.add(s);
				//f.pack();
				f.setVisible(true);
				System.out.println("Frame Size is " + f.getSize());
				s.setMessage("Building requested Image");
				try {			
					Thread.sleep(900);		   
				}catch(Exception e){}
				//s.resetMessages();
				//s.showProgress(200);
				f.addWindowListener(new WindowAdapter() {
					 public void windowClosing(WindowEvent evt) {
					 	s.resetMessages();
						f.dispose();
					 }
				  });
			}
		
}
