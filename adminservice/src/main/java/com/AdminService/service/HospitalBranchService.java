package com.AdminService.service;

import org.springframework.http.ResponseEntity;

import com.AdminService.dto.BranchDTO;
import com.AdminService.util.Response;

public interface HospitalBranchService {
	public Response createBranch(BranchDTO branch);
	 ResponseEntity<?> getBranchById(String branchId);
	Response updateBranch(String branchId, BranchDTO branch);
	Response deleteBranch(String branchId);
	Response getAllBranches();
	public  ResponseEntity<?> getBranchByClinicId(String clinicId);
}