package com.dermacare.category_services.util;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.dermacare.category_services.feign.KeyCloakFeign;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.RetryableException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

@Component
public class AutoCheckJwtToken {
	
	@Autowired
	private UtilityForStoreJwtTokenAndExpiryTime utilityForStoreJwtTokenAndExpiryTime;
	
	@Autowired
	private KeyCloakFeign keyCloakFeign;
	
	@Autowired
	private AutoReqForNewAccessTokenBeforeTokenExpiration autoReqForNewAccessToken;
		
	 @Retryable(value = {RetryableException.class,NotAuthorizedException.class,ForbiddenException.class,NotFoundException.class,BadRequestException.class,InternalServerErrorException.class}, maxAttempts = 4, backoff = @Backoff(delay = 6000))
	 public void autoCheckJwtToken(){ // CHECK TOKEN FOR SERVICE PRESENT OT NOT
		 System.out.println("autoCheckJwtToken method invoked");
		 try {
			 if(utilityForStoreJwtTokenAndExpiryTime.getAccess_token() == null && utilityForStoreJwtTokenAndExpiryTime.getExpires_in() == null) {
				 MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
				 form.add("grant_type", "client_credentials");
				 form.add("client_id", "clinicadminservice@8080");
				 form.add("client_secret", "OBBIAnghPvfF7kVkI4pTj2jv2ndmoa0g");
				  Map<String, Object> data = keyCloakFeign.getToken(form);
				 // System.out.println(data);
				  if(data != null) {
				  utilityForStoreJwtTokenAndExpiryTime  =  new ObjectMapper().convertValue(data,UtilityForStoreJwtTokenAndExpiryTime.class);
				  utilityForStoreJwtTokenAndExpiryTime.setAccess_token("Bearer "+utilityForStoreJwtTokenAndExpiryTime.getAccess_token());
				  System.out.println(utilityForStoreJwtTokenAndExpiryTime);
				  System.out.println(utilityForStoreJwtTokenAndExpiryTime.getExpires_in());
				  // scheduler(utilityForStoreJwtTokenAndExpiryTime.getExpires_in() - 50);
				  }}}catch(FeignException e) {}
	            }
	 	
	 	 
		@Recover
		public void backOffMessage(RetryableException e) {
			System.out.println(e.getMessage());
		}
				
				
		  @Scheduled(initialDelay = 1000 * 60 * 2, fixedRate = 1000 * 60 * 8) 
			 public void autoCheckJwtTokenWithScheduler(){
				// System.out.println("autoCheckJwtToken five mts methd");
			  try {
			  autoReqForNewAccessToken.reqforNewServiceToken();
			  }catch(Exception e) {}
		  }

}
