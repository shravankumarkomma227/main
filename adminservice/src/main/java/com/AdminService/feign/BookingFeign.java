package com.AdminService.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.AdminService.dto.BookingResponse;
import com.AdminService.util.ExtractFeignMessage;
import com.AdminService.util.ResponseStructure;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(value = "bookingservice")
@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "bookServiceFallBack")
@CrossOrigin
public interface BookingFeign {
	
	@GetMapping("/api/v1/getAllBookedServices")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getAllBookedService(@RequestHeader("Authorization") String token);
	
	@DeleteMapping("/api/v1/deleteService/{id}")
	public ResponseEntity<ResponseStructure<BookingResponse>> deleteBookedService(@RequestHeader("Authorization") String token,@PathVariable("id") String id);
	
	@GetMapping("/api/v1/getAllBookedServices/{doctorId}")
	public ResponseEntity<ResponseStructure<List<BookingResponse>>> getBookingByDoctorId(@RequestHeader("Authorization") String token,@PathVariable("doctorId") String doctorId);

	
	///FALLBACK METHOD
	
	default ResponseEntity<?> bookServiceFallBack(FeignException ex){		 
		return ResponseEntity.status(ex.status()).body( new ResponseStructure<BookingResponse>(null,ExtractFeignMessage.clearMessage(ex),null,ex.status()));
		}
}
