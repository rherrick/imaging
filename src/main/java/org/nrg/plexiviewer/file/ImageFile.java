/* 
 *	Copyright Washington University in St Louis 2006
 *	All rights reserved
 * 	
 * 	@author Mohana Ramaratnam (Email: mramarat@wustl.edu)

*/

package org.nrg.plexiviewer.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import edu.sdsc.grid.io.GeneralFile;
import edu.sdsc.grid.io.MetaDataCondition;
import edu.sdsc.grid.io.MetaDataField;
import edu.sdsc.grid.io.MetaDataRecordList;
import edu.sdsc.grid.io.MetaDataSelect;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.MetaDataTable;
import edu.sdsc.grid.io.local.LocalFile;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileSystem;
import edu.sdsc.grid.io.srb.SRBMetaDataRecordList;
import edu.sdsc.grid.io.srb.SRBMetaDataSet;
import org.apache.commons.lang3.StringUtils;

public class ImageFile {
    
    String format;
    boolean remote = false;
    String localPath, localFileName;
    
    private void importFile(URI uri, String suffix) throws IOException, URISyntaxException {
        LocalFile dir = null;
        System.out.println("URI Path " + uri.getPath());
        if (dir == null) {
            dir = (LocalFile) LocalFile.createTempFile( "NRG", suffix );
            dir.delete();
            dir.mkdir();
        }
        get(new URI(StringUtils.replace(uri.toString(), ".img", ".*")), dir);
    }
    
    private void printMetaData(GeneralFile file) {
        try {
            MetaDataRecordList[] rl = file.query( new String[]{SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES});
            if (rl != null ) {
                System.out.println("File " + file.getName());
                System.out.println("rl " + rl.length);
                System.out.println("Name \t Value");
                for (int i = 0; i < rl.length; i++) {
                   MetaDataTable table = (MetaDataTable)rl[i].getValue(SRBMetaDataSet.getField(SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES));
                   for (int j = 0; j < table.getRowCount(); j++) {
                       System.out.println(table.getStringValue(j,0)+"\t"+table.getStringValue(j,1));
                   }
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private MetaDataTable getMetaData(String metaData) {
            String[][] definableMetaDataValues = new String[1][2];
            definableMetaDataValues[0][0] = "ASSOCIATED_WITH";
            definableMetaDataValues[0][1] = metaData;
            int[] operators = new int[definableMetaDataValues.length];
            operators[0] = MetaDataCondition.EQUAL;
           return (new MetaDataTable(operators,definableMetaDataValues));
    }
    
    private void getMatchingFiles(String dir, String fileName, String metaData) {
        MetaDataRecordList[] rl, rl1 = null;
        MetaDataCondition[] conditions = {
            MetaDataSet.newCondition( SRBMetaDataSet.DIRECTORY_NAME,
                MetaDataCondition.EQUAL, dir), 
             MetaDataSet.newCondition( SRBMetaDataSet.FILE_NAME,
                        MetaDataCondition.LIKE, fileName ) 
        };
        MetaDataSelect[] selects = {
            MetaDataSet.newSelection( SRBMetaDataSet.FILE_NAME ) };
        MetaDataTable metaDataTable = null;//getMetaData(metaData);
        try {
            SRBFileSystem srbFileSystem = new SRBFileSystem();
            rl = srbFileSystem.query( conditions, selects  );
            if (rl != null) {
                for (int i =0; i < rl.length; i++) {
                    System.out.println("File is " + rl[i].getStringValue(0));
                    GeneralFile file = new SRBFile(srbFileSystem,dir + SRBFileSystem.PATH_SEPARATOR + rl[i].getStringValue(0));
                    MetaDataField field = SRBMetaDataSet.getField(SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES);
                    rl1 = file.query( new String[]{SRBMetaDataSet.DEFINABLE_METADATA_FOR_FILES});
                    metaDataTable = (MetaDataTable)rl[0].getValue(field);
                    //rl[0] = new SRBMetaDataRecordList( field, (MetaDataTable)null );
                    //file.modifyMetaData( rl[0] );
                    if (rl1 == null)
                        rl1 = new MetaDataRecordList[1];
                    if (metaDataTable == null) {
                        metaDataTable = getMetaData(metaData);
                    }else
                        metaDataTable.addRow(new String[]{"ASSOCIATED_WITH"},MetaDataCondition.EQUAL);
                    rl1[0] =  new SRBMetaDataRecordList( field, metaDataTable );
                    file.modifyMetaData(rl1[0]);
                    printMetaData(file);
                }
            }else {System.out.println("Rl is null");}
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
    
    private void get(URI uri, LocalFile dir) throws IOException {
        GeneralFile file = null;
        LocalFile localFile = null;
        System.out.println("URI " + uri.toString());
        file = new SRBFile( new SRBFileSystem( ), uri.getPath());
        if (file != null && file.exists()) {
            System.out.println("File Name " + file.getName());
            System.out.println("File Path " + file.getParent());
            //actually do the local copy
            localFile = new LocalFile( dir, file.getName() );
            file.copyTo( localFile );
            System.out.println("File " + file.getPath() + " copied to " + localFile.getAbsolutePath());
        }else {
            System.out.println("File doesnt exist");
        }
    }
    
    public void openFile(URI uri, String suffix) throws IOException, URISyntaxException {
        System.out.println("URI recd " + uri);
        System.out.println("URI scheme " + uri.getScheme());
        if (suffix == null) {
            suffix = "_" + new Random().nextInt();
        }
        if (uri.getScheme() != null && uri.getScheme().startsWith("srb")) {
            importFile(uri,suffix);
            remote = true;
        }else {
            File f = new File(uri);
        }
    }
    
    public static void main(String[] args) {
        try {
            //String uri = "srb://dmarcus@wustl-nrg-gpop.nbirn.net/home/dmarcus.wustl-nrg/oasis/set1/disc1/OAS1_0001_MR1/PROCESSED/MPRAGE/T88_111/OAS1_0001_MR1_mpr_n4_anon_111_t88_gfc.*";
            //String uri = "/home/dmarcus.wustl-nrg/oasis/set1/disc1/OAS1_0001_MR1/PROCESSED/MPRAGE/T88_111/OAS1_0001_MR1_mpr_n4_anon_111_t88_gfc.*";
            ImageFile i = new ImageFile();
            i.getMatchingFiles("/home/dmarcus.wustl-nrg/oasis/set1/disc1/OAS1_0001_MR1/PROCESSED/MPRAGE/T88_111","OAS1_0001_MR1_mpr_n4_anon_111_t88_gfc.*","OAS1_0001_MR1_mpr_n4_anon_111_t88_gfc.img");
            //i.openFile(new URI(uri),null);
            
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
