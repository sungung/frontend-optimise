package com.sungung.optimisefrontend.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException {
	public CustomerNotFoundException(Long id){
		super("Not found customer by id '" + id + "'.");
	}
	public CustomerNotFoundException(String email){
		super("Not found customer by email '" + email + "'.");
	}	
}
