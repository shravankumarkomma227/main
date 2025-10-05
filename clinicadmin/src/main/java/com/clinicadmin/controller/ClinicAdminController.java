package com.clinicadmin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicadmin.dto.ClinicDTO;
import com.clinicadmin.dto.ClinicLoginRequestDTO;
import com.clinicadmin.dto.Response;
import com.clinicadmin.dto.UpdateClinicLoginCredentialsDTO;
import com.clinicadmin.feignclient.AdminServiceClient;
import com.clinicadmin.repository.ClinicAdminWebFcmTokenRepository;
import com.clinicadmin.service.ClinicAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clinic-admin")
//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ClinicAdminController {

	@Autowired
	ClinicAdminService clinicAdminService;

	@Autowired
	AdminServiceClient adminServiceClient;
	

//------------------------------Update Clinic --------------------------------------------------------------------
	@PutMapping("/updatePassword/{userName}")
	public ResponseEntity<Response> updateClinicPassword(
			@RequestBody UpdateClinicLoginCredentialsDTO updateClinicLoginCredentialsDTO,
			@PathVariable String userName) {
		Response response = clinicAdminService.updateClinicCredentials(updateClinicLoginCredentialsDTO, userName);
		return ResponseEntity.status(response.getStatus()).body(response);

	}
//------------------------------Get Clinic By ID---------------------------------------------------------------------

	@GetMapping("/getClinic/{hospitalId}")
	public ResponseEntity<Response> getClincById(@PathVariable String hospitalId) {
		Response response = clinicAdminService.getClinicById(hospitalId);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

// ----------------------Update Clinic By Hospital id----------------------------------------------------------------
	@PutMapping("/updateClinic/{hospitalId}")
	public ResponseEntity<Response> updateClinic(@PathVariable String hospitalId, @Valid @RequestBody ClinicDTO dto) {
		Response response = clinicAdminService.updateClinic(hospitalId, dto);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

// ----------------------Delele Clinic By Hospital id----------------------------------------------------------------
//	@DeleteMapping("/deleteClinic/{hospitalId}")
//	public ResponseEntity<Response> deleteClinic(@PathVariable String hospitalId) {
//		Response response = clinicAdminService.deleteClinic(hospitalId);
//		return ResponseEntity.status(response.getStatus()).body(response);
//	}
	
	
	@GetMapping("/clincDeviceId/{clinicId}/{branchId}")
	public ResponseEntity<Response> getClincFcmToken(@PathVariable String clinicId,@PathVariable String branchId){
		return clinicAdminService.getClincFcmToken(clinicId, branchId);
	}
	

}
