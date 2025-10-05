package com.dermaCare.customerService.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.dermaCare.customerService.dto.AccessTokenAndRefreshToken;
import com.dermaCare.customerService.dto.LoginDTO;
import com.dermaCare.customerService.entity.Customer;
import com.dermaCare.customerService.entity.RefreshJwtToken;
import com.dermaCare.customerService.repository.CustomerRepository;
import com.dermaCare.customerService.repository.RefreshTokenRepository;
import com.dermaCare.customerService.util.JwtUtil;
import com.dermaCare.customerService.util.Response;

@Service
public class AuthServiceImpl implements AuthService  {
	
	 @Autowired
	 private JwtUtil jwtUtil;
	 
	  @Autowired
	  private FirebaseMessagingService firebaseMessagingService;
	  
	  @Autowired
	  public CustomerRepository customerRepository;
	  
	  @Autowired
	  public RefreshTokenRepository refreshTokenRepository;
	  
		   	    
	    private Map<String, String> generatedOtps = new HashMap<>();
	    private Map<String, Long> session = new HashMap<>();
	    private static final long OTP_EXPIRY_MILLIS = 1 * 60 * 1000;
	    private String refreshToken;

	    @Override
	    public ResponseEntity<Response> verifyUserCredentialsAndGenerateAndSendOtp(LoginDTO loginDTO) {
		   Response response = new Response();
	     try {
	    	 if(!isIndianMobileNumber(loginDTO.getMobileNumber())) {
	    		 response.setMessage("Please Enter Valid MobileNumber");
	 	    	response.setStatus(400);
	 	    	response.setSuccess(false);}
	   	    Optional<Customer> custmer = customerRepository.findByMobileNumber(loginDTO.getMobileNumber());
		    if(custmer.isPresent()) {
		    	custmer.get().setDeviceId(loginDTO.getDeviceId());
		    	customerRepository.save(custmer.get());}
		    	String otp = randomNumber();
		    	System.out.println(otp);
		    	if(loginDTO.getDeviceId() != null) {
		    		firebaseMessagingService.sendPushNotification(
		    			    loginDTO.getDeviceId(),
		    			    "🔐 Hello,Here’s your OTP!",
		    			    "Use " + otp + " to verify your login. Expires in 1 minute.",
		    			    "OTP",
		    			    "OTPVerificationScreen",
		    			    "default"
		    			);
		    	generatedOtps.put(loginDTO.getMobileNumber(),otp);
		    	session.put(loginDTO.getMobileNumber(),System.currentTimeMillis());
		    	response.setMessage("OTP Sent Successfully");
		    	response.setStatus(200);
		    	response.setSuccess(true);}
		    	else {
		    		response.setMessage("Please Provide DeviceId");
			    	response.setStatus(400);
			    	response.setSuccess(false);}
		    }catch(Exception e) {
		    	response.setMessage(e.getMessage());
		    	response.setStatus(500);
		    	response.setSuccess(false);}
	     return ResponseEntity.status(response.getStatus()).body(response);
	}
	   
	   
	     
	    private boolean isIndianMobileNumber(String mobileNumber) {
	        mobileNumber = mobileNumber.replaceAll("[\\s\\-()]", "");
	        String regex = "^(\\+91|91|0)?[6-9]\\d{9}$";
	        return mobileNumber.matches(regex);
	    }
	 
	     private String randomNumber() {
	         Random random = new Random();    
	         int sixDigitNumber = 100000 + random.nextInt(900000); // Generates number from 100000 to 999999
	         return String.valueOf(sixDigitNumber);
	     }
	     
	     
	    
	   public ResponseEntity<?> verifyOtp(LoginDTO loginDTO){
		   Response response = new Response();
		   try {
			   String otp = generatedOtps.get(loginDTO.getMobileNumber());
			   System.out.println(otp);
			   long createdTime = session.get(loginDTO.getMobileNumber());
			   if(!isExpired(createdTime)) {
				   if(loginDTO.getOtp().equals(otp)) {
				   try {
					  List<String> roles = new ArrayList<>();
					  roles.add("ROLE_CUSTOMER");
			       String accessToken = jwtUtil.generateJwtToken(loginDTO.getMobileNumber(), roles);
			       if(refreshTokenRepository.findByTokenName("JwtRefreshToken") != null) {
			    	   refreshTokenRepository.delete(refreshTokenRepository.findByTokenName("JwtRefreshToken"));
			       refreshToken = jwtUtil.generateJwtToken(loginDTO.getMobileNumber(), roles);
			       RefreshJwtToken tokens  = new RefreshJwtToken();
			       tokens.setTokenName("JwtRefreshToken");
			       tokens.setJwtToken(refreshToken);
			       refreshTokenRepository.save(tokens);}
			       else {
			       refreshToken = jwtUtil.generateJwtToken(loginDTO.getMobileNumber(), roles); 
			       RefreshJwtToken tokens  = new RefreshJwtToken();
			       tokens.setTokenName("JwtRefreshToken");
			       tokens.setJwtToken(refreshToken);
			       refreshTokenRepository.save(tokens);
			       }
				   response.setMessage("OTP Successfully Verified");
				   response.setStatus(200);
				   AccessTokenAndRefreshToken tokens = new AccessTokenAndRefreshToken();
				   tokens.setAccessToken(accessToken);
                   tokens.setRefreshToken(refreshToken);
                   tokens.setAccessTokenExpireTime(jwtUtil.formattedTimeByZone);
				   response.setData(tokens);				
				   response.setSuccess(true);
				   return ResponseEntity.status(response.getStatus()).body(response);
				   }catch(Exception ex) {
					   return ResponseEntity.status(500)
			                    .body("Token generation failed: " + ex.getMessage());			       
				   }
				   }else {
					   response.setMessage("Invalid OTP Please Enter Correct OTP");
					   response.setStatus(400);
					   return ResponseEntity.status(response.getStatus()).body(response);}
			   }else {
				   response.setMessage("OTP Expired Please Click On Resend OTP");
				   response.setStatus(410);
				   return ResponseEntity.status(response.getStatus()).body(response);
			   }}catch(Exception e) {
				   response.setMessage(e.getMessage());
				   response.setStatus(500);
				   return ResponseEntity.status(response.getStatus()).body(response);}
	  }

	   
	   private boolean isExpired(long createdAt) {
	       return System.currentTimeMillis() - createdAt > OTP_EXPIRY_MILLIS;
	   }
	   
	 
	   
	   public  ResponseEntity<Response> resendOtp(LoginDTO loginDTO){
		   Response response = new Response();
		   try {
			   if(!isIndianMobileNumber(loginDTO.getMobileNumber())) {
		    		response.setMessage("Please Enter Valid MobileNumber");
		 	    	response.setStatus(400);
		 	    	response.setSuccess(false);
		 	    	return ResponseEntity.status(response.getStatus()).body(response);}		   
			    String otp = randomNumber();
			    if(loginDTO.getDeviceId() != null) {
			    	firebaseMessagingService.sendPushNotification(
		    			    loginDTO.getDeviceId(),
		    			    "🔐 Hello,Here’s your ResendOTP!",
		    			    "Use " + otp + " to verify your login. Expires in 1 minute.",
		    			    "OTP",
		    			    "OTPVerificationScreen",
		    			    "default"
		    			);
		    	generatedOtps.put(loginDTO.getMobileNumber(),otp);
		    	session.put(loginDTO.getMobileNumber(),System.currentTimeMillis());
		    	response.setMessage("OTP Sent Successfully");
				response.setStatus(200);
				response.setSuccess(true);
		    	}else{
			    	response.setMessage("Please Provide DeviceId");
					response.setStatus(400);}
		        }catch(Exception e) {
			    response.setMessage(e.getMessage());
			    response.setStatus(500);}
		        return ResponseEntity.status(response.getStatus()).body(response);
	   }
	   
	   
	   public ResponseEntity<Response> requestForNewJwtTokenByRefreshToken(String refreshToken){
		   try {
			  Response res = jwtUtil.validateRefreshToken(refreshToken);
			  return ResponseEntity.status(res.getStatus()).body(res);
		   }catch(Exception e) {
			   Response respnse = new Response();
			   respnse.setMessage(e.getMessage());
			   respnse.setStatus(500);
			   return ResponseEntity.status(respnse.getStatus()).body(respnse);
		   }
	   }
	   
	   
	   public ResponseEntity<Response> generateTokensForOauth2(){
		   Response response = new Response();
		   try {
		   List<String> roles = new ArrayList<>();
			  roles.add("ROLE_CUSTOMER");
	       String accessToken = jwtUtil.generateJwtToken("DERAMACARE_USER", roles);
	       if(refreshTokenRepository.findByTokenName("JwtRefreshToken") != null) {
	       refreshTokenRepository.delete(refreshTokenRepository.findByTokenName("JwtRefreshToken"));
	       refreshToken = jwtUtil.generateJwtToken("DERAMACARE_USER", roles);
	       RefreshJwtToken tokens  = new RefreshJwtToken();
	       tokens.setTokenName("JwtRefreshToken");
	       tokens.setJwtToken(refreshToken);
	       refreshTokenRepository.save(tokens);
	       }else{
	       refreshToken = jwtUtil.generateJwtToken("DERAMACARE_USER", roles); 
	       RefreshJwtToken tokens  = new RefreshJwtToken();
	       tokens.setTokenName("JwtRefreshToken");
	       tokens.setJwtToken(refreshToken);
	       refreshTokenRepository.save(tokens);}
		   response.setMessage("Login Successful");
		   response.setStatus(200);
		   AccessTokenAndRefreshToken tokens = new AccessTokenAndRefreshToken();
		   tokens.setAccessToken(accessToken);
           tokens.setRefreshToken(refreshToken);
           tokens.setAccessTokenExpireTime(jwtUtil.formattedTimeByZone);
		   response.setData(tokens);				
		   response.setSuccess(true); 
		   }catch(Exception e) {}
		   return ResponseEntity.status(response.getStatus()).body(response);
     }
	      
}
