package com.authserviceforinternalcommunication.authservice.service;

import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import com.authserviceforinternalcommunication.authservice.dto.ServiceDetailsDto;
import com.authserviceforinternalcommunication.authservice.util.Response;

public interface AuthService {
	
	public ResponseEntity<Response> serviceRegistration(ServiceDetailsDto serviceDetailsDto);
	
	public ResponseEntity<Response> serviceLogin(ServiceDetailsDto serviceDetailsDto);

	public Map<String,Set<String>> jwks();
}
