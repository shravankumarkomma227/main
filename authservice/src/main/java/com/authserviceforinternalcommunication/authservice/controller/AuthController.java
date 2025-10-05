package com.authserviceforinternalcommunication.authservice.controller;

import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.authserviceforinternalcommunication.authservice.dto.ServiceDetailsDto;
import com.authserviceforinternalcommunication.authservice.service.AuthService;
import com.authserviceforinternalcommunication.authservice.util.Response;


@RestController
@RequestMapping("/authentication")
public class AuthController {
	
	@Autowired
	private AuthService authService;

	@PostMapping("/serviceRegistration")
	public ResponseEntity<Response> serviceRegistration(@RequestBody ServiceDetailsDto serviceDetailsDto){
		return authService.serviceRegistration(serviceDetailsDto);
	}
		
	
	@PostMapping("/serviceLogin")
	public ResponseEntity<Response> serviceLogin(@RequestBody ServiceDetailsDto serviceDetailsDto){
		return authService.serviceLogin(serviceDetailsDto);
	}
	
	@GetMapping("/jwks")
	public  Map<String,Set<String>> jwks(){
		return authService.jwks();
	}
	
		
}
