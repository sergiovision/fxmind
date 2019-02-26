package com.fxmind.exceptions;

import java.io.File;

public class GenericException extends RuntimeException {

	private File tfile;

	public GenericException( String message, File file ) {
		super(message == null ? "Unknown reason. Please let us know if you see this message." : message);
		setFile(file);
	}

	public GenericException( String message ) {
		this(message, null);
	}

	public File file() {
		return tfile;
	}
	
	public void setFile(File file) {
		tfile = file;
	}
}
