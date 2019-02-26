package com.fxmind.exceptions;

public abstract class ProcessingEvent extends RuntimeException {

	public ProcessingEvent(String message) {
        super(message == null ? "Unknown reason. Please let us know if you see this message." : message);
	}

}
