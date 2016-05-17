//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MiriadEmSeg;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import org.nrg.plexiviewer.lite.manager.PlexiManager;

public class ToolPanel extends Panel implements ActionListener, ItemListener {
    
    int currentSelection;
    SmallLabel regionLabel;
    SmallLabel coordsLabel;
	SmallLabel volCountLabel, volCountText;
    SmallLabel msgLabel;
    Panel scalePanel;
    Hashtable rm;
    Hashtable rm2;
    Choice displayChoice;
    
    
    /** Creates new form ToolPanel */
    public ToolPanel() {

        /* BWH EM Segmentation labels*/
    	rm  = new Hashtable(); 
        rm.put("0","0-clear");
        rm.put("1","1-air");
        rm.put("2","2-non-brain");
        rm.put("3","3-brain");
        rm.put("4","4-csf");
        rm.put("5","5-gray");
        rm.put("6","6-white");
        rm.put("7","7-white norm");
        rm.put("8","8-white lesion");
        rm.put("9","9-other gray");
        rm.put("10","10-LAmygdala");
        rm.put("11","11-RAmygdala");
        rm.put("12","12-LAntInsCortex");
        rm.put("13","13-RAntInsCortex");
        rm.put("14","14-LHippocampus");
        rm.put("15","15-RHippocampus");
        rm.put("16","16-LInfTempGyrus");
        rm.put("17","17-RInfTempGyrus");
        rm.put("18","18-LMidTempGyrus");
        rm.put("19","19-RMidTempGyrus");
        rm.put("20","20-LParahippocampus");
        rm.put("21","21-RParahippocampus");
        rm.put("22","22-LPostInsCortex");
        rm.put("23","23-RPostInsCortex");
        rm.put("24","24-LSupTempGyrus");
        rm.put("25","25-RSupTempGyrus");
        rm.put("26","26-LTempLobe");
        rm.put("27","27-RTempLobe");
        rm.put("28","28-LThalamus");
        rm.put("29","29-RThalamus");
               
        rm2 = new Hashtable();
        rm2.put("0-clear","0");
        rm2.put("1-air","1");
        rm2.put("2-non-brain","2");
        rm2.put("3-brain","3");
        rm2.put("4-csf","4");
        rm2.put("5-gray","5");
        rm2.put("6-white","6");
        rm2.put("7-white norm","7");
        rm2.put("8-white lesion","8");
        rm2.put("9-other gray","9");
        rm2.put("10-LAmygdala","10");
        rm2.put("11-RAmygdala","11");
        rm2.put("12-LAntInsCortex","12");
        rm2.put("13-RAntInsCortex","13");
        rm2.put("14-LHippocampus","14");
        rm2.put("15-RHippocampus","15");
        rm2.put("16-LInfTempGyrus","16");
        rm2.put("17-RInfTempGyrus","17");
        rm2.put("18-LMidTempGyrus","18");
        rm2.put("19-RMidTempGyrus","19");
        rm2.put("20-LParahippocampus","20");
        rm2.put("21-RParahippocampus","21");
        rm2.put("22-LPostInsCortex","22");
        rm2.put("23-RPostInsCortex","23");
        rm2.put("24-LSupTempGyrus","24");
        rm2.put("25-RSupTempGyrus","25");
        rm2.put("26-LTempLobe","26");
        rm2.put("27-RTempLobe","27");
        rm2.put("28-LThalamus","28");
        rm2.put("29-RThalamus","29");       
        
        initAddComponents();
    }
    
    private void initAddComponents() {
        
        GridBagConstraints gbc;
        
        setLayout(new java.awt.GridBagLayout());
        setFont(new java.awt.Font("Dialog", 0, 11));
       // setBackground(java.awt.Color.lightGray);
        setForeground(java.awt.Color.black);
        
        coordsLabel = new SmallLabel(90,18);
        coordsLabel.setText("");
        //coordsLabel.setBackground(Color.lightGray);
        gbc = new java.awt.GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx= 0;
        gbc.gridy= 0;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        add(coordsLabel, gbc);
        
        regionLabel = new SmallLabel(90,18);
        regionLabel.setText("");
        //regionLabel.setBackground(Color.lightGray);
        gbc = new java.awt.GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx= 1;
        gbc.gridy= 0;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        add(regionLabel, gbc);

        
        SmallLabel l = new SmallLabel(44, 18);
        l.setAlignment(l.LEFT);
        l.setText("Display:   ");
        //l.setBackground(Color.lightGray);
        gbc = new java.awt.GridBagConstraints();
        gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridheight = 1;
        gbc.gridx= 0;
        gbc.gridy= 1;
        add(l, gbc);
        
        displayChoice = new java.awt.Choice();
        displayChoice.setFont(new java.awt.Font("Dialog", 0, 10));
        //displayChoice.setBackground(Color.lightGray);
        //displayChoice.setForeground(java.awt.Color.black);
        displayChoice.addItem("All Regions");
        for (int i=0; i<140; i++){
            if (rm.containsKey( Integer.toString(i) ) ){
                displayChoice.addItem((String)rm.get(Integer.toString(i)) );
            }
        }
        displayChoice.addItemListener(this);
        gbc = new java.awt.GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx= 1;
        gbc.gridy= 1;
        add(displayChoice,gbc);

		volCountText = new SmallLabel(44, 18);
		volCountText.setAlignment(l.LEFT);
		volCountText.setText("Vol.Cnt: ");
			   //l.setBackground(Color.lightGray);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx= 0;
		gbc.gridy= 2;
		add(volCountText, gbc);

		volCountLabel = new SmallLabel(88,18);
		volCountLabel.setText("");
		//regionLabel.setBackground(Color.lightGray);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx= 1;
		gbc.gridy= 2;
		add(volCountLabel, gbc);

		msgLabel = new SmallLabel(200,18);
		msgLabel.setText("");
		//regionLabel.setBackground(Color.lightGray);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx= 0;
		gbc.gridy= 3;
		add(msgLabel, gbc);
		System.out.println("Seg::ToolPanel Constructor called " + PlexiManager.getSetting("SegImage.regionID"));
		System.out.println("Seg::ToolPanel Selected Index " + displayChoice.getSelectedIndex() + "   " +  displayChoice.getSelectedItem());

    }
    

	public void setVolumeCount(String r) {
		volCountLabel.setText(r);
	}


    public void setRegion(int r) {
        String s = (String) rm.get(Integer.toString(r)) ;
        //if (r>=0 && r<140) displayChoice.select(r);
        regionLabel.setText(s);
		SegCanvas c = (SegCanvas) ((SegStackWindow)this.getParent()).getCanvas();
		this.setVolumeCount(c.getVolumeCount(r));
		/*SegStackWindow w = (SegStackWindow)this.getParent();
		SegCanvas c = (SegCanvas) w.getCanvas();
		 this.setVolumeCount(c.getVolumeCount(new Integer(r))); */
        //System.out.println("setRegion:: " + regionLabel.getText());
    }
    
    public void setCoords(int x, int y, int z) {
        String s = Integer.toString(x)  +  ", " +  Integer.toString(y)  +  ", " + Integer.toString(z) + ": ";
        coordsLabel.setText(s );
		//System.out.println("setCoords:: " + coordsLabel.getText() +  ": Region : " + regionLabel.getText());
    }
    
    public Insets getInsets() {
        //Insets insets = super.getInsets();
        return new Insets(0,0,0,0);
    }
    public void actionPerformed(java.awt.event.ActionEvent e) {
        //String command = e.getActionCommand();
    }
    
    public int getCurrentSelection() {
        return currentSelection;
    }
    
    public void setRegionName(int regionId) {
    	int currentRegionId = ((Integer)PlexiManager.getSetting("SegImage.regionID")).intValue();
    	System.out.println("ToolPanle::setRegionName " + currentRegionId);
    	if (currentRegionId==-1) {
    		displayChoice.select(0);
    	}else {
			displayChoice.select(currentRegionId);
			try {
				SegCanvas c = (SegCanvas) ((SegStackWindow)this.getParent()).getCanvas();
				this.setVolumeCount(c.getVolumeCount(currentRegionId));
			}catch(Exception e) {System.out.println("ToolPanel setRegionName cought exception ");}
			System.out.println("End of toolpanel setRegionName");
    	}
    }
    
    public void itemStateChanged(java.awt.event.ItemEvent evt) {
        Object source = evt.getSource();
        if (source == displayChoice){
            String s = displayChoice.getSelectedItem();
            int r = -1;
            System.out.println("ToolPanel::itemStateChanged Region Name" + s);
            if (! s.equals("All Regions")){ 
                String rn = (String) rm2.get(s);
                try {
                    r = Integer.parseInt(rn);
					System.out.println("ToolPanel::itemStateChanged Region Name" + rn + " Region No " + r);
                } catch(Exception e){}
            }
            //update region via window
            SegStackWindow w = (SegStackWindow)this.getParent();
            SegCanvas c = (SegCanvas) w.getCanvas();
            c.setCmRegion(r);
            this.setVolumeCount(c.getVolumeCount(r));
			w.getSegImage().syncRegions(r,s);
			PlexiManager.setSetting("SegImage.regionID",new Integer(r));
        }
    }
    
    public void flush() {
    	//System.out.println("ToolPanel::Flush called ");
    	rm.clear(); rm2.clear();
    	rm=null; rm2=null;
    	displayChoice.removeItemListener(this);
    }
    
    public void setVolumeCntVisible(boolean bool) {
    	volCountLabel.setVisible(bool);
    	volCountText.setVisible(bool);
    }

	public void setMessage(String msg) {
		msgLabel.setText(msg);
	}
	    
}
