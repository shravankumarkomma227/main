package com.dermacare.doctorservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.dermacare.doctorservice.model.RefreshJwtToken;


public interface RefreshTokenRepository extends MongoRepository<RefreshJwtToken, String> {
	
	public RefreshJwtToken findByTokenName(String name);

}
