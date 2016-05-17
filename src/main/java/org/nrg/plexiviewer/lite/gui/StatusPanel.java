//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.gui;

import java.awt.*;
public class StatusPanel extends DoubleBuffer  implements Runnable {
		String status;
		Color bgColor;
		Font f;
		int w, h;
		boolean doUpdate=true;
		private Thread DotThread;
		private int length=2;
		private int squareStartAt, currentSquareIndex;
		private int currX, currY;
		private int gap =5;
		private int totalSquares=4;
		private boolean doDraw = true;
		private Color cgColor;	
		Dimension FrameDimension ;
		boolean suspended;

		public StatusPanel(int width, int height) {
			super();
			f = new Font("Dialog",0,10);
			w=width; h=height; 
			setSize(width,height);
			bgColor=Color.white; 
			this.setBackground(bgColor);
			status = "";
			currentSquareIndex =0;
			cgColor = Color.black;
			suspended = false;
		}

		public void reset() {
            status="";
			doUpdate=true;
		}
		
		public Dimension getPreferredSize(){
			return new Dimension (w,h);
		}
		
		public  void setStatus(String msg) {
				if (doUpdate) {
					status = msg;
					start();
					update(this.getGraphics());
				}
		}
		
	public void stop() {
		status="";
		doDraw=false;
		DotThread = null;
		doUpdate=false;
		update(this.getGraphics());
	}		

	public void run() {
			Thread myThread = Thread.currentThread();
			currY=getSize().height/2-length;
			//System.out.println("Run method " + DotThread==null + " " + doDraw);
			  while (DotThread == myThread && doDraw) {
				Graphics g = this.getGraphics();
				if (status!=null && !status.equals("")) {
					if (g!=null) {
						g.setFont(f);
						FontMetrics metrics =g.getFontMetrics();
						if (metrics!=null) {
							squareStartAt = metrics.stringWidth(status)+2;
							update(this.getGraphics());
							currentSquareIndex++;
							try {
							   DotThread.sleep(400);
						   } catch (InterruptedException e){
							   // the VM doesn't want us to sleep anymore,
							   // so get back to work
						   }
						   currentSquareIndex=currentSquareIndex%totalSquares;					
						}
					}
				}else if (status.equals("")) {
					update(this.getGraphics());						
				}
			   }
		 }
	
		public synchronized void start() {
			if (doUpdate) {
				if (DotThread==null) {
					DotThread = new Thread(this);
					doDraw=true;
					DotThread.start();
				}	
			}
		}  
		
		public void paintBuffer(Graphics g) {
			//System.out.println("paintBuffer method " + doDraw + " " + status);
			if (doUpdate==false || doDraw==false || status.equals("")) {
				clearSquares();
			}else {
				g.setColor(cgColor);
				g.setFont(f);
				g.drawString(status,0,getSize().height/2);
				drawSquare(g);
			}
		}

	public void drawSquare(Graphics cg) {
		int xCenter;
		Color c = cg.getColor();
		for (int i=0; i<currentSquareIndex;i++) {
			xCenter = squareStartAt + i*gap;
			cg.setColor(cgColor);
			cg.fillRect(xCenter,currY,length,length);			
		}
		cg.setColor(c);			
	}

	public void clearSquares() {
		Graphics cg = this.getGraphics();
		if (cg!=null) {
			int xCenter;
			Color c = cg.getColor();
			cg.setColor(bgColor);
			for (int i=0; i<currentSquareIndex;i++) {
				xCenter = squareStartAt + i*gap;
				cg.fillRect(xCenter,currY,length,length);			
			}
			cg.setColor(c);			
		}
	}

	public void drawSquare(Graphics cg, int xCenter, int yCenter, boolean fill) {
			if (fill)
				cg.fillRect(xCenter,yCenter,length,length);			
			else 
				cg.drawRect(xCenter,yCenter, length,length);
	}
		
		public static void main(String[] args) {
			Frame f = new Frame();
			StatusPanel s = new StatusPanel(100,200);
			s.setStatus("Mohana");
			f.add(s);
			f.show();
		}
}
