package com.dermacare.doctorservice.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import com.dermacare.doctorservice.dermacaredoctorutils.JwtUtil;
import com.dermacare.doctorservice.dto.AccessTokenAndRefreshToken;
import com.dermacare.doctorservice.dto.DoctorLoginDTO;
import com.dermacare.doctorservice.dto.ExtractFeignMessage;
import com.dermacare.doctorservice.dto.Response;
import com.dermacare.doctorservice.feignclient.ClinicAdminServiceClient;
import com.dermacare.doctorservice.model.DoctorDeviceIdsEntity;
import com.dermacare.doctorservice.model.RefreshJwtToken;
import com.dermacare.doctorservice.repository.DoctorDeviceIdsRepository;
import com.dermacare.doctorservice.repository.RefreshTokenRepository;
import com.dermacare.doctorservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;

@Service
public class AuthServiceImpl implements AuthService {	
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private DoctorDeviceIdsRepository doctorDeviceIdsRepository;
		
	private String refreshToken;
	
	public String hospitalId;
	
	public String doctorId;
		
	@Override
	public ResponseEntity<Response> doctorLogin(DoctorLoginDTO doctorLoginDTO) {
		Response response = new Response();	     
		try {	
			authManager.authenticate(new UsernamePasswordAuthenticationToken(doctorLoginDTO.getUsername(),
					doctorLoginDTO.getPassword()));
			//System.out.println("invoked");
			List<String> roles = new ArrayList<>();
			roles.add("ROLE_DOCTOR");
			String accessToken = jwtUtil.generateJwtToken(doctorLoginDTO.getUsername(),roles);
			//System.out.println(accessToken);
			if(refreshTokenRepository.findByTokenName("JwtRefreshToken") != null) {
				//System.out.println("invoked");
			       refreshTokenRepository.delete(refreshTokenRepository.findByTokenName("JwtRefreshToken"));
			       refreshToken = jwtUtil.generateJwtToken(doctorLoginDTO.getUsername(),roles);
			       RefreshJwtToken tokens  = new RefreshJwtToken();
			       tokens.setTokenName("JwtRefreshToken");
			       tokens.setJwtToken(refreshToken);
			       refreshTokenRepository.save(tokens);}
			       else {
			       refreshToken = jwtUtil.generateJwtToken(doctorLoginDTO.getUsername(),roles); 
			       RefreshJwtToken tokens  = new RefreshJwtToken();
			       tokens.setTokenName("JwtRefreshToken");
			       tokens.setJwtToken(refreshToken);
			       refreshTokenRepository.save(tokens);}
				   response.setMessage("Login successful");
				   response.setStatus(200);
				   AccessTokenAndRefreshToken tokens = new AccessTokenAndRefreshToken();
				   tokens.setAccessToken(accessToken);
	               tokens.setRefreshToken(refreshToken);
	               tokens.setAccessTokenExpireTime(jwtUtil.formattedTimeByZone);
				   response.setData(tokens);				
				   response.setSuccess(true);
				   response.setHosipitalId(hospitalId);
				   response.setDoctorId(doctorId);
				   DoctorDeviceIdsEntity d = new DoctorDeviceIdsEntity();
				   DoctorDeviceIdsEntity obj = doctorDeviceIdsRepository.findByDoctorId(doctorId);
				   if(obj != null) {
					   if(doctorLoginDTO.getDoctorWebFcmToken() != null) {
						   obj.setDoctorWebFcmToken(doctorLoginDTO.getDoctorWebFcmToken());}
						   if(doctorLoginDTO.getDoctorMobileFcmToken() != null) {
							   obj.setDoctorMobileFcmToken(doctorLoginDTO.getDoctorMobileFcmToken());}   
						   doctorDeviceIdsRepository.save(obj);
				   }else{
				   d.setDoctorId(doctorId);
				   if(doctorLoginDTO.getDoctorWebFcmToken() != null) {
				   d.setDoctorWebFcmToken(doctorLoginDTO.getDoctorWebFcmToken());}
				   if(doctorLoginDTO.getDoctorMobileFcmToken() != null) {
				   d.setDoctorMobileFcmToken(doctorLoginDTO.getDoctorMobileFcmToken());}
				   d.setHospitalId(hospitalId);
				   doctorDeviceIdsRepository.save(d);}
		}catch(FeignException e) {
			response = new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null);
		}
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
}
