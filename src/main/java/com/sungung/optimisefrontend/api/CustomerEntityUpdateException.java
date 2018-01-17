package com.sungung.optimisefrontend.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomerEntityUpdateException extends RuntimeException {

	public CustomerEntityUpdateException(String error) {
		super(error);
	}
	
}
