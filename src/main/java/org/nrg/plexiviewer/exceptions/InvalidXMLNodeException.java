/*
 * org.nrg.plexiViewer.exceptions.InvalidXMLNodeException
 * XNAT http://www.xnat.org
 * Copyright (c) 2014, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 10/4/13 10:50 AM
 */


package org.nrg.plexiviewer.exceptions;

/**
 * @author Mohana
 *
 */
public class InvalidXMLNodeException extends Exception {

public InvalidXMLNodeException() {
}

public InvalidXMLNodeException(String msg) {
	super("Dont know how to deal with element :: " + msg);
}

}
