package com.dermacare.bookingService.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import com.dermacare.bookingService.feign.KeyCloakFeign;
import feign.RetryableException;

@Component
public class AutoCheckForJwks {
	
		
	@Autowired
	private KeyCloakFeign keyCloakFeign;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Retryable(value = {RetryableException.class}, maxAttempts = 4, backoff = @Backoff(delay = 4000))
	public void autoCheckForJwks() { // TO GET JWKS FROM AUTH SERVICE
		    // FOR REQ NEW KEYS
		jwtUtil.jWKSet = keyCloakFeign.getJWKS();
	}
	
		
	@Recover
	public void message(RetryableException e) {
		System.out.println(e.getMessage());
	}	
	
}
