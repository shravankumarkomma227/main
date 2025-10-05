package com.dermacare.bookingService.util;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization");
		String token;
		String userName;
		System.out.println(authHeader);
		if(authHeader != null && authHeader.startsWith("Bearer ")) {
			System.out.println(authHeader);
		token = authHeader.substring(7);
		System.out.println("token "+token);
		if(jwtUtil.validateToken(token)){
		userName = jwtUtil.extractServiceNameFromToken(token);
		List<String> roles = jwtUtil.extractRoleFromToken(token);
		System.out.println(userName);
		if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null ) {		
			//UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
			List<SimpleGrantedAuthority> rls = roles.stream().map(n->new SimpleGrantedAuthority(n)).toList();
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
			new UsernamePasswordAuthenticationToken(userName,null,rls);
	////its used to add information related to request to authenticated object along with userdetails
			usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		    }}}
		    filterChain.doFilter(request, response);	
}}
