//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.Servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.nrg.xft.*;
import org.nrg.xft.collections.*;
import org.nrg.xft.search.*;


public class getAsegRegionVolumes extends HttpServlet {
	
	/** Initializes the servlet.
		   */
	String[] regionName= { 
		"Unknown",//0
		"Cerebral-Exterior",//1
		"Cerebral-White-Matter",//2
		"Cerebral-Cortex",//3
		"Lateral-Ventricle",//4
		"Inf-Lat-Vent",//5
		"Cerebellum-Exterior",//6
		"Cerebellum-White-Matter",//7
		"Cerebellum-Cortex",//8
		"Thalamus",//9
		"Thalamus-Proper",//10
		"Caudate",//11
		"Putamen",//12
		"Pallidum",//13
		"3rd-Ventricle",//14
		"4th-Ventricle",//15
		"Brain-Stem",//16
		"Hippocampus",//17
		"Amygdala",//18
		"Insula",//19
		"Operculum",//20
		"Line-1",//21
		"Line-2",//22
		"Line-3",//23
		"CSF",//24
		"Lesion",//25
		"Accumbens-area",//26
		"Substancia-Nigra",//27
		"VentralDC",//28
		"undetermined",//29
		"vessel",//30
		"choroid-plexus",//31
		"F3orb",//32
		"lOg",//33
		"aOg",//34
		"mOg",//35
		"pOg",//36
		"Stellate",//37
		"Porg",//38
		"Aorg",//39
		"Cerebral-Exterior",//40
		"Cerebral-White-Matter",//41
		"Cerebral-Cortex",//42
		"Lateral-Ventricle",//43
		"Inf-Lat-Vent",//44
		"Cerebellum-Exterior",//45
		"Cerebellum-White-Matter",//46
		"Cerebellum-Cortex",//47
		"Thalamus",//48
		"Thalamus-Proper",//49
		"Caudate",//50
		"Putamen",//51
		"Pallidum",//52
		"Hippocampus",//53
		"Amygdala",//54
		"Insula",//55
		"Operculum",//56
		"Lesion",//57
		"Accumbens-area",//58
		"Substancia-Nigra",//59
		"VentralDC",//60
		"undetermined",//61
		"vessel",//62
		"choroid-plexus",//63
		"F3orb",//64
		"lOg",//65
		"aOg",//66
		"mOg",//67
		"pOg",//68
		"Stellate",//69
		"Porg",//70
		"Aorg",//71
		"5th-Ventricle",//72
		"Interior",//73
		"Interior",//74
		"Lateral-Ventricles",//75
		"Lateral-Ventricles",//76
		"WM-hypointensities",//77
		"WM-hypointensities",//78
		"WM-hypointensities",//79
		"non-WM-hypointensities",//80
		"non-WM-hypointensities",//81
		"non-WM-hypointensities",//82
		"F1",//83
		"F1",//84
		"Optic-Chiasm",//85
		"Corpus_Callosum",//86
		"","","","","","","","","",
		"Amygdala-Anterior",//96
		"Amygdala-Anterior",//97
		"Dura",//98
		"",
		"wm-intensity-abnormality",//100
		"caudate-intensity-abnormality",//101
		"putamen-intensity-abnormality",//102
		"accumbens-intensity-abnormality",//103
		"pallidum-intensity-abnormality",//104
		"amygdala-intensity-abnormality",//105
		"hippocampus-intensity-abnormality",//106
		"thalamus-intensity-abnormality",//107
		"VDC-intensity-abnormality",//108
		"wm-intensity-abnormality",//109
		"caudate-intensity-abnormality",//110
		"putamen-intensity-abnormality",//111
		"accumbens-intensity-abnormality",//112
		"pallidum-intensity-abnormality",//113
		"amygdala-intensity-abnormality",//114
		"hippocampus-intensity-abnormality",//115
		"thalamus-intensity-abnormality",//116
		"VDC-intensity-abnormality",//117
		"Epidermis",//118
		"Conn-Tissue",//119
		"SC-Fat/Muscle",//120
		"Cranium",//121
		"CSF-SA",//122
		"Muscle",//123
		"Ear",//124
		"Fatty-Tissue",//125
		"Spinal-Cord",//126
		"Soft-Tissue",//127
		"Nerve",//128
		"Bone",//129
		"Air",//130
		"Orbit",//131
		"Tongue",//132
		"Nasal-Structures",//133
		"Globe",//134
		"Teeth",//135
		"Caudate/Putamen",//136
		"Caudate/Putamen",//137
		"Claustrum",//138
		"Claustrum",//139
	};
	Hashtable hemiHash;
	
	private void initHashTable () {
		String left = "Left";
		String right = "Right";
		hemiHash.put(new Integer(1),left);
		hemiHash.put(new Integer(2),left); 
		hemiHash.put(new Integer(3),left);
		hemiHash.put(new Integer(4),left);
		hemiHash.put(new Integer(5),left);
		hemiHash.put(new Integer(6),left);
		hemiHash.put(new Integer(7),left);
		hemiHash.put(new Integer(8),left);
		hemiHash.put(new Integer(9),left);
		hemiHash.put(new Integer(10),left);
		hemiHash.put(new Integer(11),left);
		hemiHash.put(new Integer(12),left);
		hemiHash.put(new Integer(13),left);
		hemiHash.put(new Integer(17),left);
		hemiHash.put(new Integer(18),left);
		hemiHash.put(new Integer(19),left);
		hemiHash.put(new Integer(20),left);
		hemiHash.put(new Integer(25),left);
		hemiHash.put(new Integer(26),left);
		hemiHash.put(new Integer(27),left);
		hemiHash.put(new Integer(28),left);
		hemiHash.put(new Integer(29),left);
		hemiHash.put(new Integer(30),left);
		hemiHash.put(new Integer(31),left);
		hemiHash.put(new Integer(32),left);
		hemiHash.put(new Integer(33),left);
		hemiHash.put(new Integer(34),left);
		hemiHash.put(new Integer(35),left);
		hemiHash.put(new Integer(36),left);
		hemiHash.put(new Integer(37),left);
		hemiHash.put(new Integer(38),left);
		hemiHash.put(new Integer(39),left);
		hemiHash.put(new Integer(40),right);
		hemiHash.put(new Integer(41),right);
		hemiHash.put(new Integer(42),right);
		hemiHash.put(new Integer(43),right);
		hemiHash.put(new Integer(44),right);
		hemiHash.put(new Integer(45),right);
		hemiHash.put(new Integer(46),right);
		hemiHash.put(new Integer(47),right);
		hemiHash.put(new Integer(48),right);
		hemiHash.put(new Integer(49),right);
		hemiHash.put(new Integer(50),right);
		hemiHash.put(new Integer(51),right);
		hemiHash.put(new Integer(52),right);
		hemiHash.put(new Integer(53),right);
		hemiHash.put(new Integer(54),right);
		hemiHash.put(new Integer(55),right);
		hemiHash.put(new Integer(56),right);
		hemiHash.put(new Integer(57),right);
		hemiHash.put(new Integer(58),right);
		hemiHash.put(new Integer(59),right);
		hemiHash.put(new Integer(60),right);
		hemiHash.put(new Integer(61),right);
		hemiHash.put(new Integer(62),right);
		hemiHash.put(new Integer(63),right);
		hemiHash.put(new Integer(64),right);
		hemiHash.put(new Integer(65),right);
		hemiHash.put(new Integer(66),right);
		hemiHash.put(new Integer(67),right);
		hemiHash.put(new Integer(68),right);
		hemiHash.put(new Integer(69),right);
		hemiHash.put(new Integer(70),right);
		hemiHash.put(new Integer(71),right);
		hemiHash.put(new Integer(73),left);
		hemiHash.put(new Integer(74),right);
		hemiHash.put(new Integer(75),left);
		hemiHash.put(new Integer(76),right);
		hemiHash.put(new Integer(78),left);
		hemiHash.put(new Integer(79),right);
		hemiHash.put(new Integer(81),left);
		hemiHash.put(new Integer(82),right);
		hemiHash.put(new Integer(83),left);
		hemiHash.put(new Integer(84),right);
		hemiHash.put(new Integer(96),left);
		hemiHash.put(new Integer(97),right);
		hemiHash.put(new Integer(100),left);
		hemiHash.put(new Integer(101),left);
		hemiHash.put(new Integer(102),left);
		hemiHash.put(new Integer(103),left);
		hemiHash.put(new Integer(104),left);
		hemiHash.put(new Integer(105),left);
		hemiHash.put(new Integer(106),left);
		hemiHash.put(new Integer(107),left);
		hemiHash.put(new Integer(108),left);
		hemiHash.put(new Integer(109),right);
		hemiHash.put(new Integer(110),right);
		hemiHash.put(new Integer(111),right);
		hemiHash.put(new Integer(112),right);
		hemiHash.put(new Integer(113),right);
		hemiHash.put(new Integer(114),right);
		hemiHash.put(new Integer(115),right);
		hemiHash.put(new Integer(116),right);
		hemiHash.put(new Integer(117),right);
		hemiHash.put(new Integer(136),left); 
		hemiHash.put(new Integer(137),right);
		hemiHash.put(new Integer(138),left);
		hemiHash.put(new Integer(139),right);
	}
		  public void init(ServletConfig config) throws ServletException {
			  super.init(config);
			  hemiHash = new Hashtable();
	          initHashTable();
		  }
    
		  /** Destroys the servlet.
		   */
		  public void destroy() {
        
		  }
	  
	/** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
		  * @param request servlet request
		  * @param response servlet response
		  */
		 protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		 throws ServletException, java.io.IOException {
			System.out.println("Recd the post\n");
		 }
	 
		/** Handles the HTTP <code>GET</code> method.
			 * @param request servlet request
			 * @param response servlet response
			 */
			protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
				try {
					int cnt;
					System.out.println("getAsegRegionVolumes Servlet invoked ");
					String contentType = "application/x-java-serialized-object";
					response.setContentType(contentType);
					String sessionId  = request.getParameter("sessionId");
					System.out.println("Session id is "+sessionId);
					ObjectOutputStream outToApplet = new ObjectOutputStream(response.getOutputStream());
					outToApplet.useProtocolVersion(ObjectStreamConstants.PROTOCOL_VERSION_1);
					CriteriaCollection cc = new CriteriaCollection("AND");
					cc.addClause("fs:automaticSegmentationData.mrSession_ID",sessionId);
					ItemCollection ic = org.nrg.xft.search.ItemSearch.GetItems(cc,null,false);
					XFTItem item = (XFTItem)ic.getFirst();
					//FsAutomaticsegmentationdata aseg = new FsAutomaticsegmentationdata(item);
					for (int i=0;i<140;i++) {
						String regName = getRegionName(i);
						String hemis =  getRegionHemisphere(i);
						if (regName.equalsIgnoreCase(""))
							cnt=0;
						else
							//cnt = aseg.getRegionVoxelCount(regName, hemis).intValue();
						//outToApplet.writeInt(cnt);
						outToApplet.flush();
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}	
				
			}
			
	/** Handles the HTTP <code>POST</code> method.
		   * @param request servlet request
		   * @param response servlet response
		   */
		  protected void doPost(HttpServletRequest request, HttpServletResponse response)
		  throws ServletException, java.io.IOException {
			  processRequest(request, response);
		  }
    
		  /** Returns a short description of the servlet.
		   */
		  public String getServletInfo() {
			  return "Short description";
		  }
    
    	  private String getRegionHemisphere(int i) {
    	  	String rtn=null;
			if (hemiHash.containsKey(new Integer(i)))
				rtn=(String)hemiHash.get(new Integer(i));
			return rtn;	     	  		
    	  }
    
    	  private String getRegionName(int i) {
    	  	 	return regionName[i];
    	  }
    	  
 
}
