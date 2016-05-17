/*
 * org.nrg.plexiViewer.lite.utils.LoadErrorDialog
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */



package  org.nrg.plexiviewer.lite.utils;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
/**
 *
 * @author  dan
 */
public class LoadErrorDialog extends java.awt.Dialog {
    
    Vector message;
    
    /** Creates new form LoadErrorDialog */
    public LoadErrorDialog(java.awt.Frame parent, Vector message, boolean modal) {
        super(parent,modal);
        setTitle("Error Loading Image");
        this.message = message;
        initComponents();
    }
    
    private void initComponents(){
        
        setLayout(new BorderLayout());
        TextArea descrpArea = new TextArea("",3,50,TextArea.SCROLLBARS_NONE);
        descrpArea.appendText("The image you requested could not be loaded.  You may have requested an image that");
        descrpArea.appendText(" has not been built. If the error persists, please contact nrgtech@npg.wustl.edu.");
        descrpArea.setEditable(false);
        add(descrpArea,BorderLayout.NORTH);
        TextArea messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(java.awt.Color.white);
        for (int i=0; i < message.size(); i++){
                        messageArea.appendText((String)message.elementAt(i) + "\n");
        }
        add(messageArea,BorderLayout.CENTER);
        Button okButton = new Button("OK");
        okButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("button click");
                closeDialog();}
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.out.println("window closing");
                closeDialog();
            }
        });
        add(okButton,BorderLayout.SOUTH);
        pack();
        
    }
    
    
    /** Closes the dialog */
    private void closeDialog() {
        setVisible(false);
        dispose();
    }//
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        Vector list = new Vector();
        list.add("first");
        list.add("second");
        
        new LoadErrorDialog(new java.awt.Frame(),list, true).show();
        //System.exit(0);
    }
    
    
    // Variables declaration - do not modify//
    // End of variables declaration//
    
}
