//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved

package org.nrg.plexiviewer.lite.gui;

import java.awt.*;

public class ProgressBar extends Canvas {

	private int canvasWidth, canvasHeight;
	private int x, y, width, height;
	private double percent;
	private long startTime;
	private int count;
	private boolean showBar;
	private boolean negativeProgress;
	private static boolean autoHide;
    
	private Color barColor = new Color(0,102,102);
	//private Color fillColor = new Color(204,204,255);
	private Color fillColor = Color.white;
	//private Color backgroundColor = new Color(220,220,220);
	private Color backgroundColor = Color.white; 
	private Color frameBrighter = backgroundColor.brighter();
	private Color frameDarker = backgroundColor.darker();

	/** This constructor is called once by ImageJ at startup. */
	public ProgressBar(int canvasWidth, int canvasHeight) {
		super();
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		x = 3;
		y = 5;
		width = canvasWidth - 8;
		height = canvasHeight - 7;
		showBar = false;
		negativeProgress = false;
		count = 0;
		percent = 0.0;
	}
        
	void fill3DRect(Graphics g, int x, int y, int width, int height) {
		try {
			g.setColor(fillColor);
			g.fillRect(x+1, y+1, width-2, height-2);
			g.setColor(frameDarker);
			g.drawLine(x, y, x, y+height);
			g.drawLine(x+1, y, x+width-1, y);
			g.setColor(frameBrighter);
			g.drawLine(x+1, y+height, x+width, y+height);
			g.drawLine(x+width, y, x+width, y+height-1);
		}catch(NullPointerException ne) {
			
		}
	}    

	/** Updates the progress bar, where the length of the bar is set to
		(<code>currentValue+1)/finalValue</code> of the maximum bar length.
		The bar is erased if <code>currentValue&gt;=finalValue</code>. 
		 */
	public void show(int currentValue, int finalValue) {
		if (currentValue>=finalValue)
			showBar = false;
		else {
			percent = Math.min((currentValue+1)/(double)finalValue, 1.0);
			showBar = true;
		}
		update(this.getGraphics());
	}

	/** Updates the progress bar. It is not displayed if
		the time between the first and second calls to 'show'
		is less than 30 milliseconds. It is erased when show
		is passed a percent value >= 1.0. */
	public void show(double percent) {
		count++;
		if (count==1) {
			//ij.IJ.log("");
			//ij.IJ.log("1st call");
			startTime = System.currentTimeMillis();
			showBar = false;
		}
		else if (count==2) {
			long time2 = System.currentTimeMillis();
			if ((time2 - startTime)>=0)
				showBar = true;
		}
 		negativeProgress = percent<this.percent;
		this.percent = percent;
		if (percent>=1.0) {
			//ij.IJ.log("total calls: "+count);
			count = 0;
			percent = 0.0;
			showBar = false;
			update(this.getGraphics());
		} else if (showBar)
			update(this.getGraphics());
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		if (showBar) {
			fill3DRect(g, x-1, y-1, width+1, height+1);
			drawBar(g);
		} else {
			if (g!=null) {
				g.setColor(backgroundColor);
				g.fillRect(0, 0, canvasWidth, canvasHeight);
			}
		}
	}

	void drawBar(Graphics g) {
		if (percent<0.0)
			percent = 0.0;
		int barEnd = (int)(width*percent);
		if (negativeProgress) {
			g.setColor(fillColor);
			g.fillRect(barEnd+2, y, width-barEnd, height);
		} else {
			g.setColor(barColor);
			g.fillRect(x, y, barEnd, height);
		}
	}
    
	public Dimension getPreferredSize() {
		return new Dimension(canvasWidth, canvasHeight);
	}
	
	public void hideBar() {
		showBar=false;
	}
}