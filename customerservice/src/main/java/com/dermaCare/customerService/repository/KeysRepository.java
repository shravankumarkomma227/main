package com.dermaCare.customerService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.dermaCare.customerService.entity.JwtKeysEntity;

public interface KeysRepository extends MongoRepository<JwtKeysEntity, String>{

	JwtKeysEntity findByKeyName(String keyName);
	
}
