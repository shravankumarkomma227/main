package com.AdminService.feign;

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

import com.AdminService.dto.BookingResponse;
import com.AdminService.dto.CustomerDTO;
import com.AdminService.util.ExtractFeignMessage;
import com.AdminService.util.Response;
import com.AdminService.util.ResponseStructure;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "customerservice")
@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "customerServiceFallBack")
public interface CustomerFeign {
	
	@PostMapping("/api/customer/saveBasicDetails")
	public ResponseEntity<Response> saveCustomerBasicDetails(@RequestHeader("Authorization") String token,@RequestBody CustomerDTO customerDTO );
	
	@GetMapping("/api/customer/getCustomerByInput/{input}")
  	public ResponseEntity<?> getCustomerByUsernameMobileEmail(@RequestHeader("Authorization") String token,@PathVariable("input") String input);
	
	@GetMapping("/api/customer/getBasicDetails/{mobileNumber}")
	public ResponseEntity<Response> getCustomerBasicDetails(@RequestHeader("Authorization") String token,@PathVariable("mobileNumber") String mobileNumber );
	
	@GetMapping("/api/customer/getAllCustomers")
	public ResponseEntity<Response> getAllCustomers(@RequestHeader("Authorization") String token);
	
	@PutMapping("/api/customer/updateCustomerBasicDetails/{mobileNumber}")
	public ResponseEntity<Response> updateCustomerBasicDetails(@RequestHeader("Authorization") String token,@RequestBody CustomerDTO customerDTO,
			@PathVariable("mobileNumber") String mobileNumber );
	
	@DeleteMapping("/api/customer/deleteCustomerBasicDetails/{mobileNumber}")
	public ResponseEntity<Response> deleteCustomerBasicDetails(@RequestHeader("Authorization") String token,@PathVariable("mobileNumber") String mobileNumber );
	
	
	//FALLBACK METHOD
	
	default ResponseEntity<?> customerServiceFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));}


}
