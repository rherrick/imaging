//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.viewer.MR;

import java.awt.*;
import java.awt.event.*;

public class ToolPanel extends java.awt.Panel implements ActionListener {
    
    public static final int STACK_TOOLS = 1;
    public static final int MONTAGE_TOOLS = 2;
    public static final int SELECT = 1;
    public static final int CONTRAST = 2;
    public static final int SYNC = 3;
    int currentSelection;
    SmallLabel messageLabel;
    SmallLabel label;
    BorderPanel messagePanel;
    Panel scalePanel;
	
    //ImageButton scaleButton;
    //ImageButton selectButton;
    //ImageButton brightButton;
    //ImageButton syncButton;
    int toolType;
    
    /** Creates new form ToolPanel */
    public ToolPanel(int t) {
        toolType = t;
        initAddComponents ();
    }
    
    private void initAddComponents() {
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        setBackground(java.awt.Color.lightGray);
        
        messagePanel = new BorderPanel();
        messagePanel.setLayout(new java.awt.GridLayout(2, 1));
        messagePanel.setFont(new java.awt.Font ("Dialog", 0, 11));
        messagePanel.setName("panel2");
        messagePanel.setBackground(java.awt.Color.lightGray);
        messagePanel.setForeground(java.awt.Color.black);
        
        label = new SmallLabel(128,14);
        label.setText("             ");
        label.setBackground(Color.lightGray);
        messagePanel.add(label);
        
        messageLabel = new SmallLabel(128, 14);
        messageLabel.setAlignment(label.LEFT);
        messageLabel.setText("             ");
        messageLabel.setBackground(Color.lightGray);
        messagePanel.add(messageLabel);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 1;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(messagePanel,gridBagConstraints1);
        
        /*selectButton = new ImageButton("/arrow.gif");
        selectButton.addActionListener(this );
        selectButton.setActionCommand("select");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 1;
        gridBagConstraints1.gridheight = 1;
        add(selectButton, gridBagConstraints1);*/
        
        /*brightButton = new ImageButton("/half.gif");
        brightButton.addActionListener(this );
        brightButton.setActionCommand("contrast");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 1;
        gridBagConstraints1.gridheight = 1;
        add(brightButton, gridBagConstraints1);*/
        
        /*syncButton = new ImageButton("/lock.gif");
        syncButton.addActionListener(this );
        syncButton.setActionCommand("sync");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 1;
        gridBagConstraints1.gridheight = 1;
        add(syncButton, gridBagConstraints1);*/
        
       /* if (toolType == STACK_TOOLS){
            scaleButton = new ImageButton("/magnify.gif");
            scaleButton.addActionListener(this);
            scaleButton.setActionCommand("scale");
            gridBagConstraints1 = new java.awt.GridBagConstraints();
            gridBagConstraints1.gridwidth = 1;
            gridBagConstraints1.gridheight = 1;
            add(scaleButton,gridBagConstraints1);
        }*/
        
    }
    
    public void setMessage(String d, String s) {
        label.setText(d);
        messageLabel.setText(s);
    }
    
    public Dimension getPreferredSize(){
        return new Dimension (128,32);
    }
    
    public Insets getInsets() {
        Insets insets = super.getInsets();
        return new Insets(0,0,0,0);
    }
   /*
private void initComponents() {//GEN-BEGIN:initComponents
panel1 = new java.awt.Panel();
label3 = new java.awt.Label();
label4 = new java.awt.Label();
button2 = new java.awt.Button();
button3 = new java.awt.Button();
button4 = new java.awt.Button();
    
setLayout(new java.awt.GridBagLayout());
java.awt.GridBagConstraints gridBagConstraints1;
    
setBackground(java.awt.Color.lightGray);
panel1.setLayout(new java.awt.GridLayout(2, 1));
    
panel1.setFont(new java.awt.Font ("Dialog", 0, 11));
panel1.setBackground(java.awt.Color.lightGray);
panel1.setForeground(java.awt.Color.black);
label3.setFont(new java.awt.Font ("Dialog", 0, 11));
label3.setBackground(java.awt.Color.lightGray);
label3.setForeground(java.awt.Color.black);
label3.setText("label3");
panel1.add(label3);
    
label4.setFont(new java.awt.Font ("Dialog", 0, 11));
label4.setBackground(java.awt.Color.lightGray);
label4.setForeground(java.awt.Color.black);
label4.setText("label4");
panel1.add(label4);
    
gridBagConstraints1 = new java.awt.GridBagConstraints();
gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
gridBagConstraints1.weighty = 1.0;
add(panel1, gridBagConstraints1);
    
button2.setFont(new java.awt.Font ("Dialog", 0, 11));
button2.setLabel("1");
button2.setName("button2");
button2.setBackground(new java.awt.Color (131, 153, 177));
button2.setForeground(java.awt.Color.black);
gridBagConstraints1 = new java.awt.GridBagConstraints();
add(button2, gridBagConstraints1);
    
button3.setFont(new java.awt.Font ("Dialog", 0, 11));
button3.setLabel("2");
button3.setName("button3");
button3.setBackground(new java.awt.Color (131, 153, 177));
button3.setForeground(java.awt.Color.black);
gridBagConstraints1 = new java.awt.GridBagConstraints();
add(button3, gridBagConstraints1);
    
button4.setFont(new java.awt.Font ("Dialog", 0, 11));
button4.setLabel("3");
button4.setName("button4");
button4.setBackground(new java.awt.Color (131, 153, 177));
button4.setForeground(java.awt.Color.black);
gridBagConstraints1 = new java.awt.GridBagConstraints();
gridBagConstraints1.gridy = 0;
add(button4, gridBagConstraints1);
    
}//GEN-END:initComponents
    */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        String command = e.getActionCommand();
        
        /*if ( command.equalsIgnoreCase("select") ){
            currentSelection = SELECT;
            brightButton.setButtonDown(false);
            syncButton.setButtonDown(false);
        }
        else if (command.equalsIgnoreCase("contrast")){
            currentSelection = CONTRAST;
            //selectButton.setButtonDown(false);
            syncButton.setButtonDown(false);
        }
        else if (command.equalsIgnoreCase("sync")){
            currentSelection = SYNC;
            //selectButton.setButtonDown(false);
            brightButton.setButtonDown(false);
        }
        else if (command.equalsIgnoreCase("scale")){
            //scaleButton.setButtonDown(false);
            ( (MRStackWindow)getParent()).rescale();
        }*/
    }
    
    public int getCurrentSelection() {
        return currentSelection;
    }
    
    /*
// Variables declaration - do not modify//GEN-BEGIN:variables
private java.awt.Panel panel1;
private java.awt.Label label3;
private java.awt.Label label4;
private java.awt.Button button2;
private java.awt.Button button3;
private java.awt.Button button4;
// End of variables declaration//GEN-END:variables
     */
}
