package com.dermacare.doctorservice.dermacaredoctorutils;


import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dermacare.doctorservice.feignclient.KeyCloakFeign;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RetryableException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

@Component
public class AutoCheckJwtToken {
	
	@Autowired
	private KeyCloakFeign keyCloakFeign;
	
	@Autowired
	private AutoReqForNewAccessTokenBeforeTokenExpiration autoReqForNewAccessToken;
	
	public String access_token;
	public Long expires_in;
		
	 @Retryable(value = {RetryableException.class,NotAuthorizedException.class,ForbiddenException.class,NotFoundException.class,BadRequestException.class,InternalServerErrorException.class}, maxAttempts = 4, backoff = @Backoff(delay = 4000))
	 public void autoCheckJwtToken(){ // CHECK TOKEN FOR SERVICE PRESENT OT NOT
		 //System.out.println("autoCheckJwtToken method invoked");
			 if(access_token == null) {
				 MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
				 form.add("grant_type", "client_credentials");
				 form.add("client_id", "clinicadminservice@8080");
				 form.add("client_secret", "OBBIAnghPvfF7kVkI4pTj2jv2ndmoa0g");
				  Map<String, Object> data = keyCloakFeign.getToken(form);
				 // System.out.println(data);
				  if(data != null) {
				 Map<String,String> token  =  new ObjectMapper().convertValue(data,new TypeReference<Map<String,String>>() {});
				 access_token = "Bearer "+token.get("access_token");
				 expires_in = Long.valueOf(token.get("expires_in"));
				  System.out.println( access_token);
				  System.out.println( expires_in);
				  // scheduler(utilityForStoreJwtTokenAndExpiryTime.getExpires_in() - 50);
				  }}
	            }
	 	
	 	 
		@Recover
		public void backOffMessage(RetryableException e) {
			System.out.println(e.getMessage());
		}
				
				
		  @Scheduled(initialDelay = 1000 * 60 * 1, fixedRate = 1000 * 60 * 8) 
			 public void autoCheckJwtTokenWithScheduler(){
				System.out.println("autoCheckJwtToken five mts methd");
			  try {
			  autoReqForNewAccessToken.reqforNewServiceToken();
			  }catch(Exception e) {}
		  }

}
