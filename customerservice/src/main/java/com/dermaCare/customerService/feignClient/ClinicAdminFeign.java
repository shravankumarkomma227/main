package com.dermaCare.customerService.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dermaCare.customerService.dto.DoctorsDTO;
import com.dermaCare.customerService.util.Response;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;



@FeignClient(value = "clinicadmin")
@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "clinicAdminServiceFallBack")
public interface ClinicAdminFeign {

	@GetMapping("/clinic-admin/getDoctorByServiceId/{hospitalId}/{service}")
	public ResponseEntity<Response> getDoctorByService(@RequestHeader("Authorization") String token,@PathVariable String hospitalId, @PathVariable String service);
	
	@GetMapping("/clinic-admin/doctors/hospital/{hospitalId}/subServiceId/{subServiceId}")
	public ResponseEntity<Response> getDoctorsBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable String hospitalId, @PathVariable String subServiceId);
	
	@GetMapping("/clinic-admin/getDoctorslots/{hospitalId}/{doctorId}")
	public ResponseEntity<Response> getDoctorSlot(@RequestHeader("Authorization") String token,@PathVariable String hospitalId,@PathVariable String doctorId);
	
	@GetMapping("/clinic-admin/doctor/{id}")
	public ResponseEntity<Response> getDoctorById(@RequestHeader("Authorization") String token,@PathVariable String id);
	
	@GetMapping("/clinic-admin/getHospitalAndDoctorUsingSubServiceId/{subServiceId}")
	public ResponseEntity<Response> getHospitalAndDoctorUsingSubServiceId(@RequestHeader("Authorization") String token,@PathVariable String subServiceId);
	
	@GetMapping("/clinic-admin/getAllDoctorsBySubServiceId/{subServiceId}")
	public ResponseEntity<Response> getAllDoctorsBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable String subServiceId);
	
	@GetMapping("/clinic-admin/averageRatings/{hospitalId}/{doctorId}")
	public ResponseEntity<Response> getAverageRatings(@RequestHeader("Authorization") String token,@PathVariable String hospitalId, @PathVariable String doctorId);
	
	@PutMapping("/clinic-admin/updateDoctorSlotWhileBooking/{doctorId}/{date}/{time}")
	public Boolean updateDoctorSlotWhileBooking(@RequestHeader("Authorization") String token,@PathVariable String doctorId, @PathVariable String date,
			@PathVariable String time);
	
	@PutMapping("/clinic-admin/updateDoctor/{doctorId}")
	public ResponseEntity<Response> updateDoctorById(@RequestHeader("Authorization") String token,@PathVariable String doctorId,
			@RequestBody DoctorsDTO dto);
	
	//FALLBACK METHODS
	
		default ResponseEntity<?> clinicAdminServiceFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(e.getMessage(),e.status(),false,null));}
}
