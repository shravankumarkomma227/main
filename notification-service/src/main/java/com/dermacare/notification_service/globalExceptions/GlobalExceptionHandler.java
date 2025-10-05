package com.dermacare.notification_service.globalExceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.dermacare.notification_service.dto.Response;
import feign.FeignException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(FeignException.class)
	public ResponseEntity<Response> handleFeignExceptions(FeignException ex){
		Response res = new Response();
		res.setMessage(ex.getMessage());
		res.setStatus(ex.status());
		res.setSuccess(false);
		return ResponseEntity.status(res.getStatus()).body(res);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleWithRoorExceptionClass(Exception ex){
		Response res = new Response();
		res.setMessage(ex.getMessage());
		res.setStatus(500);
		res.setSuccess(false);
		return ResponseEntity.status(res.getStatus()).body(res);
	}

}
