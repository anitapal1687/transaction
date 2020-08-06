package com.example.Transaction.exception;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(value={ ResourceNotFoundException.class })
	public @ResponseBody ResponseEntity<CustomErrorResponse> handleMyOwnException(final HttpServletRequest request,
			final RuntimeException ex) {
		CustomErrorResponse response = new CustomErrorResponse();

		response.setStatus(HttpStatus.NOT_FOUND.value());
		response.setMessage(HttpStatus.NOT_FOUND.toString());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

}
