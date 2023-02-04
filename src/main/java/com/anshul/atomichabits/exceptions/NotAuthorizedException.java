package com.anshul.atomichabits.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotAuthorizedException extends RuntimeException {

	public NotAuthorizedException(String message) {
		super(message);
	}
}
