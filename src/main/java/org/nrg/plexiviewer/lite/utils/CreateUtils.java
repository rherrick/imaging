//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.lite.utils;

import  java.lang.reflect.*;

public class CreateUtils {
	public static Object createObject(String className) {
			  Object object = null;
			   try {
					 Class classDefinition = Class.forName(className);
					 object = classDefinition.newInstance();
				 } catch (InstantiationException e) {
						 System.out.println(e);
				 } catch (IllegalAccessException e) {
						 System.out.println(e);
				 } catch (ClassNotFoundException e) {
					 System.out.println(e);
				 }
					 return object;
			 }
	 
		public  static Object createObject(Constructor constructor, 
											   Object[] arguments) {
				Object object = null;
				try {
				  object = constructor.newInstance(arguments);
				  return object;
				} catch (InstantiationException e) {
					System.out.println(e);
				} catch (IllegalAccessException e) {
					System.out.println(e);
				} catch (IllegalArgumentException e) {
					System.out.println(e);
				} catch (InvocationTargetException e) {
					System.out.println(e);
				}
				return object;
			 }
  
}
