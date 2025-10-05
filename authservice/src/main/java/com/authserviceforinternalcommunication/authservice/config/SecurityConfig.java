package com.authserviceforinternalcommunication.authservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.authserviceforinternalcommunication.authservice.serviceImpl.CustomUserDetailsService;
import com.authserviceforinternalcommunication.authservice.util.JWTAthenticationEntryPoint;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JWTAthenticationEntryPoint jWTAthenticationEntryPoint;
		
	 @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/authentication/**").permitAll()
	                .anyRequest().authenticated()
	            ).exceptionHandling(ex->ex.authenticationEntryPoint(jWTAthenticationEntryPoint))
	            .sessionManagement(session -> session
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	            );

	        return http.build();
	    }
	    
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    
	    @Bean
	    public UserDetailsService userDetailsService() {
	    	return new CustomUserDetailsService();
	    }
	    
	    
	    @Bean	    
	    public AuthenticationManager customAuthenticationManager(UserDetailsService userDetailsService,
	    		PasswordEncoder passwordEncoder) {
	    	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    	authProvider.setUserDetailsService(userDetailsService);
	    	authProvider.setPasswordEncoder(passwordEncoder);
	    	return new ProviderManager(authProvider);
	    }


}
