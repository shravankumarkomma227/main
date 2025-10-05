package com.dermacare.doctorservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dermacare.doctorservice.dto.ChangeDoctorPasswordDTO;
import com.dermacare.doctorservice.dto.ClinicInfoDTO;
import com.dermacare.doctorservice.dto.DoctorAvailabilityStatusDTO;
import com.dermacare.doctorservice.dto.DoctorLoginDTO;
import com.dermacare.doctorservice.dto.Response;
import com.dermacare.doctorservice.dto.TreatmentDTO;
import com.dermacare.doctorservice.dto.VitalsDTO;

@FeignClient(name = "clinicadmin")
public interface ClinicAdminServiceClient {
	
//--------------------------------------------	Doctor login feign client from clinic  --------------------------------
	
	@PostMapping("/clinic-admin/doctorLogin/{userName}")
	public ResponseEntity<Response> doctorLogin(@RequestHeader("Authorization") String token,@PathVariable String userName); 


	 @PutMapping("/clinic-admin/update-password/{username}")
	    Response changePassword(@RequestHeader("Authorization") String token,@PathVariable("username") String username, @RequestBody ChangeDoctorPasswordDTO updateDTO);
	 
	 @PostMapping("/clinic-admin/doctorId/{doctorId}/availability")
	 Response updateDoctorAvailability(@RequestHeader("Authorization") String token,@PathVariable("doctorId") String doctorId,
	                                   @RequestBody DoctorAvailabilityStatusDTO availabilityDTO);
	 
	 
//	--------------------------------- TreatmentFeignClient from clinic admin  -------------------------------------
	 @PostMapping("/clinic-admin/treatment/addTreatment")
	    public ResponseEntity<Response> addTreatment(@RequestHeader("Authorization") String token,@RequestBody TreatmentDTO dto);
	 
	  @GetMapping("/clinic-admin/treatment/getAllTreatments")
	    public ResponseEntity<Response> getAllTreatments(@RequestHeader("Authorization") String token);
	  
	  @GetMapping("/clinic-admin/treatment/getTreatmentById/{id}/{hospitalId}")
	    public ResponseEntity<Response> getTreatmentById(@RequestHeader("Authorization") String token,@PathVariable String id , @PathVariable String hospitalId);
	  
	  @DeleteMapping("/clinic-admin/treatment/deleteTreatmentById/{id}/{hospitalId}")
	    public ResponseEntity<Response> deleteTreatmentById(@RequestHeader("Authorization") String token,@PathVariable String id, @PathVariable String hospitalId);
	  
	  @PutMapping("/clinic-admin/treatment/updateTreatmentById/{id}/{hospitalId}")
	    public ResponseEntity<Response> updateTreatmentById(@RequestHeader("Authorization") String token,@PathVariable String id, @PathVariable String hospitalId, @RequestBody TreatmentDTO dto);
	  

	  
	  @GetMapping("/clinic-admin/doctors")
		 public ResponseEntity<Response> getAllDoctors(@RequestHeader("Authorization") String token);
		 
		 @GetMapping("/clinic-admin/doctor/{id}")
		 public ResponseEntity<Response> getDoctorById(@RequestHeader("Authorization") String token,@PathVariable String id);
		 
		 @GetMapping("/clinic-admin/clinic/{clinicId}/doctor/{doctorId}")
			public ResponseEntity<Response> getDoctorByClinicAndDoctorId(@RequestHeader("Authorization") String token,@PathVariable String clinicId,
					@PathVariable String doctorId);
		 
		 @GetMapping("/clinic-admin/doctors/hospitalById/{hospitalId}")
			public ResponseEntity<Response> getDoctorsByHospitalById(@RequestHeader("Authorization") String token,@PathVariable String hospitalId);
		 
		 @GetMapping("/clinic-admin/doctors/hospital/{hospitalId}/subServiceId/{subServiceId}")
			public ResponseEntity<Response> getDoctorsBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable String hospitalId,
					@PathVariable String subServiceId);
		 
		 
		 @GetMapping("/clinic-admin/getAllDoctorsBySubServiceId/{subServiceId}")
			public ResponseEntity<Response> getAllDoctorsBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable String subServiceId);
		 
		 @GetMapping("/clinic-admin/clinic/{clinicId}")
		 ResponseEntity<Response> getClinicById(@RequestHeader("Authorization") String token,@PathVariable String clinicId);

		 @GetMapping("/clinics/doctor/{doctorId}")
		    ClinicInfoDTO getClinicInfoByDoctorId(@RequestHeader("Authorization") String token,@PathVariable String doctorId);
		
		// ------------------------------ Vitals ------------------------------
		 @PostMapping("/clinic-admin/{patientId}/addingVitals")
		 ResponseEntity<Response> addVitals(@RequestHeader("Authorization") String token,@PathVariable String patientId, @RequestBody VitalsDTO dto);

		 @GetMapping("/clinic-admin/getVitals/{patientId}")
		 ResponseEntity<Response> getVitals(@RequestHeader("Authorization") String token,@PathVariable String patientId);

		 @DeleteMapping("/clinic-admin/deleteVitals/{patientId}")
		 ResponseEntity<Response> delVitals(@RequestHeader("Authorization") String token,@PathVariable String patientId);

		 @PutMapping("/clinic-admin/updateVitals/{patientId}")
		 ResponseEntity<Response> updateVitals(@RequestHeader("Authorization") String token,@PathVariable String patientId, @RequestBody VitalsDTO dto);

}
