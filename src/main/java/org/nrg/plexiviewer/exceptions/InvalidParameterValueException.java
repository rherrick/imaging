//Copyright 2005 Harvard University / Howard Hughes Medical Institute (HHMI) All Rights Reserved
package org.nrg.plexiviewer.exceptions;

public class InvalidParameterValueException extends Exception {
	public InvalidParameterValueException() {
	}

	public InvalidParameterValueException(String msg) {
		super("Invalid:: " + msg);
	}
}	
