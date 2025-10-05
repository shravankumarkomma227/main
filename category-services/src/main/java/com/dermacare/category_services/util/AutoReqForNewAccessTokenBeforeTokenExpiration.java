package com.dermacare.category_services.util;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.dermacare.category_services.feign.KeyCloakFeign;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RetryableException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;

@Component
public class AutoReqForNewAccessTokenBeforeTokenExpiration {

	@Autowired
	private UtilityForStoreJwtTokenAndExpiryTime utilityForStoreJwtTokenAndExpiryTime;
	
	@Autowired
	private KeyCloakFeign keyCloakFeign;
	
	@Retryable(value = {RetryableException.class,NotAuthorizedException.class,ForbiddenException.class,NotFoundException.class,BadRequestException.class,InternalServerErrorException.class}, maxAttempts = 8, backoff = @Backoff(delay = 10000))
	public void reqforNewServiceToken() { // TO GET JWKS FROM AUTH SERVICE
		// System.out.println("autoCheckForJwks method invoked");
		//try {
		if(utilityForStoreJwtTokenAndExpiryTime.getAccess_token() == null && utilityForStoreJwtTokenAndExpiryTime.getExpires_in() == null) {
			 MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
			 form.add("grant_type", "client_credentials");
			 form.add("client_id", "clinicadminservice@8080");
			 form.add("client_secret", "OBBIAnghPvfF7kVkI4pTj2jv2ndmoa0g");
			  Map<String, Object> data = keyCloakFeign.getToken(form);
			  System.out.println(data);
			  if(data != null) {
			  utilityForStoreJwtTokenAndExpiryTime  =  new ObjectMapper().convertValue(data,UtilityForStoreJwtTokenAndExpiryTime.class);
			  //System.out.println(utilityForStoreJwtTokenAndExpiryTime);
	          }}
//		}catch(FeignException e) {
//			System.out.println( e.getClass());
//		}
	}
	
	
	@Recover
	public void message(RetryableException e) {
		System.out.println(e.getMessage());
	}	
	
}
