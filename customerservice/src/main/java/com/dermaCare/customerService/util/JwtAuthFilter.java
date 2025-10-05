package com.dermaCare.customerService.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
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
		String token = new String();
		String userName = new String();
		List<String> roles = new ArrayList<>();
		//System.out.println(request.getRequestURI());
		if(!request.getRequestURI().startsWith("/api/customerpublicapis")){
			//System.out.println("public");
			if(authHeader == null) {
				Response res = new Response();
				res.setStatus(401);
				res.setSuccess(false);
				res.setMessage("Request Unauthorized");
				response.setContentType("Application/json");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				new ObjectMapper().writeValue(response.getOutputStream(),res);
			    return;}
		if(authHeader != null && authHeader.startsWith("Bearer ")){
		token = authHeader.substring(7);
		if(jwtUtil.isTokenExprired(token)){
			Response res = new Response();
			res.setStatus(401);
			res.setSuccess(false);
			res.setMessage("Request Unauthorized");
			response.setContentType("Application/json");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			new ObjectMapper().writeValue(response.getOutputStream(),res);
		    return;}
		userName = jwtUtil.extractServiceNameFromToken(token);
		roles = jwtUtil.extractRoleFromToken(token);
		//System.out.println(userName);
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
