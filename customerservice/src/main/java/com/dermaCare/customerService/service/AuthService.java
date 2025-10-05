package com.dermaCare.customerService.service;

import org.springframework.http.ResponseEntity;
import com.dermaCare.customerService.dto.LoginDTO;
import com.dermaCare.customerService.util.Response;

public interface AuthService {
	
	
	 public ResponseEntity<Response> verifyUserCredentialsAndGenerateAndSendOtp(LoginDTO loginDTO);
		
	 public ResponseEntity<?> verifyOtp(LoginDTO loginDTO);
	
	 public  ResponseEntity<Response> resendOtp(LoginDTO loginDTO);
	 
	  public ResponseEntity<Response> requestForNewJwtTokenByRefreshToken(String refreshToken);
	  
	  public ResponseEntity<Response> generateTokensForOauth2();
}
