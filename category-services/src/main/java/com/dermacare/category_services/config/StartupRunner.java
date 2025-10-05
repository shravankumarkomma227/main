package com.dermacare.category_services.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.dermacare.category_services.util.AutoCheckForJwks;
import com.dermacare.category_services.util.AutoCheckJwtToken;
import com.dermacare.category_services.util.JwtUtil;
import com.dermacare.category_services.util.UtilityForStoreJwtTokenAndExpiryTime;
import feign.FeignException;

@Component
public class StartupRunner implements CommandLineRunner {
	
		
	@Autowired
	private JwtUtil jwtUtil;
		
	@Autowired
	private UtilityForStoreJwtTokenAndExpiryTime utilityForStoreJwtTokenAndExpiryTime;
	
	@Autowired
	private AutoCheckJwtToken autoCheckJwtToken;
	
	@Autowired
	private AutoCheckForJwks autoCheckForJwks;
	
	
	
	@Override
	public void run(String... args) throws Exception {
		keycheck();	
		checkingVariableForToken();
	}

	private void keycheck() {
		try {
			if(jwtUtil.jWKSet == null) {
				autoCheckForJwks.autoCheckForJwks();
			}
			}catch(FeignException e) {}
		}
	
	
	private void checkingVariableForToken(){
		try {			
			 if(utilityForStoreJwtTokenAndExpiryTime.getAccess_token() == null || utilityForStoreJwtTokenAndExpiryTime.getExpires_in() == null) {
			autoCheckJwtToken.autoCheckJwtToken();
			System.out.println("autoCheckJwtToken");}
			}catch(Exception e) {}
	}
	
}
