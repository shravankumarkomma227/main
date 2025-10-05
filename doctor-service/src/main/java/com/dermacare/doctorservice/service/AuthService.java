package com.dermacare.doctorservice.service;

import org.springframework.http.ResponseEntity;
import com.dermacare.doctorservice.dto.DoctorLoginDTO;
import com.dermacare.doctorservice.dto.Response;


public interface AuthService {
	
public ResponseEntity<Response> doctorLogin(DoctorLoginDTO doctorLoginDTO);

public ResponseEntity<Response> requestForNewJwtTokenByRefreshToken(String refreshToken);
	
}
