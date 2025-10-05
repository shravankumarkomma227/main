package com.authserviceforinternalcommunication.authservice.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authserviceforinternalcommunication.authservice.repository.AuthRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private AuthRepository authRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {		
		return authRepository.findByUserName(username).orElseThrow(()->new RuntimeException("Object Not Found With Provided Name"));
	}
	
	
}
