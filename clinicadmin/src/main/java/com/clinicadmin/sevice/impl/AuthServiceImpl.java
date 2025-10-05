package com.clinicadmin.sevice.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import com.clinicadmin.dto.AccessTokenAndRefreshToken;
import com.clinicadmin.dto.ClinicCredentialsDTO;
import com.clinicadmin.dto.DoctorLoginDTO;
import com.clinicadmin.dto.Response;
import com.clinicadmin.entity.ClinicAdminDeviceTokenEntity;
import com.clinicadmin.entity.DoctorLoginCredentials;
import com.clinicadmin.entity.RefreshJwtToken;
import com.clinicadmin.repository.ClinicAdminWebFcmTokenRepository;
import com.clinicadmin.repository.DoctorLoginCredentialsRepository;
import com.clinicadmin.repository.RefreshTokenRepository;
import com.clinicadmin.service.AuthService;
import com.clinicadmin.utils.ExtractFeignMessage;
import com.clinicadmin.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;

@Service
public class AuthServiceImpl implements AuthService {	
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private DoctorLoginCredentialsRepository credentialsRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private ClinicAdminWebFcmTokenRepository clinicAdminWebFcmTokenRepository;
	 
	private String refreshToken;
	
	public String hospitalId;
	public String branchId;
	public String hospitalName;
		
	
	@Override
	public ResponseEntity<Response> cliniLogin(ClinicCredentialsDTO clinicCredentialsDTO) {
		Response response = new Response();	
		try {	
			authManager.authenticate(new UsernamePasswordAuthenticationToken(clinicCredentialsDTO.getUsername(),
					clinicCredentialsDTO.getPassword()));
			List<String> roles = new ArrayList<>();
			roles.add("ROLE_CLINICADMIN");
			String accessToken = jwtUtil.generateJwtToken(clinicCredentialsDTO.getUsername(),roles);
			if(refreshTokenRepository.findByTokenName("JwtRefreshToken") != null) {
			       refreshTokenRepository.delete(refreshTokenRepository.findByTokenName("JwtRefreshToken"));
			       refreshToken = jwtUtil.generateJwtToken(clinicCredentialsDTO.getUsername(),roles);
			       RefreshJwtToken tokens  = new RefreshJwtToken();
			       tokens.setTokenName("JwtRefreshToken");
			       tokens.setJwtToken(refreshToken);
			       refreshTokenRepository.save(tokens);}
			       else {
			       refreshToken = jwtUtil.generateJwtToken(clinicCredentialsDTO.getUsername(),roles); 
			       RefreshJwtToken tokens  = new RefreshJwtToken();
			       tokens.setTokenName("JwtRefreshToken");
			       tokens.setJwtToken(refreshToken);
			       refreshTokenRepository.save(tokens);
			       }
				   response.setMessage("Login successful");
				   response.setStatus(200);
				   AccessTokenAndRefreshToken tokens = new AccessTokenAndRefreshToken();
				   tokens.setAccessToken(accessToken);
	               tokens.setRefreshToken(refreshToken);
	               tokens.setAccessTokenExpireTime(jwtUtil.formattedTimeByZone);
	               tokens.setHospitalId(hospitalId);
	               tokens.setHospitalName(hospitalName);
				   response.setData(tokens);				
				   response.setSuccess(true);
				
				   ClinicAdminDeviceTokenEntity c = new ClinicAdminDeviceTokenEntity();
				   ClinicAdminDeviceTokenEntity obj = clinicAdminWebFcmTokenRepository.findByClinicIdAndBranchId(hospitalId,branchId);
				   if(obj != null) {
					   if(clinicCredentialsDTO.getClinicAdminWebFcmToken() != null) {
						   obj.setClinicAdminWebFcmToken(clinicCredentialsDTO.getClinicAdminWebFcmToken());
						   clinicAdminWebFcmTokenRepository.save(obj);} 
				   }else{
				   c.setClinicId(hospitalId);
				   c.setBranchId(branchId);
				   if(clinicCredentialsDTO.getClinicAdminWebFcmToken() != null) {
				   c.setClinicAdminWebFcmToken(clinicCredentialsDTO.getClinicAdminWebFcmToken());}
				   clinicAdminWebFcmTokenRepository.save(c);}
		}catch(FeignException e) {
			response = new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null);
		}
		return ResponseEntity.status(response.getStatus()).body(response);
	}
	
	
	@Override
	public ResponseEntity<Response> doctorLogin(String userName) {
		Response responseDTO = new Response();
        try {
		Optional<DoctorLoginCredentials> credentialsOptional = credentialsRepository
				.findByUsername(userName);
		//System.out.println(credentialsOptional);
		if (credentialsOptional.isPresent()) {
			DoctorLoginCredentials credentials = credentialsOptional.get();		
  				responseDTO.setData(new ObjectMapper().convertValue(credentials, DoctorLoginDTO.class));
  				//System.out.println(responseDTO.getData());
				responseDTO.setStatus(HttpStatus.OK.value());
				responseDTO.setMessage("Login successful");
				responseDTO.setSuccess(true);
			}else{
				responseDTO.setData(null);
				responseDTO.setStatus(HttpStatus.NOT_FOUND.value());
				responseDTO.setMessage("Invalid Credentials");
				responseDTO.setSuccess(false);
			}
		}catch(Exception e) {
			responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseDTO.setMessage(e.getMessage());
			responseDTO.setSuccess(false);
		}

		return ResponseEntity.status(responseDTO.getStatus()).body(responseDTO);
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
}