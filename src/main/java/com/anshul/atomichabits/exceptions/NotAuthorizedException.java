package com.anshul.atomichabits.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class NotAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = -5037535643782503981L;

	public NotAuthorizedException(String message) {
		super(message);
	}
}
