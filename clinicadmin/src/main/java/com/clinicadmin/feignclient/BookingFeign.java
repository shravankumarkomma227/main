package com.clinicadmin.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.clinicadmin.dto.BookingResponse;
import com.clinicadmin.dto.Response;
import com.clinicadmin.dto.ResponseStructure;

@FeignClient(value = "bookingservice")
public interface BookingFeign {

	@GetMapping("/api/v1/getBookedServiceById/{id}")
	public ResponseEntity<ResponseStructure<BookingResponse>> getBookedService(@RequestHeader("Authorization") String token,@PathVariable String id);
	
	@PutMapping("/api/v1/updateAppointment")
	public ResponseEntity<?> updateAppointment(@RequestHeader("Authorization") String token,@RequestBody BookingResponse bookingResponse );
	
	//---------------------------to get patientdetails by bookingId,pateintId,mobileNumber---------------------------
	@GetMapping("/api/v1/getPatientDetailsForConsetForm/{bookingId}/{patientId}/{mobileNumber}")
	public ResponseEntity<Response> getPatientDetailsForConsentForm(@RequestHeader("Authorization") String token,@PathVariable String bookingId,@PathVariable String patientId,@PathVariable String mobileNumber);
	

}