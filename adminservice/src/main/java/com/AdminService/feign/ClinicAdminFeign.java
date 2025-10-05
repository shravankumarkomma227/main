package com.AdminService.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.AdminService.dto.DoctorsDTO;
import com.AdminService.dto.SubServicesDto;
import com.AdminService.util.ExtractFeignMessage;
import com.AdminService.util.Response;
import com.AdminService.util.ResponseStructure;

import feign.FeignException;

@FeignClient(name = "clinicadmin")
//@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "clinicAdminServiceFallBack")
public interface ClinicAdminFeign {	
	
	@GetMapping("/clinic-admin/subService/getAllSubServies")
    public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getAllSubServices(@RequestHeader("Authorization") String token);
	
	 // ---------------------- Doctor APIs ----------------------
    @PostMapping("/clinic-admin/addDoctor")
    ResponseEntity<Response> addDoctor(@RequestHeader("Authorization") String token,@RequestBody DoctorsDTO dto);

    @GetMapping("/clinic-admin/doctors")
    ResponseEntity<Response> getAllDoctors(@RequestHeader("Authorization") String token);
		
    @GetMapping("/clinic-admin/doctor/{id}")
    ResponseEntity<Response> getDoctorById(@RequestHeader("Authorization") String token,@PathVariable("id") String id);

    @PutMapping("/clinic-admin/updateDoctor/{doctorId}")
    ResponseEntity<Response> updateDoctorById(@RequestHeader("Authorization") String token,@PathVariable String doctorId,
                                              @Validated @RequestBody DoctorsDTO dto);
		
    @DeleteMapping("/clinic-admin/delete-doctor/{doctorId}")
    ResponseEntity<Response> deleteDoctorById(@RequestHeader("Authorization") String token,@PathVariable String doctorId);
		
    @DeleteMapping("/clinic-admin/delete-doctors-by-clinic/{clinicId}")
    ResponseEntity<Response> deleteDoctorsByClinic(@RequestHeader("Authorization") String token,@PathVariable String clinicId); 

	///FALLBACK METHODS	
	default ResponseEntity<?> clinicAdminServiceFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));
		
	
	}

}