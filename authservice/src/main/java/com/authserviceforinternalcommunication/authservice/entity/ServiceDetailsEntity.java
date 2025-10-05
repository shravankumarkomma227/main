package com.authserviceforinternalcommunication.authservice.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ServiceDetailsEntity")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceDetailsEntity implements UserDetails{
	
	@Id
	private String id;
	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	private String serviceId;
	private List<String> roles;
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return roles.stream().map(n -> new SimpleGrantedAuthority(n)).toList();
}


	@Override
	public String getUsername() {
		
		return userName;
	}

}
