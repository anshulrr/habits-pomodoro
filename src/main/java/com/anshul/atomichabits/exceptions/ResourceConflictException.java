package com.anshul.atomichabits.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ResourceConflictException extends RuntimeException {

	private static final long serialVersionUID = 6602951767538627743L;

	public ResourceConflictException(String message) {
		super(message);
	}
}
