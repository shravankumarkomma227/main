package com.AdminService.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.AdminService.entity.Branch;

public interface HospitalBranchRepository extends MongoRepository<Branch, String>  {

	 // Find branches by clinicId (used in createBranch)
    List<Branch> findByClinicId(String clinicId);

    // Find branch by branchId (custom field, not the PK)
    Optional<Branch> findByBranchId(String branchId);

    // Delete branch by branchId
    void deleteByBranchId(String branchId);
    

}


