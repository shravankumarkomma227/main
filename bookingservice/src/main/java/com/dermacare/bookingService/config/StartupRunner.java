package com.dermacare.bookingService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.dermacare.bookingService.util.AutoCheckForJwks;
import com.dermacare.bookingService.util.AutoCheckJwtToken;
import com.dermacare.bookingService.util.JwtUtil;
import feign.FeignException;

@Component
public class StartupRunner implements CommandLineRunner {
	
	@Autowired
	private JwtUtil jwtUtil;
	
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
			 if(autoCheckJwtToken.access_token == null || autoCheckJwtToken.access_token == null) {
			autoCheckJwtToken.autoCheckJwtToken();
			System.out.println("autoCheckJwtToken");}
			}catch(Exception e) {}
	}
	
	
}
