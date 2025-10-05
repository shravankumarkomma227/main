package com.clinicadmin.sevice.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clinicadmin.dto.ClinicCredentialsDTO;
import com.clinicadmin.dto.Response;
import com.clinicadmin.feignclient.AdminServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class CustomClinicAdminLoginDetailsService implements UserDetailsService {

	@Autowired
	private AdminServiceClient adminServiceClient;
	
	@Autowired
	private AuthServiceImpl authServiceImpl;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 Response response = adminServiceClient.clinicLogin(username);
		 System.out.println(response);
		 ClinicCredentialsDTO credentials = new ObjectMapper().convertValue(response.getData(),ClinicCredentialsDTO.class);
		 System.out.println(credentials);
		 authServiceImpl.hospitalId = credentials.getHospitalId();
		 authServiceImpl.hospitalName = credentials.getHospitalName();
		 authServiceImpl.branchId = credentials.getBranchId();
		 return credentials;}
	}
