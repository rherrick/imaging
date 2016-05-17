//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.converter;

import java.lang.reflect.Constructor;
import java.net.URISyntaxException;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.nrg.plexiviewer.lite.UserSelection;
import org.nrg.plexiviewer.lite.io.PlexiImageFile;
import org.nrg.plexiviewer.lite.utils.CreateUtils;

public class ConverterUtils {
	

	public synchronized static PlexiImageFile convert(UserSelection u) {
		int exitStatus = 0;
		String converterClassName = "org.nrg.plexiviewer.converter.DefaultConverter" ;
/*		if (u.getLoResType()!=null) {
			LoRes lres=PlexiSpecDocReader.GetInstance().getSpecDoc(u.getProject()).getViewableItem(u.getDataType()).getLoRes(u.getLoResType());
			converterClassName=lres.getConverterClassName();
            System.out.println("Converter Class is " + converterClassName);
		}else {
			Thumbnail tbNail=PlexiSpecDocReader.GetInstance().getSpecDoc(u.getProject()).getViewableItem(u.getDataType()).getThumbnail();
			converterClassName = tbNail.getConverterClassName(); 
		}*/
		PlexiImageFile pf=null;
       // System.out.println("ConverterUtils:: start ");
		try {
			Class[] intArgsClass = new Class[] {u.getClass()};
            //System.out.println("ConverterUtils:: class created ");
			Object[] intArgs = new Object[] {(UserSelection)u.clone()};
            //System.out.println("ConverterUtils:: clone created ");
			Constructor intArgsConstructor;
			Class imgConverterClass = Class.forName(converterClassName);
			intArgsConstructor = imgConverterClass.getConstructor(intArgsClass);
            //System.out.println("Converter Cconstructor obtained");
			plexiLoResConverterI imgConverter = (plexiLoResConverterI) CreateUtils.createObject(intArgsConstructor, intArgs);
			System.out.println("Invoking " + imgConverter.getClass().getName());
			exitStatus= imgConverter.convertAndSave(u);
			if (exitStatus!=0) {
				System.out.println("Couldnt launch the conveter");
				return pf;
			}else {
				pf=imgConverter.getFileLocationAndName();
			}
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (NoSuchMethodException e1) {
			System.out.println(e1);
		} catch (URISyntaxException e2) {
            System.out.println(e2);
        } catch (Exception e3) {
        	System.out.println(e3);
        }
        
		System.out.println("Converter Utils created file in " + pf.toString());
		return pf;
	}
		
	public static void matchPattern(String fileName, String pattern) {
		String rtn=null;
		try {
			RE r = new RE(pattern);
			boolean found = r.match(fileName);
		}catch(RESyntaxException reE) {
			reE.printStackTrace();
		}
	}
		
}
