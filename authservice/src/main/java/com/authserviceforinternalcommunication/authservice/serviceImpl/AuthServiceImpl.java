package com.authserviceforinternalcommunication.authservice.serviceImpl;


import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.authserviceforinternalcommunication.authservice.dto.ServiceDetailsDto;
import com.authserviceforinternalcommunication.authservice.entity.ServiceDetailsEntity;
import com.authserviceforinternalcommunication.authservice.repository.AuthRepository;
import com.authserviceforinternalcommunication.authservice.repository.KeysRepository;
import com.authserviceforinternalcommunication.authservice.service.AuthService;
import com.authserviceforinternalcommunication.authservice.util.JwtUtil;
import com.authserviceforinternalcommunication.authservice.util.Response;


@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private AuthRepository authRepository;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private KeysRepository keysRepository;
	
	
	public ResponseEntity<Response> serviceRegistration(ServiceDetailsDto serviceDetailsDto){
		Response res = new Response(); 
		//System.out.println(serviceDetailsDto);
		  try
		    {
			Optional<ServiceDetailsEntity> objectEntity = authRepository.findByUserName(serviceDetailsDto.getUserName());
			//System.out.println(objectEntity);
			if(!objectEntity.isPresent()){
			ServiceDetailsEntity entity = new ServiceDetailsEntity();
			entity.setPassword(passwordEncoder.encode(serviceDetailsDto.getPassword()));
			entity.setRoles(serviceDetailsDto.getRoles());
			entity.setUserName(serviceDetailsDto.getUserName());
			entity.setServiceId(serviceDetailsDto.getServiceId());
			ServiceDetailsEntity savedEntity = authRepository.save(entity);
			//System.out.println(savedEntity);
			if(savedEntity != null) {
				res=new Response(true,null,"Successfully Stored Data",200);
			}else {
				res=new Response(false,null,"Failed To Store Data",400);}	
			}else {
				res=new Response(true,null,"Already Data Present In DB ",200);
			}
			}catch(Exception e) {
				res=new Response(false,null,e.getMessage(),500);	
			}
			return ResponseEntity.status(res.getStatus()).body(res);
		}
	
	
	
	public ResponseEntity<Response> serviceLogin(ServiceDetailsDto serviceDetailsDto) {
		Response response = new Response();
		//System.out.println("Shravan"+serviceDetailsDto);
		try {	
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(serviceDetailsDto.getUserName(),serviceDetailsDto.getPassword()));	
		    String token = jwtUtil.generateJwtToken(serviceDetailsDto.getUserName(),serviceDetailsDto.getRoles(),serviceDetailsDto.getServiceId());	
		    //System.out.println(token);
				response.setMessage("tokenExpireAt "+jwtUtil.formattedTimeByZone);
		        response.setStatus(200);
		        response.setData(token);
		        response.setSuccess(true);		       
		}catch(Exception e) {
			response.setMessage(e.getMessage());
	        response.setStatus(500);
	        response.setSuccess(false);
		}
		//System.out.println(response);
		return ResponseEntity.status(response.getStatus()).body(response);
	}	
	
	
	public Map<String,Set<String>> jwks(){
		Map<String,Set<String>> currentKeys = new LinkedHashMap<>();
		try {		
			if(keysRepository.findByKeyName("JwtKey") != null){
				if(!keysRepository.findByKeyName("JwtKey").getKeys().isEmpty() 
				&& keysRepository.findByKeyName("JwtKey").getKeys() != null){
					currentKeys.put("keys", keysRepository.findByKeyName("JwtKey").getKeys());
					Set<String> ids = new LinkedHashSet<>();
					ids.add("AdminService@8081");
					ids.add("clinicadminservice@8080");
					ids.add("customerservice@8083");
					ids.add("doctorservice@8082");
					ids.add("notificationservice@9092");
					currentKeys.put("sIds", ids);
			}}else{
				currentKeys =null;
			}}catch(Exception e) {
			currentKeys =null;}
	return currentKeys;
	}}

