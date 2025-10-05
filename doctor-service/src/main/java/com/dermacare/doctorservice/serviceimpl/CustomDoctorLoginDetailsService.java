package com.dermacare.doctorservice.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.dermacare.doctorservice.dermacaredoctorutils.AutoCheckJwtToken;
import com.dermacare.doctorservice.dto.DoctorLoginDTO;
import com.dermacare.doctorservice.dto.Response;
import com.dermacare.doctorservice.feignclient.ClinicAdminServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class CustomDoctorLoginDetailsService implements UserDetailsService {

	@Autowired
	private ClinicAdminServiceClient clinicAdminServiceClient;
	
	@Autowired
	private AuthServiceImpl authServiceImpl;
	
	@Autowired
   	private AutoCheckJwtToken token;
   	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {		
		 Response response = clinicAdminServiceClient.doctorLogin(token.access_token,  username).getBody();			
		 DoctorLoginDTO credentials = new ObjectMapper().convertValue(response.getData(),DoctorLoginDTO.class);
		// System.out.println(credentials);
		 authServiceImpl.hospitalId = credentials.getHospitalId();
		 authServiceImpl.doctorId = credentials.getDoctorId();
		 return credentials;
		}
}