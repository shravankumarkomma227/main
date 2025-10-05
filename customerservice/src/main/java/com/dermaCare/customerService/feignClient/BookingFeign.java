package com.dermaCare.customerService.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dermaCare.customerService.dto.BookingRequset;
import com.dermaCare.customerService.dto.BookingResponse;
import com.dermaCare.customerService.util.ExtractFeignMessage;
import com.dermaCare.customerService.util.ResponseStructure;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;



@FeignClient(value = "bookingservice")
@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "bookingServiceFallBack")
public interface BookingFeign {

	@GetMapping("/api/v1/getBookedServiceById/{id}")
	public ResponseEntity<ResponseStructure<BookingResponse>> getBookedService(@RequestHeader("Authorization") String token,@PathVariable String id);
	
	@PutMapping("/api/v1/updateAppointment")
	public ResponseEntity<?> updateAppointment(@RequestHeader("Authorization") String token,@RequestBody BookingResponse bookingResponse );
	
	@PostMapping("/api/v1/bookService")
	public ResponseEntity<ResponseStructure<BookingResponse>> bookService(@RequestHeader("Authorization") String token,@RequestBody BookingRequset req);
	
	@DeleteMapping("/api/v1/deleteService/{id}")
	//@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "deleteBookedServiceFallBack")
	public ResponseEntity<ResponseStructure<BookingResponse>> deleteBookedService(@RequestHeader("Authorization") String token,@PathVariable String id);
	
	@GetMapping("/api/v1/getBookedServicesByMobileNumber/{mobileNumber}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getCustomerBookedServices(
			@RequestHeader("Authorization") String token,@PathVariable String mobileNumber);
	
	@GetMapping("/api/v1/getAllBookedServices")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getAllBookedService(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/v1/getAllBookedServices/{doctorId}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getBookingByDoctorId(@RequestHeader("Authorization") String token,@PathVariable String doctorId);

	@GetMapping("/api/v1/getBookedServicesByServiceId/{serviceId}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getBookingByServiceId(@RequestHeader("Authorization") String token,@PathVariable String serviceId);
	
	@GetMapping("/api/v1/getBookedServicesByClinicId/{clinicId}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getBookingByClinicId(@RequestHeader("Authorization") String token,@PathVariable String clinicId);

	@GetMapping("/api/v1/getInProgressAppointments/{mobilenumber}/{patientId}")
	public ResponseEntity<?> inProgressAppointments(@PathVariable String mobilenumber,@PathVariable String patientId);
	
	
	//FALLBACK METHODS
	
		default ResponseEntity<?> bookingServiceFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<BookingResponse>(null,e.getMessage(),null,e.status()));}
		
//		
//		default ResponseEntity<?> deleteBookedServiceFallBack(FeignException e){		 
//			return ResponseEntity.status(e.status()).body(new ResponseStructure<BookingResponse>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));}
//			
	
	
}