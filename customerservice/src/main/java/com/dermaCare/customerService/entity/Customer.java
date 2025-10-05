package com.dermaCare.customerService.entity;


import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document(collection = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements UserDetails {
	@Id
    private String id; // MongoDB ObjectId 
	private String customerId;
	private String deviceId;
    private String mobileNumber;
    private String fullName; // required 
    private String fcm;
    private String gender; // required
    @Indexed(unique = true)
    private String emailId;
    private String dateOfBirth;
    private String referCode;
    private boolean registrationCompleted;
    private List<String> roles;
    
    @Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(roles != null) {
	return roles.stream().map(n->new SimpleGrantedAuthority(n)).toList();
		}else{
			return null;
		}
}
	
	@Override
	public String getUsername() {
		
		return mobileNumber ;
	}
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
}