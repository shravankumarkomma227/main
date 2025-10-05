package com.AdminService.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.AdminService.dto.JwkKey;
import com.AdminService.dto.JwksResponse;
import com.AdminService.dto.Roles;
import com.AdminService.entity.JwtKeysEntity;
import com.AdminService.entity.RefreshJwtToken;
import com.AdminService.feign.KeyCloakFeign;
import com.AdminService.repository.KeysRepository;
import com.AdminService.repository.RefreshTokenRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;


@Component
public class JwtUtil {
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private KeysRepository keysRepository;
	
	@Autowired
	private KeyCloakFeign keyCloakFeign;
	
	@Autowired
	private ReqForNewKeycloakKeys reqForNewKeycloakKeys;
	
	public String formattedTimeByZone;		
	public String SECRET;
	public Set<String> newKeys;
	public Set<String> keysForClientToken;
	public JwksResponse jWKSet;
    public String nameInJwks;
	
	public String generateJwtToken(String serviceName,List<String> roles) {
		
	    Key key = Keys.hmacShaKeyFor(SECRET.getBytes()); 
	   // System.out.println(key);
		ZonedDateTime time = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
		ZonedDateTime plusTime = time.plusHours(2);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
	    this.formattedTimeByZone = plusTime.format(formatter);
		Date specificDateTime = Date.from(plusTime.toInstant());
		
        Map<String ,Object> claims = new LinkedHashMap<>();
        claims.put("preferred_username", serviceName);
        claims.put("roles", roles);
        claims.put("keyId",SECRET.substring(SECRET.length() - 5));
        
        if(keysRepository.findByKeyName("JwtKey") == null){
        JwtKeysEntity k = new JwtKeysEntity();
        k.setKeyName("JwtKey");
        Set<String> keys = new LinkedHashSet<>();
        keys.add(SECRET);
        k.setKeys(keys);
        keysRepository.save(k);
        }else{
       if(keysRepository.findByKeyName("JwtKey").getKeys() != null && !keysRepository.findByKeyName("JwtKey").getKeys().isEmpty()){
    	   JwtKeysEntity entity = keysRepository.findByKeyName("JwtKey");
    	   newKeys = entity.getKeys();
    	   System.out.println(SECRET);
    	   if(!newKeys.contains(SECRET)) {
    		  System.out.println(SECRET);
    		   newKeys.add(SECRET);
    	   entity.setKeys(newKeys);
    	   keysRepository.save(entity);} 
               }else{
        	   JwtKeysEntity entity = keysRepository.findByKeyName("JwtKey");
        	   newKeys =new LinkedHashSet<>();
        	   newKeys.add(SECRET);
        	   //System.out.println(SECRET);
        	   entity.setKeys(newKeys);
        keysRepository.save(entity);}}
           
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(specificDateTime)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();}
	
	
	
    public Response validateRefreshToken(String RefToken) {
    	Response res = new Response();
    	try {
    		if(!isTokenExprired(RefToken) && verifyRefreshTokenDetails(RefToken)){
    			res.setData(Collections.singletonMap("jwtToken", generateJwtToken(extractServiceNameFromToken(RefToken),extractRoleFromToken(RefToken))));
    			res.setStatus(200);
    			res.setSuccess(true);
    			res.setMessage("new jwt token");
    		}else {
    			res.setMessage("RefreshToken Has Expired Please Login To Get New RefreshToken");
    			res.setStatus(401);
    			res.setSuccess(false);
    		}}catch(Exception e) {
    			res.setStatus(500);
    			res.setMessage(e.getMessage());
    			res.setSuccess(false);
    	}
    	return res;
    }
    	
    
    private boolean verifyRefreshTokenDetails(String RefToken) {
    	try {
    		RefreshJwtToken refreshJwtToken	 = refreshTokenRepository.findByTokenName("JwtRefreshToken");
    		if(refreshJwtToken == null) {
    			return false;
    		}else {
    			String rName = extractServiceNameFromToken(RefToken);
    			return rName.equalsIgnoreCase(extractServiceNameFromToken(refreshJwtToken.getJwtToken()));
    		}
    	}catch(Exception e) {
    		return false;
    	}
    }
	
    
    public String extractServiceIdFromToken(String token) {
    	String sub = new String();
		try {
			String kId = new String();
			Map<String,Object>  clms = new ObjectMapper().readValue(decodeTokenWithoutKeyForKeyId(token), Map.class);
			kId = new ObjectMapper().convertValue(clms.get("kid"),String.class);
			String currentSecretKey = new String();
			if(keysForClientToken == null){
			if(keysRepository.findByKeyName("JwtKey") != null){
				if(!keysRepository.findByKeyName("JwtKey").getKeys().isEmpty() 
				&& keysRepository.findByKeyName("JwtKey").getKeys() != null){
					keysForClientToken = keysRepository.findByKeyName("JwtKey").getKeys();}}}
			System.out.println(keysForClientToken);
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
				    }else{/// FOR JWT OF APPLICATION
				    String keyId = claimsMap.get("keyId");///KEY ID OF APPLICATION TOKEN	
				    if(keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().isPresent()){
				    	currentSecretKey = keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().get();
				    }else{
				    	keysForClientToken = keysRepository.findByKeyName("JwtKey").getKeys();
					    currentSecretKey = keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().get();
				    }
				Key currentSecretKeyBytes = Keys.hmacShaKeyFor(currentSecretKey.getBytes());				
				Claims claims = Jwts.parserBuilder()
						.setSigningKey(currentSecretKeyBytes)
						.build()
						.parseClaimsJws(token)
						.getBody();	
					   // System.out.println(claims);
				    sub = claims.getSubject();}
				    return sub;
				     }catch(Exception e) {
				    	 System.out.println(e.getMessage());
							return null;
						}}
				
					
	public List<String> extractRoleFromToken(String token) {
		List<String> roles = new ArrayList<>();
		try {
			String kId = new String();
			Map<String,Object>  clms = new ObjectMapper().readValue(decodeTokenWithoutKeyForKeyId(token), Map.class);
			kId = new ObjectMapper().convertValue(clms.get("kid"),String.class);
			String currentSecretKey = new String();
			if(keysForClientToken == null){
			if(keysRepository.findByKeyName("JwtKey") != null){
				if(!keysRepository.findByKeyName("JwtKey").getKeys().isEmpty() 
				&& keysRepository.findByKeyName("JwtKey").getKeys() != null){
					keysForClientToken = keysRepository.findByKeyName("JwtKey").getKeys();}}}
			Map<String, String> claimsMap = new ObjectMapper().readValue(decodeTokenWithoutKey(token), Map.class);
			//System.out.println(claimsMap);
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
				    }else{/// FOR JWT OF APPLICATION
				    String keyId = claimsMap.get("keyId");	
				    if(keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().isPresent()){
				    	currentSecretKey = keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().get();
				    }else{
				    	keysForClientToken = keysRepository.findByKeyName("JwtKey").getKeys();
					    currentSecretKey = keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().get();
				    }
			Key currentSecretKeyBytes = Keys.hmacShaKeyFor(currentSecretKey.getBytes());				
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(currentSecretKeyBytes)
					.build()
					.parseClaimsJws(token)
					.getBody();	
	Object obj = claims.get("roles",ArrayList.class);
	roles = new ObjectMapper().convertValue(obj, new TypeReference<ArrayList<String>>(){});
	}}catch(Exception e) { roles = null;}
		return roles;
}
		
	
	public String extractServiceNameFromToken(String token) {
		String name = new String();
		try {
			String kId = new String();
			Map<String,Object>  clms = new ObjectMapper().readValue(decodeTokenWithoutKeyForKeyId(token), Map.class);
			kId = new ObjectMapper().convertValue(clms.get("kid"),String.class);
			String currentSecretKey = new String();
			if(keysForClientToken == null){
			if(keysRepository.findByKeyName("JwtKey") != null){
				if(!keysRepository.findByKeyName("JwtKey").getKeys().isEmpty() 
				&& keysRepository.findByKeyName("JwtKey").getKeys() != null){
					keysForClientToken = keysRepository.findByKeyName("JwtKey").getKeys();}}}
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
				    }else{/// FOR JWT OF APPLICATION
				    String keyId = claimsMap.get("keyId");	
				    if(keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().isPresent()){
				    	currentSecretKey = keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().get();
				    }else{
				    	keysForClientToken = keysRepository.findByKeyName("JwtKey").getKeys();
					    currentSecretKey = keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().get();
				    }
				Key currentSecretKeyBytes = Keys.hmacShaKeyFor(currentSecretKey.getBytes());				
				Claims claims = Jwts.parserBuilder()
						.setSigningKey(currentSecretKeyBytes)
						.build()
						.parseClaimsJws(token)
						.getBody();	
				name = claims.get("preferred_username", String.class);
		}}catch(Exception e) {
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
			//System.out.println(kId);
			String currentSecretKey = new String();
			if(keysForClientToken == null){
			if(keysRepository.findByKeyName("JwtKey") != null){
				if(!keysRepository.findByKeyName("JwtKey").getKeys().isEmpty() 
				&& keysRepository.findByKeyName("JwtKey").getKeys() != null){
					keysForClientToken = keysRepository.findByKeyName("JwtKey").getKeys();}}}
			//System.out.println(keysForClientToken);
			Map<String, String> claimsMap = new ObjectMapper().readValue(decodeTokenWithoutKey(token), Map.class);
			if(new ObjectMapper().convertValue(claimsMap.get("preferred_username"),String.class).equalsIgnoreCase("service-account-clinicadminservice@8080")){ 
				if(jWKSet == null){
				jWKSet = keyCloakFeign.getJWKS();}
			for(JwkKey keyclaimas : jWKSet.getKeys()) {
			String keyId = keyclaimas.getKid();
			//System.out.println(keyId);
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
				    }else{/// FOR JWT OF APPLICATION
				    String keyId = claimsMap.get("keyId");
				   // System.out.println(keyId);
				    if(keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().isPresent()){
				    	currentSecretKey = keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().get();
				    	//System.out.println(currentSecretKey);
				    }else{
				    	keysForClientToken = keysRepository.findByKeyName("JwtKey").getKeys();
					    currentSecretKey = keysForClientToken.stream().filter(s->s.endsWith(keyId)).findFirst().get();
					   // System.out.println(currentSecretKey);
				    }			    
				Key currentSecretKeyBytes = Keys.hmacShaKeyFor(currentSecretKey.getBytes());				
				Claims claims = Jwts.parserBuilder()
						.setSigningKey(currentSecretKeyBytes)
						.build()
						.parseClaimsJws(token)
						.getBody();	
				exp = claims.getExpiration().before(new Date());
				//System.out.println(exp);
				}}catch(Exception e) {
			//System.out.println(e.getMessage());
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
	

