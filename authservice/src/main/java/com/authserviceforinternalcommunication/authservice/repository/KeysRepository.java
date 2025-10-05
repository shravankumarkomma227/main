package com.authserviceforinternalcommunication.authservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.authserviceforinternalcommunication.authservice.entity.JwtKeysEntity;

public interface KeysRepository extends MongoRepository<JwtKeysEntity, String>{

	JwtKeysEntity findByKeyName(String keyName);
	
}
