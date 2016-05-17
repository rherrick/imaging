//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.Seg;

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
        
        rm  = new Hashtable();
        rm.put("0", "Unknown");
        //rm.put("1", "L-Cerebral-Ext");
        rm.put("2", "L-Cerebral-WM");
        rm.put("3", "L-Cerebral-Cortex");
        rm.put("4", "L-Lat-Vent");
        rm.put("5", "L-Inf-Lat-Vent");
        //rm.put("6", "L-Cerebellum-Ext");
        rm.put("7", "L-Cerebellum-WM");
        rm.put("8", "L-Cerebellum-Cortex");
        //rm.put("9", "L-Thalamus");
        rm.put("10", "L-Thalamus-Proper");
        rm.put("11", "L-Caudate");
        rm.put("12", "L-Putamen");
        rm.put("13", "L-Pallidum");
        rm.put("14", "3rd-Ventricle");
        rm.put("15", "4th-Ventricle");
        rm.put("16", "Brain-Stem");
        rm.put("17", "L-Hippocampus");
        rm.put("18", "L-Amygdala");
        //rm.put("19", "L-Insula");
        //rm.put("20", "L-Operculum");
        //rm.put("21", "Line-1");
        //rm.put("22", "Line-2");
        //rm.put("23", "Line-3");
        rm.put("24", "CSF");
        //rm.put("25", "L-Lesion");
        rm.put("26", "L-Accumbens-area");
        //rm.put("27", "L-Substancia-Nigra");
        rm.put("28", "L-VentralDC");
        //rm.put("29", "L-undetermined");
        rm.put("30", "L-vessel");
        /*rm.put("31", "L-choroid-plexus");
        rm.put("32", "L-F3orb");
        rm.put("33", "L-lOg");
        rm.put("34", "L-aOg");
        rm.put("35", "L-mOg");
        rm.put("36", "L-pOg");
        rm.put("37", "L-Stellate");
        rm.put("38", "L-Porg");
        rm.put("39", "L-Aorg");
        rm.put("40", "R-Cerebral-Ext"); */
        rm.put("41", "R-Cerebral-WM");
        rm.put("42", "R-Cerebral-Cortex");
        rm.put("43", "R-Lat-Vent");
        rm.put("44", "R-Inf-Lat-Vent");
        //rm.put("45", "R-Cerebellum-Ext");
        rm.put("46", "R-Cerebellum-WM");
        rm.put("47", "R-Cerebellum-Cortex");
        //rm.put("48", "R-Thalamus");
        rm.put("49", "R-Thalamus-Proper");
        rm.put("50", "R-Caudate");
        rm.put("51", "R-Putamen");
        rm.put("52", "R-Pallidum");
        rm.put("53", "R-Hippocampus");
        rm.put("54", "R-Amygdala");
        //rm.put("55", "R-Insula");
        //rm.put("56", "R-Operculum");
        //rm.put("57", "R-Lesion");
        rm.put("58", "R-Accumbens-area");
        //rm.put("59", "R-Substancia-Nigra");
        rm.put("60", "R-VentralDC");
        //rm.put("61", "R-undetermined");
        rm.put("62", "R-vessel");
        /*rm.put("63", "R-choroid-plexus");
        rm.put("64", "R-F3orb");
        rm.put("65", "R-lOg");
        rm.put("66", "R-aOg");
        rm.put("67", "R-mOg");
        rm.put("68", "R-pOg");
        rm.put("69", "R-Stellate");
        rm.put("70", "R-Porg");
        rm.put("71", "R-Aorg"); */
        rm.put("72", "5th-Ventricle");
        //rm.put("73", "L-Interior");
        //rm.put("74", "R-Interior");
        //rm.put("75", "L-Lat-Vents");
        //rm.put("76", "R-Lat-Vents");
        rm.put("77", "WM-hypointens");
        rm.put("78", "L-WM-hypointens");
        rm.put("79", "R-WM-hypointens");
        rm.put("80", "non-WM-hypointens");
        //rm.put("81", "L-non-WM-hypointens");
        //rm.put("82", "R-non-WM-hypointens");
        //rm.put("83", "L-F1");
        //rm.put("84", "R-F1");
        rm.put("85", "Optic-Chiasm");
        //rm.put("86", "Corpus_Callosum");
        //rm.put("96", "L-Amygdala-Ant");
        //rm.put("97", "R-Amygdala-Ant");
        //rm.put("98", "Dura");
        //rm.put("100", "L-wm-intensity-abnormality");
        //rm.put("101", "L-caudate-intensity-abnormality");
        //rm.put("102", "L-putamen-intensity-abnormality");
        //rm.put("103", "L-accumbens-intensity-abnormality");
        //rm.put("104", "L-pallidum-intensity-abnormality");
        //rm.put("105", "L-amygdala-intensity-abnormality");
        //rm.put("106", "L-hippocampus-intensity-abnormality");
        //rm.put("107", "L-thalamus-intensity-abnormality");
        //rm.put("108", "L-VDC-intensity-abnormality");
        //rm.put("109", "R-wm-intensity-abnormality");
        //rm.put("110", "R-caudate-intensity-abnormality");
        //rm.put("111", "R-putamen-intensity-abnormality");
        //rm.put("112", "R-accumbens-intensity-abnormality");
        //rm.put("113", "R-pallidum-intensity-abnormality");
        //rm.put("114", "R-amygdala-intensity-abnormality");
        //rm.put("115", "R-hippocampus-intensity-abnormality");
        //rm.put("116", "R-thalamus-intensity-abnormality");
        //rm.put("117", "R-VDC-intensity-abnormality");
        /*rm.put("118", "Epidermis");
        rm.put("119", "Conn-Tissue");
        rm.put("120", "SC-Fat/Muscle");
        rm.put("121", "Cranium");
        rm.put("122", "CSF-SA");
        rm.put("123", "Muscle");
        rm.put("124", "Ear");
        rm.put("125", "Fatty-Tissue");
        rm.put("126", "Spinal-Cord");
        rm.put("127", "Soft-Tissue");
        rm.put("128", "Nerve");
        rm.put("129", "Bone");
        rm.put("130", "Air");
        rm.put("131", "Orbit");
        rm.put("132", "Tongue");
        rm.put("133", "Nasal-Structures");
        rm.put("134", "Globe");
        rm.put("135", "Teeth");
        rm.put("136", "L-Caudate/Putamen");
        rm.put("137", "R-Caudate/Putamen");
        rm.put("138", "L-Claustrum");
        rm.put("139", "R-Claustrum"); */
        
        rm2  = new Hashtable();
        rm2.put("Unknown", "0");
        //rm2.put("L-Cerebral-Ext", "1");
        rm2.put("L-Cerebral-WM", "2");
        rm2.put("L-Cerebral-Cortex", "3");
        rm2.put("L-Lat-Vent", "4");
        rm2.put("L-Inf-Lat-Vent", "5");
        //rm2.put("L-Cerebellum-Ext", "6");
        rm2.put("L-Cerebellum-WM", "7");
        rm2.put("L-Cerebellum-Cortex", "8");
        //rm2.put("L-Thalamus", "9");
        rm2.put("L-Thalamus-Proper", "10");
        rm2.put("L-Caudate", "11");
        rm2.put("L-Putamen", "12");
        rm2.put("L-Pallidum", "13");
        rm2.put("3rd-Ventricle", "14");
        rm2.put("4th-Ventricle", "15");
        rm2.put("Brain-Stem", "16");
        rm2.put("L-Hippocampus", "17");
        rm2.put("L-Amygdala", "18");
        //rm2.put("L-Insula", "19");
        //rm2.put("L-Operculum", "20");
        //rm2.put("Line-1", "21");
        //rm2.put("Line-2", "22");
        //rm2.put("Line-3", "23");
        rm2.put("CSF", "24");
        //rm2.put("L-Lesion", "25");
        rm2.put("L-Accumbens-area", "26");
        //rm2.put("L-Substancia-Nigra", "27");
        rm2.put("L-VentralDC", "28");
        //rm2.put("L-undetermined", "29");
        rm2.put("L-vessel", "30");
        /*rm2.put("L-choroid-plexus", "31");
        rm2.put("L-F3orb", "32");
        rm2.put("L-lOg", "33");
        rm2.put("L-aOg", "34");
        rm2.put("L-mOg", "35");
        rm2.put("L-pOg", "36");
        rm2.put("L-Stellate", "37");
        rm2.put("L-Porg", "38");
        rm2.put("L-Aorg", "39");
        rm2.put("R-Cerebral-Ext", "40");*/
        rm2.put("R-Cerebral-WM", "41");
        rm2.put("R-Cerebral-Cortex", "42");
        rm2.put("R-Lat-Vent", "43");
        rm2.put("R-Inf-Lat-Vent", "44");
        //rm2.put("R-Cerebellum-Ext", "45");
        rm2.put("R-Cerebellum-WM", "46");
        rm2.put("R-Cerebellum-Cortex", "47");
        //rm2.put("R-Thalamus", "48");
        rm2.put("R-Thalamus-Proper", "49");
        rm2.put("R-Caudate", "50");
        rm2.put("R-Putamen", "51");
        rm2.put("R-Pallidum", "52");
        rm2.put("R-Hippocampus", "53");
        rm2.put("R-Amygdala", "54");
        //rm2.put("R-Insula", "55");
        //rm2.put("R-Operculum", "56");
        //rm2.put("R-Lesion", "57");
        rm2.put("R-Accumbens-area", "58");
        //rm2.put("R-Substancia-Nigra", "59");
        rm2.put("R-VentralDC", "60");
        //rm2.put("R-undetermined", "61");
        rm2.put("R-vessel", "62");
        /*rm2.put("R-choroid-plexus", "63");
        rm2.put("R-F3orb", "64");
        rm2.put("R-lOg", "65");
        rm2.put("R-aOg", "66");
        rm2.put("R-mOg", "67");
        rm2.put("R-pOg", "68");
        rm2.put("R-Stellate", "69");
        rm2.put("R-Porg", "70");
        rm2.put("R-Aorg", "71");*/
        rm2.put("5th-Ventricle", "72");
        /*rm2.put("L-Interior", "73");
        rm2.put("R-Interior", "74");
        rm2.put("L-Lat-Vents", "75");
        rm2.put("R-Lat-Vents", "76"); */
        rm2.put("WM-hypointens", "77");
        rm2.put("L-WM-hypointens", "78");
        rm2.put("R-WM-hypointens", "79");
        rm2.put("non-WM-hypointens", "80");
      //  rm2.put("L-non-WM-hypointensities", "81");
      //  rm2.put("R-non-WM-hypointensities", "82");
        //rm2.put("L-F1", "83");
       // rm2.put("R-F1", "84");
        rm2.put("Optic-Chiasm", "85");
        //rm2.put("Corpus_Callosum", "86");
        //rm2.put("L-Amygdala-Ant", "96");
        //rm2.put("R-Amygdala-Ant", "97");
        //rm2.put("Dura", "98");
        /*rm2.put("L-wm-intensity-abnormality", "100");
        rm2.put("L-caudate-intensity-abnormality", "101");
        rm2.put("L-putamen-intensity-abnormality", "102");
        rm2.put("L-accumbens-intensity-abnormality", "103");
        rm2.put("L-pallidum-intensity-abnormality", "104");
        rm2.put("L-amygdala-intensity-abnormality", "105");
        rm2.put("L-hippocampus-intensity-abnormality", "106");
        rm2.put("L-thalamus-intensity-abnormality", "107");
        rm2.put("L-VDC-intensity-abnormality", "108");
        rm2.put("R-wm-intensity-abnormality", "109");
        rm2.put("R-caudate-intensity-abnormality", "110");
        rm2.put("R-putamen-intensity-abnormality", "111");
        rm2.put("R-accumbens-intensity-abnormality", "112");
        rm2.put("R-pallidum-intensity-abnormality", "113");
        rm2.put("R-amygdala-intensity-abnormality", "114");
        rm2.put("R-hippocampus-intensity-abnormality", "115");
        rm2.put("R-thalamus-intensity-abnormality", "116");
        rm2.put("R-VDC-intensity-abnormality", "117");
        rm2.put("Epidermis", "118");
        rm2.put("Conn-Tissue", "119");
        rm2.put("SC-Fat/Muscle", "120");
        rm2.put("Cranium", "121");
        rm2.put("CSF-SA", "122");
        rm2.put("Muscle", "123");
        rm2.put("Ear", "124");
        rm2.put("Fatty-Tissue", "125");
        rm2.put("Spinal-Cord", "126");
        rm2.put("Soft-Tissue", "127");
        rm2.put("Nerve", "128");
        rm2.put("Bone", "129");
        rm2.put("Air", "130");
        rm2.put("Orbit", "131");
        rm2.put("Tongue", "132");
        rm2.put("Nasal-Structures", "133");
        rm2.put("Globe", "134");
        rm2.put("Teeth", "135");
        rm2.put("L-Caudate/Putamen", "136");
        rm2.put("R-Caudate/Putamen", "137");
        rm2.put("L-Claustrum", "138");
        rm2.put("R-Claustrum", "139");*/
        
        
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
        Insets insets = super.getInsets();
        return new Insets(0,0,0,0);
    }
    public void actionPerformed(java.awt.event.ActionEvent e) {
        String command = e.getActionCommand();
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
