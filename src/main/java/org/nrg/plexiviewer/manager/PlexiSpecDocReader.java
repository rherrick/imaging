/*
 * org.nrg.plexiViewer.manager.PlexiSpecDocReader
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.manager;

import org.nrg.plexiviewer.exceptions.LayoutNotFoundException;
import org.nrg.plexiviewer.lite.xml.Layout;
import org.nrg.plexiviewer.lite.xml.PlexiViewerSpecDoc;
import org.nrg.plexiviewer.lite.xml.PlexiViewerSpecForSession;
import org.nrg.plexiviewer.lite.xml.ViewableItem;
import org.nrg.xdat.XDATTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;


public class PlexiSpecDocReader {

	private PlexiViewerSpecDoc viewerSpecDoc;	
    private Hashtable layoutHash = new Hashtable();
	private static PlexiSpecDocReader DR=null;
	private Document doc;

	private  PlexiSpecDocReader() {
        viewerSpecDoc = new PlexiViewerSpecDoc();
		loadImageViewerInfo();
	}
    
    
    public boolean refresh() {
        viewerSpecDoc = new PlexiViewerSpecDoc();
        return loadImageViewerInfo();
    }

    public boolean loadImageViewerInfo() {
        boolean success = true;
        try {
            String settingsDir = XDATTool.GetSettingsDirectory();
            //System.out.println("Directory location is " + settingsDir); 
            String plexiImageSpecDoc ="PlexiViewerSpec.xml";
            String fileLocation = settingsDir + File.separator + plexiImageSpecDoc;
            //String fileLocation =  plexiImageSpecDoc;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse( new File(fileLocation));
            Element rootElement = doc.getDocumentElement();
            NodeList elements = rootElement.getChildNodes();
            for(int i =0; i<elements.getLength();i++) {
                if (elements.item(i).getNodeName().equalsIgnoreCase("PlexiViewer")) {
                    loadImageViewerInfo(elements.item(i));
                }
            }
        }catch (SAXException sxe) {
            // Error generated during parsing
            Exception  x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            x.printStackTrace();
            success = false;
         } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
            success = false;
         } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
            success = false;
         }catch (Exception e) {
            e.printStackTrace();
            success = false;
         }
         return success;    
    }
    
	public void loadImageViewerInfo(Node plexiViewerNode) throws Exception
	{
        PlexiViewerSpecForSession plexiViewerSpecDoc = new PlexiViewerSpecForSession();
        //String fileLocation = org.cnl.xnat.utils.FileUtils.GetInstance(org.cnl.xnat.utils.FileUtils.GetCNDAPropertiesFileLocation()).getImageSpecificationFile();
        Populate populateFromXML = new Populate();
		try {
			NodeList elements = plexiViewerNode.getChildNodes();
			for(int i =0; i<elements.getLength();i++)
			{
				/*if (elements.item(i).getNodeName().equalsIgnoreCase("ThumbnailArchiveLocation")) {
					plexiViewerSpecDoc.setThumbnailArchiveLocation(elements.item(i).getFirstChild().getNodeValue());
				}else if (elements.item(i).getNodeName().equalsIgnoreCase("LoResArchiveLocation")) {
					plexiViewerSpecDoc.setLoResArchiveLocation(elements.item(i).getFirstChild().getNodeValue());
				}else if (elements.item(i).getNodeName().equalsIgnoreCase("CacheLocation")) {
                    plexiViewerSpecDoc.setCacheLocation(elements.item(i).getFirstChild().getNodeValue());
                }*/ 
                
                if (elements.item(i).getNodeName().equalsIgnoreCase("DefaultLoResType")) {
					plexiViewerSpecDoc.setDefaultLoResType(elements.item(i).getFirstChild().getNodeValue());
				} else 	if (elements.item(i).getNodeName().equalsIgnoreCase("Layout")) {
					Layout layout = populateFromXML.FillLayout(elements.item(i));
					layoutHash.put(layout.getName(),layout);
					//System.out.println("Inserted layout" + layout.getName());
				} else if (elements.item(i).getNodeName().equalsIgnoreCase("ViewableItem")) {
					ViewableItem v = populateFromXML.FillViewableItem(elements.item(i));
					plexiViewerSpecDoc.addViewableItem(v);
				}	 
			}
            try {
                setupLayout(plexiViewerSpecDoc);
            }catch(LayoutNotFoundException lnfe) {
                lnfe.printStackTrace();
            }
            String sessionTypes[] = plexiViewerNode.getAttributes().getNamedItem("sessionType").getNodeValue().split(",");
            for (int i = 0; i < sessionTypes.length; i++)
                viewerSpecDoc.setViewerSpecification(sessionTypes[i].trim(),plexiViewerSpecDoc);
        }catch (Exception e) {
	 	 	e.printStackTrace();
	 	 }
	}
		
	public static PlexiSpecDocReader GetInstance() {
		if (DR==null)
			DR = new PlexiSpecDocReader();
		return DR;		
	}
	
	public PlexiViewerSpecDoc getSpecDoc() {
		return viewerSpecDoc;
	}
	
    public PlexiViewerSpecForSession getSpecDoc(String project) {
        return viewerSpecDoc.getPlexiViewerSpecForSession(project);
    }
    
    public void setSpecDoc(String project, PlexiViewerSpecForSession projectSpec ) throws LayoutNotFoundException {
          synchronized(viewerSpecDoc) {
              setupLayout(projectSpec);
              viewerSpecDoc.setViewerSpecification(project, projectSpec);
          }
    }
    
    
	public void setupLayout(PlexiViewerSpecForSession plexiViewerSpecDoc) throws LayoutNotFoundException {
		if (plexiViewerSpecDoc!=null && !plexiViewerSpecDoc.getViewableItemHash().isEmpty()) {
			for (Enumeration e=plexiViewerSpecDoc.getViewableItemHash().keys(); e.hasMoreElements(); ) {
				Object key = e.nextElement();
				ViewableItem v = (ViewableItem)plexiViewerSpecDoc.getViewableItemHash().get(key);
				if (v.getHiRes()!=null) {
					String lname = v.getHiRes().getLayoutName();
					if (layoutHash.containsKey(lname)) 
						v.getHiRes().setLayout((Layout)layoutHash.get(v.getHiRes().getLayoutName()));	
					else throw new LayoutNotFoundException("Didnt find the Layout " + v.getHiRes().getLayoutName());
				}
			}
		}else {
			System.out.println("Error PlexiViewerDocInfo is null");
		}
	}	
	
    public static void main(String args[]) {
        PlexiViewerSpecDoc vd = PlexiSpecDocReader.GetInstance().getSpecDoc();
        System.out.println("Read the doc " );
        PlexiViewerSpecForSession specDoc=PlexiSpecDocReader.GetInstance().getSpecDoc("GenPIB");
        System.out.println("CachePath is " + specDoc.getCacheLocation());
    }
}
