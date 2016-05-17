//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.lite.gui;
import java.awt.*;
import java.awt.event.*;

public class PlexiTextWindow extends Frame implements ActionListener, FocusListener {

	private PlexiTextPanel textPanel;

	/**
	Opens a new single-column text window.
	@param title    the title of the window
	@param str      the text initially displayed in the window
	@param width    the width of the window in pixels
	@param height   the height of the window in pixels
	*/
	public PlexiTextWindow(String title, String data, int width, int height) {
		this(title, "", data, width, height);
	}

	/**
	Opens a new multi-column text window.
	@param title    the title of the window
	@param headings the tab-delimited column headings
	@param data     the text initially displayed in the window
	@param width    the width of the window in pixels
	@param height   the height of the window in pixels
	*/
	public PlexiTextWindow(String title, String headings, String data, int width, int height) {
		super(title);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		textPanel = new PlexiTextPanel(title);
		textPanel.setTitle(title);
		add("Center", textPanel);
		textPanel.setColumnHeadings(headings);
		textPanel.append(data);
		addFocusListener(this);
		addMenuBar();
		setSize(width, height);
		show();
	}

    
	void addMenuBar() {
		MenuBar mb = new MenuBar();
		Menu m = new Menu("File");
		MenuItem m1 =new MenuItem("Save As..."/*, new MenuShortcut(KeyEvent.VK_S)*/);
		m.add(m1);
		m.addActionListener(this);
		mb.add(m);
		setMenuBar(mb);
	}

	/**
	Adds one or lines of text to the window.
	@param text     The text to be appended. Multiple
					lines should be separated by \n.
	*/
	public void append(String text) {
		textPanel.append(text);
	}
    
	/** Set the font that will be used to display the text. */
	public void setFont(Font font) {
		super.setFont(font);
		textPanel.setFont(font);
	}
  
	    
	/** Returns a reference to this TextWindow's TextPanel. */
	public PlexiTextPanel getTextPanel() {
		return textPanel;
	}

	

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		textPanel.doCommand(cmd);
	}

	public void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		int id = e.getID();
		if (id==WindowEvent.WINDOW_CLOSING)
			close();    
	}

	public void close() {
		setVisible(false);
		dispose();
		textPanel.flush();
	}
    
	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {}
	
}
