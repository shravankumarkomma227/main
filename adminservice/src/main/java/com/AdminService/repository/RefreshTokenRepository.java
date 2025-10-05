package com.AdminService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.AdminService.entity.RefreshJwtToken;


public interface RefreshTokenRepository extends MongoRepository<RefreshJwtToken, String> {
	
	public RefreshJwtToken findByTokenName(String name);

}
