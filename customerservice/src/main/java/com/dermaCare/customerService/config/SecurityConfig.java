package com.dermaCare.customerService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.dermaCare.customerService.util.JWTAthenticationEntryPoint;
import com.dermaCare.customerService.util.JwtAuthFilter;


@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
	
	 @Autowired 
	 private JwtAuthFilter jwtAuthFilter;
	 
	 @Autowired
	 private JWTAthenticationEntryPoint jWTAthenticationEntryPoint;
		
	 
	  @Bean
	    @Order(1)
	    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .securityMatcher("/oauth2/authorization/google","/login/**")  
	            .authorizeHttpRequests(auth -> auth
	                .anyRequest().authenticated())
	            .oauth2Login(n->n.defaultSuccessUrl("/customerPublicApis/oauth2tokens"));
	        return http.build();
	    }    
	    	    	

	    @Bean
	    @Order(2)
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.csrf(csrf -> csrf.disable()).securityMatcher("/customerpublicapis")
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/VerifyUserCredentialsAndGenerateAndSendOtp","/verifyOtp","/resendOtp",
	                "/newAccessTokenForCustomerService","/oauth2tokens")
	                .permitAll()
	                .anyRequest().authenticated()
	            ).exceptionHandling(ex->ex.authenticationEntryPoint(jWTAthenticationEntryPoint))
	            .sessionManagement(session -> session
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	            )
	            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	    }	
	    
	    
//	    @Bean
//		public ClientRegistrationRepository clientRegistrationRepository() {
//			return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
//		}

		
				
}
