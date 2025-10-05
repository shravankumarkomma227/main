package com.AdminService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.AdminService.entity.JwtKeysEntity;


public interface KeysRepository extends MongoRepository<JwtKeysEntity, String>{

	JwtKeysEntity findByKeyName(String keyName);
	
}
