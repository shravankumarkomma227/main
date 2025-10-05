package com.dermacare.bookingService.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dermacare.bookingService.dto.JwkKey;
import com.dermacare.bookingService.dto.JwksResponse;
import com.dermacare.bookingService.dto.Roles;
import com.dermacare.bookingService.feign.KeyCloakFeign;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;


@Component
public class JwtUtil {
	
	
	@Autowired
	private KeyCloakFeign keyCloakFeign;
	
	@Autowired
	private ReqForNewKeycloakKeys reqForNewKeycloakKeys;
	
	
	public String formattedTimeByZone;		
	public JwksResponse jWKSet;
	
    
    public String extractServiceIdFromToken(String token) {
    	String sub = new String();
		try {
			String kId = new String();
			Map<String,Object>  clms = new ObjectMapper().readValue(decodeTokenWithoutKeyForKeyId(token), Map.class);
			kId = new ObjectMapper().convertValue(clms.get("kid"),String.class);
			Map<String, String> claimsMap = new ObjectMapper().readValue(decodeTokenWithoutKey(token), Map.class);
			if(new ObjectMapper().convertValue(claimsMap.get("preferred_username"),String.class).equalsIgnoreCase("service-account-clinicadminservice@8080")){
				if(jWKSet == null){
					jWKSet = keyCloakFeign.getJWKS();}
			for(JwkKey keyclaimas : jWKSet.getKeys()) {
			String keyId = keyclaimas.getKid();
			if(keyId.equalsIgnoreCase(kId)) {			
				String n = keyclaimas.getN();
				String e = keyclaimas.getE();
				PublicKey pkey = getPublicKeyFromNAndE(n,e);
						if(validateJwt(token, pkey)){
							sub = new ObjectMapper().convertValue(claimsMap.get("sub"),String.class);
							break;
					    }else{
						jWKSet = reqForNewKeycloakKeys.keys();
						for(JwkKey jwk : jWKSet.getKeys()) {
						String kyId = jwk.getKid();
						if(kyId.equalsIgnoreCase(kId)) {
							String modulus = jwk.getN();
							String exponent = jwk.getE();
							PublicKey pulickey = getPublicKeyFromNAndE(modulus,exponent);
						if(validateJwt(token, pulickey)){
							sub = new ObjectMapper().convertValue(claimsMap.get("sub"),String.class);
							break;}}}}}}
				    }else{}/// FOR JWT OF APPLICATION
				    }catch(Exception e) {
				    	 System.out.println(e.getMessage());
				    	 sub = null;
						}
		          return sub;
		}
				
					
	public List<String> extractRoleFromToken(String token) {
		List<String> roles = new ArrayList<>();
		try {
			String kId = new String();
			Map<String,Object>  clms = new ObjectMapper().readValue(decodeTokenWithoutKeyForKeyId(token), Map.class);
			kId = new ObjectMapper().convertValue(clms.get("kid"),String.class);
			Map<String, String> claimsMap = new ObjectMapper().readValue(decodeTokenWithoutKey(token), Map.class);
			if(new ObjectMapper().convertValue(claimsMap.get("preferred_username"),String.class).equalsIgnoreCase("service-account-clinicadminservice@8080")){ 
				if(jWKSet == null){
					jWKSet = keyCloakFeign.getJWKS();}
			for(JwkKey keyclaimas : jWKSet.getKeys()) {
			String keyId = keyclaimas.getKid();
			if(keyId.equalsIgnoreCase(kId)) {
				String n = keyclaimas.getN();
				String e = keyclaimas.getE();
				PublicKey pkey = getPublicKeyFromNAndE(n,e);
						if(validateJwt(token, pkey)){
							 Map<String,Roles> dto = new ObjectMapper().convertValue(claimsMap.get("resource_access"),new TypeReference< Map<String,Roles>>(){});
							 System.out.println(dto);
							 Roles clientAccess =  dto.get("account");
							 roles = clientAccess.getRoles();
							  break;
					       }else{
						   jWKSet = reqForNewKeycloakKeys.keys();
							for(JwkKey jwk : jWKSet.getKeys()) {
							String kyId = jwk.getKid();
							if(kyId.equalsIgnoreCase(kId)) {
								String modulus = jwk.getN() ;
								String exponent = jwk.getE();
								PublicKey pulickey = getPublicKeyFromNAndE(modulus,exponent);
						if(validateJwt(token, pulickey)) {
							 Map<String,Roles> dto = new ObjectMapper().convertValue(claimsMap.get("resource_access"),new TypeReference< Map<String,Roles>>(){});
							 //System.out.println(dto);
							 Roles clientAccess =  dto.get("account");
							 roles = clientAccess.getRoles();
							  break;}}}}}}
				    }else{}/// FOR JWT OF APPLICATION
				    }catch(Exception e) { roles = null;}
		              return roles;
      }
		
	
	public String extractServiceNameFromToken(String token) {
		String name = new String();
		try {
			String kId = new String();
			Map<String,Object>  clms = new ObjectMapper().readValue(decodeTokenWithoutKeyForKeyId(token), Map.class);
			kId = new ObjectMapper().convertValue(clms.get("kid"),String.class);
			//System.out.println(keysForClientToken);
			Map<String, String> claimsMap = new ObjectMapper().readValue(decodeTokenWithoutKey(token), Map.class);
			if(new ObjectMapper().convertValue(claimsMap.get("preferred_username"),String.class).equalsIgnoreCase("service-account-clinicadminservice@8080")){
				if(jWKSet == null){
					jWKSet = keyCloakFeign.getJWKS();}
			for(JwkKey keyclaimas : jWKSet.getKeys()) {
			String keyId = keyclaimas.getKid();
			if(keyId.equalsIgnoreCase(kId)) {
				String n = keyclaimas.getN();
				String e = keyclaimas.getE();
				PublicKey pkey = getPublicKeyFromNAndE(n,e);
						if(validateJwt(token, pkey)){
							name = new ObjectMapper().convertValue(claimsMap.get("preferred_username"),String.class);
							break;
					      }else{
						   jWKSet = reqForNewKeycloakKeys.keys();
							for(JwkKey jwk : jWKSet.getKeys()) {
							String kyId = jwk.getKid();
							if(kyId.equalsIgnoreCase(kId)) {								
   							    String modulus = jwk.getN();
								String exponent = jwk.getE();
								PublicKey pulickey = getPublicKeyFromNAndE(modulus,exponent);
						if(validateJwt(token, pulickey)) {
							name = new ObjectMapper().convertValue(claimsMap.get("preferred_username"),String.class);
							break;}}}}}}
				    }else{}/// FOR JWT OF APPLICATION
				    }catch(Exception e) {
		            System.out.println(e.getMessage());
		            name = null;
	                }
		             return name;
             }

	
	public boolean isTokenExprired(String token) {
		boolean exp = false;
		try {
			String kId = new String();
			Map<String,Object>  clms = new ObjectMapper().readValue(decodeTokenWithoutKeyForKeyId(token), Map.class);
			kId = new ObjectMapper().convertValue(clms.get("kid"),String.class);
			//System.out.println(keysForClientToken);
			Map<String, String> claimsMap = new ObjectMapper().readValue(decodeTokenWithoutKey(token), Map.class);
			if(new ObjectMapper().convertValue(claimsMap.get("preferred_username"),String.class).equalsIgnoreCase("service-account-clinicadminservice@8080")){ 
				if(jWKSet == null){
					jWKSet = keyCloakFeign.getJWKS();}
			for(JwkKey keyclaimas : jWKSet.getKeys()) {
			String keyId = keyclaimas.getKid();
			if(keyId.equalsIgnoreCase(kId)) {
				String n = keyclaimas.getN();
				String e = keyclaimas.getE();
				PublicKey pkey = getPublicKeyFromNAndE(n,e);
						if(validateJwt(token, pkey)){
							Long sec =  new ObjectMapper().convertValue(claimsMap.get("exp"),Long.class);
							Long ms = sec * 1000;
							Date d = new Date(ms);
							exp = d.before(new Date());
							break;
					     }else{
						 jWKSet = reqForNewKeycloakKeys.keys();
							for(JwkKey jwk : jWKSet.getKeys()) {
							String kyId = jwk.getKid();
							if(kyId.equalsIgnoreCase(kId)) {
								String modulus = jwk.getN();
								String exponent = jwk.getE();
								PublicKey pulickey = getPublicKeyFromNAndE(modulus,exponent);
						if(validateJwt(token, pulickey)){
							Long sec =  new ObjectMapper().convertValue(claimsMap.get("exp"),Long.class);
							Long ms = sec * 1000;
							Date d = new Date(ms);
							exp = d.before(new Date());
							break;}}}}}}
				    }else{}/// FOR JWT OF APPLICATION				   
				}catch(Exception e) {
			System.out.println(e.getMessage());
			exp = true;
		}
		return exp;
}
	
	
	public boolean validateToken(String token) {
		try {
		if(!isTokenExprired(token)) {
		return true;}
		else {
	    return false;}
        }catch(Exception e) {
	    return false;}}
			
			
		private String decodeTokenWithoutKey(String token) {

	        String[] parts = token.split("\\.");
	        if (parts.length == 3) {
	            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
	         return payloadJson;
	        } else {
	            return null;
	        }
	    }	
		
		
		private String decodeTokenWithoutKeyForKeyId(String token) {

	        String[] parts = token.split("\\.");
	        if (parts.length == 3) {
	            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
	         return payloadJson;
	        } else {
	            return null;
	        }
	    }	
		
		
		 private PublicKey getPublicKeyFromNAndE(String n, String e) throws Exception {
		        // Decode Base64URL (Keycloak uses URL-safe Base64, not standard Base64)
		        byte[] modulusBytes = Base64.getUrlDecoder().decode(n);
		        byte[] exponentBytes = Base64.getUrlDecoder().decode(e);

		        // Convert to BigInteger
		        BigInteger modulus = new BigInteger(1, modulusBytes);
		        BigInteger exponent = new BigInteger(1, exponentBytes);

		        // Create RSA public key spec
		        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);

		        // Generate public key
		        KeyFactory factory = KeyFactory.getInstance("RSA");
		        return factory.generatePublic(spec);
		    }

		 
		 private boolean validateJwt(String jwt, PublicKey publicKey) throws Exception {
		        SignedJWT signedJWT = SignedJWT.parse(jwt);
		        RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
		        return signedJWT.verify(verifier);
		    }
    
   }
	

