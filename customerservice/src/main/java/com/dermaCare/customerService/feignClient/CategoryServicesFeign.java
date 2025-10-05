package com.dermaCare.customerService.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dermaCare.customerService.dto.BookingResponse;
import com.dermaCare.customerService.dto.CategoryDto;
import com.dermaCare.customerService.dto.ServicesDto;
import com.dermaCare.customerService.dto.SubServicesDto;
import com.dermaCare.customerService.util.Response;
import com.dermaCare.customerService.util.ResponseStructure;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(value = "category-services")
@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "categoryServiceFallBack")
public interface CategoryServicesFeign {
	
	@GetMapping("/api/v1/subServices/getAllSubServices")
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getAllSubServices(@RequestHeader("Authorization") String token);

	@GetMapping("/api/v1/subServices/getSubService/{hospitalId}/{subServiceId}")
	public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable String hospitalId, @PathVariable String subServiceId);
	
	@GetMapping("/api/v1/services/getServices/{categoryId}")
	public ResponseEntity<ResponseStructure<List<ServicesDto>>> getServiceById(@RequestHeader("Authorization") String token,@PathVariable String categoryId);
	
	@GetMapping("/api/v1/SubServicesInfo/getSubServicesByServiceId/{serviceId}")
	public ResponseEntity<Response> getSubServicesByServiceId(@RequestHeader("Authorization") String token,@PathVariable String serviceId);
	
	@GetMapping("/api/v1/category/getCategories")
    ResponseEntity<ResponseStructure<List<CategoryDto>>> getAllCategory(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/v1/subServices/retrieveSubServicesBySubServiceId/{subServiceId}")
	 public ResponseEntity<ResponseStructure<List<SubServicesDto>>> retrieveSubServicesBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable String subServiceId);
	
	
	//FALLBACK METHODS
	
	default ResponseEntity<?> categoryServiceFallBack(FeignException e){		 
	return ResponseEntity.status(e.status()).body(new ResponseStructure<BookingResponse>(null,e.getMessage(),null,e.status()));}
	
}
