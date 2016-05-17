//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.TextArea;

public class PlexiNoFrillsTextWindow extends Frame implements WindowListener {
	String text;
	TextArea textArea;
	public PlexiNoFrillsTextWindow(String title)
	{
		super(title);
		setLayout(new FlowLayout());
		setSize(480, 300);
		textArea = new java.awt.TextArea();
		textArea.setEditable(false);
		add(textArea);
		addWindowListener(this); 
	}	
	
	public void show(String text) {
		this.text = text;
		textArea.setText(text);
		setVisible(true);
		show();
	}
		
	public void windowOpened(WindowEvent we){}

	public void windowClosed(WindowEvent we){}

	public void windowIconified(WindowEvent we){}

	public void windowDeiconified(WindowEvent we){}

	public void windowActivated(WindowEvent we){}

	public void windowDeactivated(WindowEvent we){}

	public void windowClosing(WindowEvent we)
	{
			setVisible(false);
			dispose();
			text=null;
	}
	
	public static void main(String args[]) {
		PlexiNoFrillsTextWindow tw = new PlexiNoFrillsTextWindow("Image Viewer Warning");
		tw.show("Not enough memory available to show the image.\nPlease try increasing the memory (see Viewer Help on how to)");
	}
	
}
