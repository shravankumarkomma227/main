package com.AdminService.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.AdminService.dto.BranchDTO;
import com.AdminService.entity.Branch;
import com.AdminService.repository.HospitalBranchRepository;
import com.AdminService.util.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HospitalBranchServiceImpl implements HospitalBranchService{

	@Autowired
	public HospitalBranchRepository branchRepository;
	
	@Override
	public Response createBranch(BranchDTO dto) {
	    Response res = new Response();
	    try {
	        if (dto.getClinicId() == null || dto.getClinicId().isBlank()) {
	            res.setMessage("clinicId is required");
	            res.setSuccess(false);
	            res.setStatus(400);
	            return res;
	        }

	        Branch entity = new ObjectMapper().convertValue(dto, Branch.class);
	        entity.setBranchId(null); 

	        String clinicId = dto.getClinicId();
	        List<Branch> siblings = branchRepository.findByClinicId(clinicId);

	    
	        Set<Integer> existingNumbers = siblings.stream()
	            .map(Branch::getBranchId)
	            .filter(Objects::nonNull)
	            .map(id -> {
	                int idx = id.lastIndexOf("-B_");
	                if (idx >= 0) {
	                    try {
	                        return Integer.parseInt(id.substring(idx + 3));
	                    } catch (NumberFormatException ignored) {}
	                }
	                return null;
	            })
	            .filter(Objects::nonNull)
	            .collect(Collectors.toSet());

	        
	        int next = 1;
	        while (existingNumbers.contains(next)) {
	            next++;
	        }

	        String newBranchId = clinicId + "-B_" + next;
	        entity.setBranchId(newBranchId);

	        Branch saved = branchRepository.save(entity);

	        res.setMessage("Branch created successfully");
	        res.setSuccess(true);
	        res.setStatus(201);
	        res.setData(saved);
	        return res;

	    } catch (Exception e) {
	        res.setMessage("Error while creating branch: " + e.getMessage());
	        res.setSuccess(false);
	        res.setStatus(500);
	        return res;
	    }
	}

	@Override
	public Response getAllBranches() {
	    Response response = new Response();
	    try {
	        List<Branch> branches = branchRepository.findAll();
	        response.setMessage("Branches fetched successfully");
	        response.setSuccess(true);
	        response.setStatus(200);
	        response.setData(branches);
	    } catch (Exception e) {
	        response.setMessage("Error fetching branches: " + e.getMessage());
	        response.setSuccess(false);
	        response.setStatus(500);
	    }
	    return response;
	}

	@Override
	public  ResponseEntity<?> getBranchById(String branchId) {
		
		  Response response = new Response();
	        try {
	            Optional<Branch> branch = branchRepository.findByBranchId(branchId);
	            if (branch.isPresent()) {
	                response.setMessage("Branch found");
	                response.setSuccess(true);
	                response.setStatus(200);
	                response.setData(branch.get());
	            } else {
	                response.setMessage("Branch not found");
	                response.setSuccess(false);
	                response.setStatus(404);
	            }
	        } catch (Exception e) {
	            response.setMessage("Error fetching branch: " + e.getMessage());
	            response.setSuccess(false);
	            response.setStatus(500);
	        }
	        return ResponseEntity.status(response.getStatus()).body(response);
	}

	@Override
	public Response updateBranch(String branchId, BranchDTO branchDto) {
	    Response response = new Response();
	    try {
	        Optional<Branch> existingOpt = branchRepository.findByBranchId(branchId);
	        if (existingOpt.isPresent()) {
	            Branch branch = existingOpt.get();


	            branch.setClinicId(branchDto.getClinicId() != null ? branchDto.getClinicId() : branch.getClinicId());
	            branch.setBranchName(branchDto.getBranchName() != null ? branchDto.getBranchName() : branch.getBranchName());
	            branch.setAddress(branchDto.getAddress() != null ? branchDto.getAddress() : branch.getAddress());
	            branch.setCity(branchDto.getCity() != null ? branchDto.getCity() : branch.getCity());
	            branch.setContactNumber(branchDto.getContactNumber() != null ? branchDto.getContactNumber() : branch.getContactNumber());
	            branch.setEmail(branchDto.getEmail() != null ? branchDto.getEmail() : branch.getEmail());
	            branch.setLatitude(branchDto.getLatitude() != null ? branchDto.getLatitude() : branch.getLatitude());
	            branch.setLongitude(branchDto.getLongitude() != null ? branchDto.getLongitude() : branch.getLongitude());
	            branch.setVirtualClinicTour(branchDto.getVirtualClinicTour() != null ? branchDto.getVirtualClinicTour() : branch.getVirtualClinicTour());


	            Branch updatedBranch = branchRepository.save(branch);

	            response.setMessage("Branch updated successfully");
	            response.setSuccess(true);
	            response.setStatus(200);
	            response.setData(updatedBranch);
	        } else {
	            response.setMessage("Branch not found");
	            response.setSuccess(false);
	            response.setStatus(404);
	        }
	    } catch (Exception e) {
	        response.setMessage("Error updating branch: " + e.getMessage());
	        response.setSuccess(false);
	        response.setStatus(500);
	    }
	    return response;
	}



	@Override
	public Response deleteBranch(String branchId) {
		
		 Response response = new Response();
	        try {
	            Optional<Branch> existing = branchRepository.findByBranchId(branchId);
	            if (existing.isPresent()) {
	                branchRepository.deleteByBranchId(branchId);
	                response.setMessage("Branch deleted successfully");
	                response.setSuccess(true);
	                response.setStatus(200);
	            } else {
	                response.setMessage("Branch not found");
	                response.setSuccess(false);
	                response.setStatus(404);
	            }
	        } catch (Exception e) {
	            response.setMessage("Error deleting branch: " + e.getMessage());
	            response.setSuccess(false);
	            response.setStatus(500);
	        }
	        return response;
	}
	
	
	@Override
	public  ResponseEntity<?> getBranchByClinicId(String clinicId) {
		
		  Response response = new Response();
	        try {
	            List<Branch> branch = branchRepository.findByClinicId(clinicId);
	            if (branch != null && !branch.isEmpty() ) {
	                response.setMessage("Branch found");
	                response.setSuccess(true);
	                response.setStatus(200);
	                response.setData(branch);
	            } else {
	                response.setMessage("Branch not found");
	                response.setSuccess(false);
	                response.setStatus(404);
	            }
	        } catch (Exception e) {
	            response.setMessage("Error fetching branch: " + e.getMessage());
	            response.setSuccess(false);
	            response.setStatus(500);
	        }
	        return ResponseEntity.status(response.getStatus()).body(response);
	}

}