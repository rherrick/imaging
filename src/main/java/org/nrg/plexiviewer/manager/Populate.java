//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.manager;

import org.nrg.plexiviewer.lite.display.*;
import org.nrg.plexiviewer.lite.xml.*;
import org.w3c.dom.*;
import org.nrg.xft.utils.*;
import java.util.*;

public class Populate {
	
	public Populate() {
	}
	
	public Layout FillLayout(Node layoutNode) {
		Layout layout = new Layout();
		layout.setName(layoutNode.getAttributes().getNamedItem("name").getNodeValue());
		layout.setVoxelSize(Integer.parseInt(layoutNode.getAttributes().getNamedItem("voxelSize").getNodeValue()));
		for(int k =0; k<layoutNode.getChildNodes().getLength();k++) {
			if (layoutNode.getChildNodes().item(k).getNodeName().equalsIgnoreCase("Coordinates")) {
				Node coordinatesNode = 	layoutNode.getChildNodes().item(k);
				float oX =  Float.parseFloat(coordinatesNode.getAttributes().getNamedItem("originX").getNodeValue());
				float oY =  Float.parseFloat(coordinatesNode.getAttributes().getNamedItem("originY").getNodeValue());
				float oZ =  Float.parseFloat(coordinatesNode.getAttributes().getNamedItem("originZ").getNodeValue());
				layout.setOrigin(new Point3d(oX,oY,oZ));	
			}
		}
				
		return layout;
	}
	
	
	public ViewableItem FillViewableItem(Node vNode) {
		ViewableItem vi = new ViewableItem();
		vi.setType(vNode.getAttributes().getNamedItem("type").getNodeValue());
        if (XMLUtils.HasAttribute(vNode,"imageViewerClassName"))
            vi.setImageViewerClassName(vNode.getAttributes().getNamedItem("imageViewerClassName").getNodeValue());
		if (XMLUtils.HasAttribute(vNode,"displayCondition"))
		    vi.setDisplayCondition(vNode.getAttributes().getNamedItem("displayCondition").getNodeValue());
		for(int j =0; j<vNode.getChildNodes().getLength();j++) {
            //if (vNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("Viewer")) {
             //   Node sNode = vNode.getChildNodes().item(j);
              //  Viewer viewer = FillViewer(sNode);
              //  vi.setViewer(viewer);
            //}else 
            if (vNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("SchemaLink")) {
				Node sNode = vNode.getChildNodes().item(j);
				SchemaLink sl = FillSchemaLink(sNode);
				vi.setSchemaLink(sl);
			}else if (vNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("UserInterface")) {
				Node uNode = vNode.getChildNodes().item(j);
				UserInterface u = FillUserInterface(uNode);
				vi.setUserInterface(u);
			}	
			else if (vNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("Thumbnail")) {
				Node tNode = vNode.getChildNodes().item(j);
				Thumbnail tbnail = FillThumbNail(tNode);
				vi.setThumbnail(tbnail);
			}	
			else if (vNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("Hi-Res")) {
				Node hResNode = vNode.getChildNodes().item(j);
				HiRes hRes= FillHiRes(hResNode);
				vi.setHiRes(hRes); 
			}else if (vNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("Lo-Res")) {
				Node lResNode = vNode.getChildNodes().item(j);
				LoRes lRes = FillLoRes(lResNode);
				vi.addToLoResHash(lRes);	
			}		
		}
		return vi;
	}

    

    
	private SchemaLink FillSchemaLink(Node sNode) {
		SchemaLink s = new SchemaLink();
		if (XMLUtils.HasAttribute(sNode,"element"))	s.setElementName(sNode.getAttributes().getNamedItem("element").getNodeValue());
		if (XMLUtils.HasAttribute(sNode,"value"))	s.setValue(sNode.getAttributes().getNamedItem("value").getNodeValue());
		return s;
	}

	private UserInterface FillUserInterface(Node uNode) {
		UserInterface u = new UserInterface();
		if (XMLUtils.HasAttribute(uNode,"selectionPriority"))	u.setSelectionPriority(Integer.parseInt(uNode.getAttributes().getNamedItem("selectionPriority").getNodeValue()));
		if (XMLUtils.HasAttribute(uNode,"displayText"))	u.setDisplayText(uNode.getAttributes().getNamedItem("displayText").getNodeValue());
		if (XMLUtils.HasAttribute(uNode,"allowedToChooseFiles"))	u.setAllowedToChooseFiles(Boolean.valueOf(uNode.getAttributes().getNamedItem("allowedToChooseFiles").getNodeValue()).booleanValue());
		if (XMLUtils.HasAttribute(uNode,"allowedToChooseID"))	u.setAllowedToChooseIds(Boolean.valueOf(uNode.getAttributes().getNamedItem("allowedToChooseID").getNodeValue()).booleanValue());
		for(int j =0; j<uNode.getChildNodes().getLength();j++) {
			if (uNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("LinkedDropDown")) {
				Node lNode = uNode.getChildNodes().item(j);
				LinkedDropDown l = FillLinkedDropDown(lNode);
				u.addToLinkedDropDown(l);
			}			
		}		
		return u;
	}
	
	private LinkedDropDown FillLinkedDropDown(Node lNode) {
		LinkedDropDown l = new LinkedDropDown();
		if (XMLUtils.HasAttribute(lNode,"viewableItemType"))	l.setViewableItemType(lNode.getAttributes().getNamedItem("viewableItemType").getNodeValue());
		return l;
	}
	
	
	private HiRes FillHiRes(Node hResNode) {
		HiRes hRes = new HiRes();
		int location=0;
		hRes.setFormat(hResNode.getAttributes().getNamedItem("format").getNodeValue());
		if (XMLUtils.HasAttribute(hResNode,"minIntensity")) {
			hRes.setMinIntensity(new Float(Float.parseFloat(hResNode.getAttributes().getNamedItem("minIntensity").getNodeValue())));
		}
		if (XMLUtils.HasAttribute(hResNode,"maxIntensity")) {
			hRes.setMaxIntensity(new Float(Float.parseFloat(hResNode.getAttributes().getNamedItem("maxIntensity").getNodeValue())));
		}
		for(int j =0; j<hResNode.getChildNodes().getLength();j++) {
			if (hResNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("Layer")) {
				Node iNode = hResNode.getChildNodes().item(j);
				if (XMLUtils.HasAttribute(iNode,"num"))
					location = Integer.parseInt(iNode.getAttributes().getNamedItem("num").getNodeValue());
				SchemaLink s = FillSchemaLink(iNode);
				hRes.setLayer(new Integer(location),s);
			}
			else if (hResNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("LayoutRef")) {
				hRes.setLayoutName(hResNode.getChildNodes().item(j).getAttributes().getNamedItem("name").getNodeValue());
			}else if (hResNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("MontageView")) {
				Node mvNode = hResNode.getChildNodes().item(j);
				hRes.setMontageView(FillMontageView(mvNode));
			}
		}
		return hRes;	
	}	
	
	private LoRes FillLoRes(Node lResNode) {
		LoRes lRes = new LoRes();
		lRes.setFormat(lResNode.getAttributes().getNamedItem("format").getNodeValue());
		lRes.setConverterClassName(lResNode.getAttributes().getNamedItem("converterClassName").getNodeValue());
		lRes.setType(lResNode.getAttributes().getNamedItem("type").getNodeValue());
		for(int j =0; j<lResNode.getChildNodes().getLength();j++) {
			if (lResNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("CropDetails")) {
				Node cdNode = lResNode.getChildNodes().item(j);
				lRes.setCropDetails(FillCropDetails(cdNode));
			}else if (lResNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("MontageView")) {
				Node mvNode = lResNode.getChildNodes().item(j);
				lRes.setMontageView(FillMontageView(mvNode));
			}else if (lResNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("Parameter")) {
				Node pNode = lResNode.getChildNodes().item(j);
				String attrib = pNode.getAttributes().getNamedItem("attribute").getNodeValue();
				String value = pNode.getAttributes().getNamedItem("value").getNodeValue();
				lRes.setParameter(attrib,value);
			}
		}
		return lRes;	
	}	
	
	private Thumbnail FillThumbNail(Node tNode) {
		Thumbnail tbnail = new Thumbnail();
		tbnail.setFormat(tNode.getAttributes().getNamedItem("format").getNodeValue());
		tbnail.setConverterClassName(tNode.getAttributes().getNamedItem("converterClassName").getNodeValue());
		Hashtable  sliceInfo = new Hashtable();
		for(int j =0; j<tNode.getChildNodes().getLength();j++) {
			if (tNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("CropDetails")) {
				Node cdNode = tNode.getChildNodes().item(j);
				tbnail.setCropDetails(FillCropDetails(cdNode));
			}else if (tNode.getChildNodes().item(j).getNodeName().equalsIgnoreCase("Slice")) {
				Node sNode = tNode.getChildNodes().item(j);
				if (XMLUtils.HasAttribute(sNode,"orientation") && XMLUtils.HasAttribute(sNode,"number")) {
					String key = sNode.getAttributes().getNamedItem("orientation").getNodeValue().toUpperCase();
					Integer value = Integer.valueOf(sNode.getAttributes().getNamedItem("number").getNodeValue());
					if (sliceInfo.containsKey(key)) {
						Vector v = (Vector)sliceInfo.get(key);
						v.add(value);	
					}else {
						Vector v = new Vector();
						v.addElement(value);
						sliceInfo.put(key,v);
					}
				}
			}		
 		}
		tbnail.setSlices(sliceInfo);
		return tbnail;	
	}
	
	
	private CropDetails FillCropDetails(Node cdNode) {
		CropDetails cd = new CropDetails();
		if (XMLUtils.HasAttribute(cdNode,"startSlice")) {
			cd.setStartSlice(Integer.parseInt(cdNode.getAttributes().getNamedItem("startSlice").getNodeValue()));
		}						
		if (XMLUtils.HasAttribute(cdNode,"endSlice")) {
			cd.setEndSlice(Integer.parseInt(cdNode.getAttributes().getNamedItem("endSlice").getNodeValue()));
		}
		java.awt.Rectangle bounds = new java.awt.Rectangle();
		bounds.x = Integer.parseInt(cdNode.getAttributes().getNamedItem("X").getNodeValue());
		bounds.y = Integer.parseInt(cdNode.getAttributes().getNamedItem("Y").getNodeValue());
		bounds.width = Integer.parseInt(cdNode.getAttributes().getNamedItem("width").getNodeValue());
		bounds.height = Integer.parseInt(cdNode.getAttributes().getNamedItem("height").getNodeValue());
		cd.setBoundingRectangle(bounds);	
		return cd;
	}	


	private MontageView FillMontageView(Node mvNode) {
		MontageView mv = new MontageView();
		if (XMLUtils.HasAttribute(mvNode,"scale")) {
			mv.setScale(Float.parseFloat(mvNode.getAttributes().getNamedItem("scale").getNodeValue()));
		}
		for (int i=0;i<mvNode.getChildNodes().getLength();i++) {
			if (mvNode.getChildNodes().item(i).getNodeName().equalsIgnoreCase("transverse")) {
				Node childNode = mvNode.getChildNodes().item(i);
				mv.addViewInfo("TRANSVERSE",getMvStartSlice(childNode),getMvEndSlice(childNode),getMvSliceSpacing(childNode));
			}else if (mvNode.getChildNodes().item(i).getNodeName().equalsIgnoreCase("sagittal")) {
				Node childNode = mvNode.getChildNodes().item(i);
				mv.addViewInfo("SAGITTAL",getMvStartSlice(childNode),getMvEndSlice(childNode),getMvSliceSpacing(childNode));
			}else if (mvNode.getChildNodes().item(i).getNodeName().equalsIgnoreCase("coronal")) {
				Node childNode = mvNode.getChildNodes().item(i);
				mv.addViewInfo("CORONAL",getMvStartSlice(childNode),getMvEndSlice(childNode),getMvSliceSpacing(childNode));
			}
		}
		return mv;
	}	
	
	private int getMvStartSlice(Node node) {
		return Integer.parseInt(node.getAttributes().getNamedItem("start_slice").getNodeValue());
	}
	private int getMvEndSlice(Node node) {
		return Integer.parseInt(node.getAttributes().getNamedItem("end_slice").getNodeValue());
	}
	private int getMvSliceSpacing(Node node) {
		return Integer.parseInt(node.getAttributes().getNamedItem("slice_spacing").getNodeValue());
	}
}
