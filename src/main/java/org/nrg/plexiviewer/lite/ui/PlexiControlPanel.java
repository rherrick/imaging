/*
 * org.nrg.plexiViewer.lite.ui.PlexiControlPanel
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.lite.ui;

/**
 * @author Mohana
 *
 */
import ij.IJ;
import ij.ImageJ;
import org.nrg.plexiviewer.lite.UserInterfaceContents;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.gui.PlexiMessagePanel;
import org.nrg.plexiviewer.lite.manager.PlexiManager;
import org.nrg.plexiviewer.lite.utils.BrowserVersionInfo;
import org.nrg.plexiviewer.lite.utils.PlexiSubscriberClientProxy;
import org.nrg.xdat.bean.XnatAbstractresourceBean;

import java.applet.AppletContext;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

public class PlexiControlPanel extends Panel implements ItemListener {
	UserInterfaceContents contents;
	PlexiManager plexiManager;
	boolean chooseScanNo;
	private String startDisplayWith;
	private String startDataWith; 
	int count =0;
	long totalMemory;
	long freeMemory;
	/**
	 * Constructor 
	 */
	public PlexiControlPanel(UserInterfaceContents c, AppletContext appletContext) {
	   contents=c;
	   chooseScanNo=false;	
	   startDisplayWith = null;
	   plexiManager = new PlexiManager(appletContext);
	   initComponents(); 
	 }
	
	public void initComponents() {
		try { 
		ImageJ ij = IJ.getInstance();
	     if (ij == null || !ij.quitting()) {	// initialize IJ and make a window
	    	 new ImageJ(PlexiManager.getApplet(), ImageJ.EMBEDDED).exitWhenQuitting(false);
	    	 ij = IJ.getInstance();
	    	 ij.setVisible(false);
	     }
		}catch(Exception e) {
			System.out.println("ImageJ Init threw and exception ");
		}
		setLayout(new BorderLayout(0,0));
		setBackground(Color.white);
		

		java.awt.GridBagConstraints gridBagConstraints1;
		java.awt.GridBagConstraints gbc2;
		java.awt.Label label;
 
		optionsPanel = new Panel();
			   
		typeChoice = new java.awt.Choice();
			   
		viewChoice = new java.awt.Choice();
		fileChoice = new ChoiceWithObjects();
		dataChoice = new java.awt.Choice();
		dataChoice.addItemListener(this);
			   
		displayChoice = new java.awt.Choice();
		radCheckbox = new java.awt.Checkbox();
		goButton = new java.awt.Button();
					   
        
		optionsPanel.setLayout(new java.awt.GridBagLayout());
		optionsPanel.setFont(new java.awt.Font("Dialog", 0, 11));
		optionsPanel.setBackground(this.background);
		optionsPanel.setForeground(this.foreground);
	   


 label = new java.awt.Label();
	 label.setFont(new java.awt.Font("Dialog", 0, 11));
	 label.setBackground(this.background);
	 label.setForeground(this.foreground);
	 label.setText("Data  "); //RAW/PROCESSED/ASSESSOR
	 gbc2 = new GridBagConstraints();
	 gbc2.gridx = 0;
	 gbc2.gridy = 0;
	 gbc2.anchor = GridBagConstraints.WEST;
	 gbc2.insets = new java.awt.Insets(0,0,0,0);
	 gbc2.weightx = 0.9;
	 gbc2.weighty = 0.9;
	 optionsPanel.add(label,gbc2);
	
	 dataChoice.setFont(new java.awt.Font("Dialog", 0, 11));
	 dataChoice.setBackground(this.background);
	 dataChoice.setForeground(this.foreground);
	 Vector  dataChoicesVector = contents.getValuesForData();
	 if (dataChoicesVector.size() != 0) {
		 for (Enumeration e = dataChoicesVector.elements(); e.hasMoreElements(); ) {
			 dataChoice.add((String)e.nextElement());
		 }
	 }else {
		 dataChoice.add("No images");
		 disableAll = true;
	 }
	 
	 GridBagConstraints dataGbc = new GridBagConstraints();
	 dataGbc.gridx = 1;
	 dataGbc.gridy = 0;
	 dataGbc.anchor = GridBagConstraints.WEST;
	 dataGbc.insets = new java.awt.Insets(0,0,0,0);
	 dataGbc.weightx = 0.9;
	 dataGbc.weighty = 0.9;
	 dataGbc.fill = GridBagConstraints.HORIZONTAL;
	 dataChoice.addItemListener(this); 	
	 optionsPanel.add(dataChoice,dataGbc);

 
 label = new java.awt.Label();
 
 		 label.setFont(new java.awt.Font("Dialog", 0, 11));
		 label.setBackground(this.background);
		 label.setForeground(this.foreground);
		 label.setText("Type"); //old Scan
		 gbc2 = new GridBagConstraints();
		 gbc2.gridx = 0;
		 gbc2.gridy = 1;
		 gbc2.anchor = GridBagConstraints.WEST;
		 gbc2.insets = new java.awt.Insets(0,0,0,0);
		 gbc2.weightx = 0.9;
		 gbc2.weighty = 0.9;
		 optionsPanel.add(label,gbc2);
		        
		 typeChoice.setFont(new java.awt.Font("Dialog", 0, 11));
		 typeChoice.setBackground(this.background);
		 typeChoice.setForeground(this.foreground);
		 typeChoice.addItemListener(this);
		 GridBagConstraints typeGbc = new GridBagConstraints();
		 typeGbc.gridx = 1;
		 typeGbc.gridy = 1;
		 typeGbc.anchor = GridBagConstraints.WEST;
		 typeGbc.insets = new java.awt.Insets(0,0,0,0);
		 typeGbc.weightx = 0.9;
		 typeGbc.weighty = 0.9;
		 typeGbc.fill = GridBagConstraints.HORIZONTAL;
		 optionsPanel.add(typeChoice,typeGbc);

 label = new java.awt.Label();

        
 				scanNoORFilelabel = new java.awt.Label();
				scanNoORFilelabel.setFont(new java.awt.Font("Dialog", 0, 11));
				scanNoORFilelabel.setBackground(this.background);
				scanNoORFilelabel.setForeground(this.foreground);
				//scanNoORFilelabel.setEnabled(false);
				scanNoORFilelabel.setText("Image");
				gbc2 = new GridBagConstraints();
				gbc2.gridx = 0;
				gbc2.gridy = 2;
				gbc2.anchor = GridBagConstraints.WEST;
				gbc2.insets = new java.awt.Insets(0,0,0,0);
				gbc2.weightx = 0.9;
				gbc2.weighty = 0.9;
				optionsPanel.add(scanNoORFilelabel,gbc2);
				 fileChoice.setFont(new java.awt.Font("Dialog", 0, 11));
				 fileChoice.setBackground(this.background);
				 fileChoice.setForeground(this.foreground);
				 GridBagConstraints scanNoGbc = new GridBagConstraints();
				 scanNoGbc.gridx = 1;
				 scanNoGbc.gridy = 2;
				 scanNoGbc.anchor = GridBagConstraints.WEST;
				 scanNoGbc.insets = new java.awt.Insets(0,0,0,0);
				 scanNoGbc.weightx = 0.9;
				 scanNoGbc.weighty = 0.9;
				 scanNoGbc.fill = GridBagConstraints.HORIZONTAL;
				 fileChoice.addItemListener(this);
				 //fileChoice.setEnabled(false);
				 optionsPanel.add(fileChoice,scanNoGbc);
        
label = new java.awt.Label();
		label.setFont(new java.awt.Font("Dialog", 0, 11));
		label.setBackground(this.background);
		label.setForeground(this.foreground);
		label.setText("View  ");
		gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 4;
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.insets = new java.awt.Insets(0,0,0,0);
		gbc2.weightx = 0.9;
		gbc2.weighty = 0.9;
		optionsPanel.add(label,gbc2);
        
		viewChoice.setFont(new java.awt.Font("Dialog", 0, 11));
		viewChoice.setBackground(this.background);
		viewChoice.setForeground(this.foreground);
		GridBagConstraints viewGbc = new GridBagConstraints();
		viewGbc.gridx = 1;
		viewGbc.gridy = 4;
		viewGbc.anchor = GridBagConstraints.WEST;
		viewGbc.insets = new java.awt.Insets(0,0,0,0);
		viewGbc.weightx = 0.9;
		viewGbc.weighty = 0.9;
		viewGbc.fill = GridBagConstraints.HORIZONTAL;
		viewChoice.addItemListener(this);
		optionsPanel.add(viewChoice,viewGbc);
        
		label = new java.awt.Label();
		label.setFont(new java.awt.Font("Dialog", 0, 11));
        label.setBackground(this.background);
		label.setForeground(this.foreground);
		label.setText("Display");
		gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 5;
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.insets = new java.awt.Insets(0,0,0,0);
		gbc2.weightx = 0.9;
		gbc2.weighty = 0.9;
		optionsPanel.add(label,gbc2);
        
		displayChoice.setFont(new java.awt.Font("Dialog", 0, 11));
		displayChoice.setBackground(this.background);
		displayChoice.setForeground(this.foreground);
		displayChoice.add("Stack          ");
		displayChoice.add("Montage        ");
		gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 5;
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.insets = new java.awt.Insets(0,0,0,0);
		gbc2.weightx = 0.9;
		gbc2.weighty = 0.9;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		displayChoice.addItemListener(this);
		optionsPanel.add(displayChoice,gbc2);
        
		radCheckbox.setState(false);
		radCheckbox.setBackground(this.background);
		radCheckbox.setFont(new java.awt.Font("Dialog", 0, 11));
		radCheckbox.setForeground(this.foreground);
		radCheckbox.setLabel("radiologic");
		gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 6;
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.insets = new java.awt.Insets(0,0,0,0);
		gbc2.weightx = 0.9;
		gbc2.weighty = 0.9;
		optionsPanel.add(radCheckbox,gbc2);
        
		goButton.setFont(new java.awt.Font("Dialog", 0, 11));
		goButton.setLabel("     GO     ");
		goButton.setForeground(this.foreground);
		goButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				goButtonClicked(evt);
			}
		}
		);
		
		gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 6;
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.insets = new java.awt.Insets(3,0,0,0);
		gbc2.weightx = 0.9;
		gbc2.weighty = 0.9;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.add(goButton,gbc2);

		msgPanel = new PlexiMessagePanel(210,30);
		msgPanel.setMessage("");

		gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 7;
		gbc2.anchor = GridBagConstraints.WEST;
		gbc2.insets = new java.awt.Insets(0,0,0,0);
		gbc2.weightx = 0.9;
		gbc2.weighty = 0.9;
		gbc2.gridwidth = 2;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		optionsPanel.add(msgPanel,gbc2); 

		add(optionsPanel, BorderLayout.NORTH);

 initChoices();
  
	}

	void initFiles(Vector files) {
		fileChoice.removeAll();
		 for (Enumeration e = files.elements(); e.hasMoreElements(); ) {
			 fileChoice.add((GenericIdentifier)e.nextElement());
			 
		 }

	}
	 
	public void itemStateChanged(ItemEvent e) {
		msgPanel.setMessage("");
		disableImageView(false);
			if (e.getSource()== typeChoice) {	
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setFileChoice();
					fileChoice.setEnabled(true);
				}
			}else if (e.getSource()== dataChoice) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String item = (String) e.getItem();
					item = item.trim();
					setTypeChoice(item);
					setFileChoice();
				}
			}else if (e.getSource()== fileChoice) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					GenericIdentifier item = (GenericIdentifier) fileChoice.getSelectedObject();
					setOrientationChoice((XnatAbstractresourceBean)item.getValue());
					viewChoice.setEnabled(true);
				}
			}
	}	

	private void setVisibility() {
	}

	private void setFileChoice() {
		String typeStr = typeChoice.getSelectedItem();
		String dataStr = dataChoice.getSelectedItem();
		Vector files = contents.getFileList(dataStr,typeStr);
		if (files.size()>0) {
			initFiles(files);
			GenericIdentifier item = (GenericIdentifier) fileChoice.getSelectedObject();
			setOrientationChoice((XnatAbstractresourceBean)item.getValue());
		}else {
			fileChoice.removeAll();
			viewChoice.removeAll();
			fileChoice.add("No Images available");
			//msgPanel.setMessage("There are no images viewable.");
			disableImageView(true);
		}
	}
	
	private void disableImageView(boolean notviewable) {
		 goButton.setEnabled(!notviewable);
	}

/*	private void setScanChoice(String item) {
		scanNoDisplayStatus = false;
		dataChoice.removeAll();
		Vector dataLinked = contents.getLinkedDrop(item);
		if (dataLinked!=null) {
			for (int i=0; i<dataLinked.size(); i++) {
				dataChoice.add((String)dataLinked.elementAt(i));
			}
		}
		if (startDisplayWith!=null) {
			typeChoice.select(item);
			dataChoice.select(startDisplayWith); //relying on select method to set to 0 if not found
			startDisplayWith = null;		
		}
		//System.out.println("Scan Type Choice is " + scanTypeChoice.getSelectedItem());
		if (dataChoice.getSelectedItem()!=null) 
			//setDataChoice(StringUtils.peelSpace(dataChoice.getSelectedItem()));
			setDataChoice(dataChoice.getSelectedItem().trim());
		//System.out.println("setScanChoice done");
        setOrientationChoice(item);
	}*/

    private void setOrientationChoice(XnatAbstractresourceBean file) {
        viewChoice.removeAll();
        Vector ori = contents.getOrientations(file);
        if (ori!=null && ori.size() > 0) {
        	for (int i=0; i<ori.size(); i++) {
                viewChoice.add((String)ori.elementAt(i));
            }
        }else {
        	
        }
        viewChoice.setEnabled(true);
    }

    
/*	private void setDataChoice(String item) {
			scanNoDisplayStatus = false;
			chooseScanNo = false;
			fileChoice.removeAll();
			int scanIndex = typeChoice.getSelectedIndex();
			int dataIndex = dataChoice.getSelectedIndex();
			Vector scanNo = contents.getScanNoList(scanIndex);
			if (scanNo!=null) {
				for (int i=0; i<scanNo.size(); i++) {
					fileChoice.add(new GenericIdentifier(new Integer(i),(String)scanNo.elementAt(i)));
				}
			}
			if (contents.canChooseFiles(scanIndex,dataIndex)) {
				scanNoORFilelabel.setText("File");
				scanNoDisplayStatus = true;
			}else if (contents.canChooseScans(scanIndex,dataIndex)) {
				scanNoORFilelabel.setText("Run");
				scanNoDisplayStatus = true;
				chooseScanNo = true;
			}else {
				scanNoORFilelabel.setText("Run");
			}
            setOrientationChoice(item);
	} */
	 
	private void goButtonClicked(java.awt.event.MouseEvent evt) {
 			BrowserVersionInfo bInfo = new BrowserVersionInfo();
 			msgPanel.clear();
			/*if (bInfo.isMac() &&  bInfo.getMRJVersion()!=null && bInfo.getMRJVersion().equalsIgnoreCase("2.2.5")) {
				//msgPanel.setMessage("Please upgrade Java Version to 2.2.6. See Help for how to upgrade");
				return;
			}*/
			long startTime = System.currentTimeMillis();
			Runtime.getRuntime().runFinalization();
			totalMemory = Runtime.getRuntime().totalMemory();
			Runtime.getRuntime().gc();
			freeMemory = Runtime.getRuntime().freeMemory();
			int freeMemPct = (int)(((float)freeMemory/(float)totalMemory)*100);
			System.out.println("Memory Free:: " + freeMemPct  +"%");
			if (freeMemPct<25) {
				msgPanel.setMessage("Low on Memory....Please close some image windows");
				System.out.println("Running low on memory......Please close some image windows");
				return;
			}
			Cursor c = this.getCursor();
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			  int scanIndex = typeChoice.getSelectedIndex();
			  int dataIndex =  dataChoice.getSelectedIndex();
			  String orientation = viewChoice.getSelectedItem().trim().toLowerCase();
			  XnatAbstractresourceBean file =null;
			  if ((GenericIdentifier)fileChoice.getSelectedObject()!=null) 
			  	file = ((XnatAbstractresourceBean)((GenericIdentifier)fileChoice.getSelectedObject()).getValue());
			  String display = displayChoice.getSelectedItem().trim();
			  boolean radiologic = radCheckbox.getState();
 			  UserSelection uselected = new UserSelection();
 			  //uselected.setExptId(contents.getExptId());
 			  uselected.setDataType(dataChoice.getSelectedItem());				  	
			  uselected.setDisplay(display);
			  uselected.setOrientation(orientation.toLowerCase());
			  uselected.setRadiologic(radiologic);
			  uselected.setSessionLabel(contents.getSessionLabel());
			  uselected.setSessionId(contents.getSessionId());
			  uselected.setProject(contents.getProject());
			  uselected.setWindowTitle(contents.getSessionLabel() + ":" + typeChoice.getSelectedItem() + ":" + dataChoice.getSelectedItem() +":" + fileChoice.getSelectedItem()+ ":" + orientation.substring(0,1).toUpperCase()+orientation.substring(1));
			  uselected.setImageViewerClass(contents.getViewerClassName());
			  uselected.setXnatFile(file);
			PlexiSubscriberClientProxy statusProxy = new PlexiSubscriberClientProxy(msgPanel,uselected.toString());
			statusProxy.start();
			int found = plexiManager.show(msgPanel,(UserSelection)uselected.clone());
			this.setCursor(c);
			statusProxy.finish();
			if (found==1) msgPanel.setMessage("");
			//System.out.println("Found is " + found);
			msgPanel.resetMessages();
			long endTime = System.currentTimeMillis();
			System.out.println("Total Time " + (endTime-startTime)/1000 + " s");
		}


/*	public void initItems() {
		//System.out.println("setDisplayChoices begin");
		String item=null;
		boolean found=false;
		Hashtable scans = contents.getAllScans();
		for (Enumeration e=scans.keys();!found && e.hasMoreElements() ;) {
			Integer i = (Integer)e.nextElement();
			DBFSXMLInfo db = (DBFSXMLInfo)scans.get(i);
			if (db.getScanToDisplay().equals(getStartDisplayWith())) {
				item = getStartDisplayWith();
				found = true;
			}else {
				for (int j=0;j<db.getFsxmlmergedInfo().size();j++) {
					FSXMLInfo fs = (FSXMLInfo)db.getFsxmlmergedInfo().elementAt(j);
					if (fs.getDataToDisplay().equals(getStartDisplayWith())) {
						found=true;
						item=db.getScanToDisplay();
					}
				}
			}
		}
		if (item!=null) {
			setScanChoice(item);
			setVisibility();
		}else {
			startDisplayWith =null;	
		}
		//System.out.println("setDisplayChoices done");
	}*/
    
	public void initChoices() {
		String item="";
		if (disableAll) {
			setVisibility();
		}else {	
			if (dataChoice.getItemCount()!=0) {
				item = dataChoice.getItem(0);
				item = item.trim();
				setTypeChoice(item);
				setFileChoice();
			}
			//viewChoice.setEnabled(false);
		}
   }
    
	
   private void setTypeChoice(String dataChoiceStr) {
	   typeChoice.removeAll();
	   Vector values = contents.getTypeValues(dataChoiceStr);
		 for (Enumeration e = values.elements(); e.hasMoreElements(); ) {
			 typeChoice.add((String)e.nextElement());
		 }
	   
   }
   /**
	* @return
	*/
   public String getStartDisplayWith() {
	   return startDisplayWith;
   }

   /**
	* @param string
	*/
   public void setStartDisplayWith(String string) {
	   startDisplayWith = string;
	   //initItems();
   }
   
    
   public Dimension getPreferredSize() {
		return new Dimension(250,220);
   }

	   Color background = Color.white;		  
	   Color farbackground = Color.white;
	   Color foreground = Color.black;
	  
	   private Panel optionsPanel;
//	   private GeneralToolsPanel buttonPanel;
	   private PlexiMessagePanel msgPanel;
	   private Panel uiPanel;
	   private Label scanNoORFilelabel;
	   
	   private java.awt.Choice typeChoice;
	   private ChoiceWithObjects fileChoice;
	   private java.awt.Choice viewChoice;
	
	   private java.awt.Choice dataChoice;
	   private java.awt.Choice displayChoice;
	  
	   private java.awt.Checkbox radCheckbox;
	   private java.awt.Button goButton;
	   private boolean scanNoDisplayStatus=false;
	   private boolean hasUIPanel = false;
	   private boolean disableAll = false;
	   
}

