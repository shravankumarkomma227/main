package com.AdminService.config;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.AdminService.util.AutoCheckForJwks;
import com.AdminService.util.AutoCheckJwtToken;
import com.AdminService.util.JwtUtil;
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
		secretKeyGenerator();
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
	
	private void secretKeyGenerator(){
		try {
		 KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
	      SecretKey secretKey = keyGen.generateKey();
	      String encoded = Base64.getEncoder().encodeToString(secretKey.getEncoded());
	      jwtUtil.SECRET = encoded;
		}catch(NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
		}
	} 
	
}
