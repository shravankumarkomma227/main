package com.dermacare.doctorservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.dermacare.doctorservice.model.DoctorDeviceIdsEntity;

public interface DoctorDeviceIdsRepository extends MongoRepository<DoctorDeviceIdsEntity, String>{

	DoctorDeviceIdsEntity findByDoctorId(String doctorId);
}


