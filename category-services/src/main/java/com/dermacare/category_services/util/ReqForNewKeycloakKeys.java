package com.dermacare.category_services.util;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import com.dermacare.category_services.dto.JwksResponse;
import com.dermacare.category_services.feign.KeyCloakFeign;
import feign.FeignException;
import feign.RetryableException;

@Component
public class ReqForNewKeycloakKeys {

	@Autowired
	private KeyCloakFeign keyCloakFeign;
			
				
			 @Retryable(value = {RetryableException.class}, maxAttempts = 4, backoff = @Backoff(delay = 4000))			
			 public JwksResponse keys() { // FOR REQ NEW KEYS
					return keyCloakFeign.getJWKS();
			}
			
			@Recover
			public void backOffMessages(FeignException e) {
				System.out.println(e.getMessage());
			}		
}
