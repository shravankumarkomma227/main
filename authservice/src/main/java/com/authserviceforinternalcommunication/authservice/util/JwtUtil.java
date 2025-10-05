package com.authserviceforinternalcommunication.authservice.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
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
import com.authserviceforinternalcommunication.authservice.entity.JwtKeysEntity;
import com.authserviceforinternalcommunication.authservice.repository.KeysRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {

	@Autowired
	private KeysRepository keysRepository;
	
	
	public String formattedTimeByZone;		
	private String SECRET = secretKeyGenerator();
	private Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
	private Set<String> keys;
    
	public String generateJwtToken(String serviceName,List<String> roles,String serviceId) {
		    
		ZonedDateTime time = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
		ZonedDateTime plusTime = time.plusHours(2);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
	    this.formattedTimeByZone = plusTime.format(formatter);
		Date specificDateTime = Date.from(plusTime.toInstant());
		
        Map<String ,Object> claims = new LinkedHashMap<>();
        claims.put("name", serviceName);
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
    	   keys = entity.getKeys();
    	   if(!keys.contains(SECRET)) {
    	   keys.add(SECRET);}
    	   entity.setKeys(keys);
    	   keysRepository.save(entity); 
        }else {
        	   JwtKeysEntity entity = keysRepository.findByKeyName("JwtKey");
        	   keys = new LinkedHashSet<>();
        	   keys.add(SECRET);
        	   entity.setKeys(keys);
        keysRepository.save(entity);
        }}         
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(serviceId)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(specificDateTime)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();}
		

		private String secretKeyGenerator(){
			try {
			 KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
		      SecretKey secretKey = keyGen.generateKey();
		      String encoded = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		      return  encoded;
			}catch(NoSuchAlgorithmException e) {
				return e.getMessage();
			}
		}
		
    }
