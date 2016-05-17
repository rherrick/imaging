//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.ui;

import java.awt.*;

import ij.IJ;
import ij.ImageJ;
import ij.gui.*;

import java.awt.event.*;
import java.awt.image.*;

import org.nrg.plexiviewer.lite.gui.PlexiAdjusterWindow;
import org.nrg.plexiviewer.lite.image.PlexiMedianCut;
import org.nrg.plexiviewer.lite.io.PlexiFileSaver;
import org.nrg.plexiviewer.lite.manager.PlexiManager;
import org.nrg.plexiviewer.lite.gui.PlexiFileInfo;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.gui.PlexiPrinter;

public class PlexiMenuBar {
	
	private ImageWindow parentListener;
	private UserSelection uselection;
	private String host;
	private boolean showCrossHair;
	private CheckboxMenuItem showCrossHairMI ;
	private CheckboxMenuItem rulerMI ;
	private CheckboxMenuItem markRightMI ;
	private CheckboxMenuItem enablePlexiInteractiveKeys ;
	private MenuBar mb;
	ItemListener repaintCanvasItemListener;
	ItemListener enablePlexiInteractiveKeysItemListener;

	ActionListener infoActionListener;
	ActionListener adjustBCActionListener; 
	ActionListener printActionListener;
	ActionListener saveActionListener;
	ActionListener ijActionListener;
	private static boolean imageJShowing = false;
	
	public PlexiMenuBar(ImageWindow parent, String host, UserSelection u) {
		parentListener = parent;
		uselection = u;
		this.host = host;
		mb = new MenuBar();
		init();
		//Use the helper method makeMenuItem
		// for making the menu items and registering
		// their listener.
	}
	
	public PlexiMenuBar(Frame f) {
		
	}

	public void clear() {
			Menu m = mb.getMenu(0);
			//File Menu
			m.getItem(0).removeActionListener(infoActionListener);
			m.getItem(1).removeActionListener(printActionListener);
			m.getItem(2).removeActionListener(saveActionListener);
			//View Menu
			m = mb.getMenu(1);
			m.getItem(0).removeActionListener(ijActionListener);
			m.getItem(1).removeActionListener(adjustBCActionListener);
	}
	
	public MenuBar getMenuBar() {
		
		return mb;
	}

	void init() {
		 repaintCanvasItemListener = new ItemListener() {
				public void itemStateChanged( ItemEvent itemEvent ) {
					if (parentListener!=null)
						{
							parentListener.getCanvas().repaint();
						}
				}
			};

			enablePlexiInteractiveKeysItemListener = new ItemListener() {
				public void itemStateChanged( ItemEvent itemEvent ) {
					PlexiManager.controlKeys = !PlexiManager.controlKeys;
				}
			};

			
		infoActionListener = new ActionListener() {
				public void actionPerformed( ActionEvent actionEvent ) {
					if (parentListener!=null)
						{
							PlexiFileInfo info = new PlexiFileInfo(parentListener.getImagePlus(),host,uselection);
							info.show();
						}
				}
			};
		adjustBCActionListener =new  ActionListener() {
				public void actionPerformed( ActionEvent actionEvent ) {
					if (parentListener!=null) {
						PlexiAdjusterWindow.GetInstance().display();
					}	
				}
		};
		
		ijActionListener =new  ActionListener() {
			public void actionPerformed( ActionEvent actionEvent ) {
				if (!imageJShowing) {
					 final ImageJ ij = IJ.getInstance();
				     if (ij == null || !ij.quitting()) {	// initialize IJ and make a window
				    	 new ImageJ(PlexiManager.getApplet(), ImageJ.EMBEDDED).exitWhenQuitting(false);
				    	 imageJShowing = true;
				    }else {
				       ij.setVisible(true);	
				       imageJShowing = true;
				    }
				}else {
					final ImageJ ij = IJ.getInstance();
					if (ij != null) {
						if (!ij.isShowing()) {
							ij.setVisible(true);
						}
						ij.toFront();
					}
				}
					 
			}
		};

		printActionListener = new ActionListener() {
				public void actionPerformed( ActionEvent actionEvent ) {
					if (parentListener!=null) {
						Image fileImage = createImage(parentListener);
						PlexiPrinter printer = new PlexiPrinter();
						String title = parentListener.getImagePlus().getTitle(); 
						if (uselection.getDisplay().equalsIgnoreCase("Stack"))
							title += ":Slice " + parentListener.getImagePlus().getCurrentSlice();
						printer.print(fileImage,title);
					}	
				}
		};
		saveActionListener = new ActionListener() {
				public void actionPerformed( ActionEvent actionEvent ) {
					if (parentListener!=null) {
						Image fileImage = createImage(parentListener);
						int width = fileImage.getWidth(null);
						int height = fileImage.getHeight(null);
					
						PixelGrabber pg = new PixelGrabber(fileImage, 0, 0, width, height, false);
						 try {
									 pg.grabPixels();
						 } catch (InterruptedException e) {
						 }
						int [] pixels = (int[]) pg.getPixels(); 
						PlexiMedianCut mc = new PlexiMedianCut(pixels, width, height);
						Image image8 = mc.convert(255);
						//new ShowImage(image8,"8 bit Color");
					   // write it out in the format you want
					   PlexiFileSaver fs =  new PlexiFileSaver(image8);
						String fileName = parentListener.getImagePlus().getTitle();
						int index = fileName.lastIndexOf(":");
						fileName =fileName.substring(0,index+2) +  parentListener.getImagePlus().getCurrentSlice();
						fileName = fileName.replace(':','_');
						fileName = fileName.replace(" ","");
						fs.setImageFileName(fileName);
				  	
						fs.saveImageAsGif();
					  fileImage.flush();
					  fileImage=null;
					  mc=null;
					  System.gc();
					}	
				}
			};
		Menu file = new Menu("File");

		MenuItem info = new MenuItem("Info", new MenuShortcut(KeyEvent.VK_I));
		info.addActionListener(infoActionListener);
		file.add(info);
		
		MenuItem print = new MenuItem("Print", new MenuShortcut(KeyEvent.VK_P));
		print.addActionListener(printActionListener);
		file.add(print);
		
		MenuItem snapShot = new MenuItem("SnapShot",  new MenuShortcut(KeyEvent.VK_S));
		snapShot.addActionListener(saveActionListener);		
		file.add(snapShot);
				
		
		Menu view = new Menu("View");
		MenuItem ij = new MenuItem("ImageJ",  new MenuShortcut(KeyEvent.VK_I));
		ij.addActionListener(ijActionListener);
		view.add(ij);		

		MenuItem bc = new MenuItem("Brightness/Contrast",  new MenuShortcut(KeyEvent.VK_B));
		bc.addActionListener(adjustBCActionListener);
		view.add(bc);		
		view.addSeparator();		
		showCrossHairMI = new CheckboxMenuItem("CrossHair", true);
		showCrossHairMI.addItemListener(repaintCanvasItemListener);
		showCrossHairMI.setState(true);
		view.add(showCrossHairMI);		
		
		rulerMI = new CheckboxMenuItem("Ruler", true);
		rulerMI.addItemListener(repaintCanvasItemListener);
		rulerMI.setState(true);
		view.add(rulerMI);	

		markRightMI = new CheckboxMenuItem("R/L Marker", true);
		markRightMI.addItemListener(repaintCanvasItemListener);
		markRightMI.setState(true);
		view.add(markRightMI);	

		enablePlexiInteractiveKeys = new CheckboxMenuItem("ImageJ controls Mouse mvmt", false);
		enablePlexiInteractiveKeys.addItemListener(enablePlexiInteractiveKeysItemListener);
		enablePlexiInteractiveKeys.setState(false);
		view.add(enablePlexiInteractiveKeys);	

		mb.add(file);
		mb.add(view);
	}
	public Image createImage(ImageWindow win) {
		win.addNotify();
		ImageCanvas can = win.getCanvas();
		Rectangle srcRect = can.getSrcRect();
		int width = srcRect.width;
		int height = srcRect.height;
		Image fileImage = parentListener.createImage((int)(width*can.getMagnification()),(int)(height*can.getMagnification()));
				
		Graphics g = fileImage.getGraphics();
		//write to the image
		can.paint(g);
		//dispose of the graphics content
		g.dispose();

		return fileImage;		
	}
	
	public boolean showCrossHair() {
		return showCrossHairMI.getState(); 
	}

	public boolean showRuler() {
		return rulerMI.getState();
	}
	
	public boolean markRight() {
		return markRightMI.getState();
	}
	
	
}
