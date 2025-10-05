package com.clinicadmin.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.clinicadmin.entity.CustomConsentForm;

@Repository
public interface CustomConsentFormRepository extends MongoRepository<CustomConsentForm, String> {
	Optional<CustomConsentForm> findByHospitalIdAndConsentFormType(String hospitalId, String consentFormType);

	Optional<CustomConsentForm> findByHospitalIdAndSubServiceid(String hospitalId, String subServiceid);
}
