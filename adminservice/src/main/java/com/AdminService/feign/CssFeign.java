package com.AdminService.feign;

import java.util.List;

import org.bson.types.ObjectId;
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

import com.AdminService.dto.CategoryDto;
import com.AdminService.dto.ServicesDto;
import com.AdminService.dto.SubServicesDto;
import com.AdminService.dto.SubServicesInfoDto;
import com.AdminService.util.ExtractFeignMessage;
import com.AdminService.util.Response;
import com.AdminService.util.ResponseStructure;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@FeignClient(name = "category-services")
public interface CssFeign {
		
	@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "addNewCategoryFallBack")
    @PostMapping("/api/v1/category/addCategory")
    ResponseEntity<ResponseStructure<CategoryDto>> addNewCategory(@RequestHeader("Authorization") String token,@RequestBody CategoryDto dto);
	
    @GetMapping("/api/v1/category/getCategories")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getAllCategoryFallBack")
    ResponseEntity<ResponseStructure<List<CategoryDto>>> getAllCategory(@RequestHeader("Authorization") String token);
	
    @GetMapping("/api/v1/category/getCategory/{categoryId}")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getCategoryByIdFallBack")
	public ResponseEntity<ResponseStructure<CategoryDto>> 
    getCategoryById(@RequestHeader("Authorization") String token,@PathVariable("categoryId") String categoryId) ;
	
    @DeleteMapping("/api/v1/category/deleteCategory/{categoryId}")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "deleteCategoryFallBack")
    public ResponseEntity<ResponseStructure<String>> deleteCategory(@RequestHeader("Authorization") String token,@PathVariable("categoryId") ObjectId categoryId);  // Use string for compatibility
	
    @PutMapping("/api/v1/category/updateCategory/{categoryId}")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "updateCategoryFallBack")
    ResponseEntity<ResponseStructure<CategoryDto>> updateCategory(
    		@RequestHeader("Authorization") String token, @PathVariable("categoryId") ObjectId categoryId,
            @RequestBody CategoryDto updatedCategory);
    
    
    //SERVICES
    
    @PostMapping("/api/v1/services/addService")
    @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "addServiceFallBack")
	public ResponseEntity<ResponseStructure<ServicesDto>> addService(@RequestHeader("Authorization") String token,@RequestBody ServicesDto dto);
	
	@GetMapping("/api/v1/services/getServices/{categoryId}")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getServiceByIdFallBack")
	public ResponseEntity<ResponseStructure<List<ServicesDto>>> getServiceById(@RequestHeader("Authorization") String token,@PathVariable("categoryId") String categoryId);

	@GetMapping("/api/v1/services/getService/{serviceId}")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getServiceByServiceIdFallBack")
	public ResponseEntity<ResponseStructure<ServicesDto>> getServiceByServiceId(@RequestHeader("Authorization") String token,@PathVariable("serviceId") String serviceId);
	
	@DeleteMapping("/api/v1/services/deleteService/{serviceId}")
	@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "deleteServiceFallBack")
	public ResponseEntity<ResponseStructure<String>> deleteService(@RequestHeader("Authorization") String token,@PathVariable("serviceId") String serviceId);	

	@PutMapping("/api/v1/services/updateService/{serviceId}")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "updateByServiceIdFallBack")
	public ResponseEntity<ResponseStructure<ServicesDto>> updateByServiceId(@RequestHeader("Authorization") String token,@PathVariable("serviceId") String serviceId,
			@RequestBody ServicesDto domainServices);
	
	@GetMapping("/api/v1/services/getAllServices")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getAllServicesFallBack")
	public ResponseEntity<ResponseStructure<List<ServicesDto>>> getAllServices(@RequestHeader("Authorization") String token);
	
	
	// SUBSERVICES
	
	@PostMapping("/api/v1/SubServicesInfo/addSubService")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "addSubServiceFallBack")
	public ResponseEntity<Response> addSubService(@RequestHeader("Authorization") String token,@RequestBody SubServicesInfoDto dto);
	
	@GetMapping("/api/v1/SubServicesInfo/getSubServiceByIdCategory/{categoryId}")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getSubServiceByIdCategoryFallBack")
	public ResponseEntity<Response> getSubServiceInfoByIdCategory(@RequestHeader("Authorization") String token,@PathVariable("categoryId") String categoryId);
	
	@GetMapping("/api/v1/SubServicesInfo/getSubServicesByServiceId/{serviceId}")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getSubServicesByServiceIdFallBack")
	public ResponseEntity<Response> getSubServicesInfoByServiceId(@RequestHeader("Authorization") String token,@PathVariable("serviceId") String serviceId);
	
	@GetMapping("/api/v1/SubServicesInfo/getSubServiceBySubServiceId/{subServiceId}")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getSubServiceBySubServiceIdFallBack")
	public ResponseEntity<Response> getSubServiceBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable("subServiceId") String subServiceId);
	
	@GetMapping("/api/v1/SubServicesInfo/getAllSubServices")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "getAllSubServicesFallBack")
	public ResponseEntity<Response> getAllSubServicesInfo(@RequestHeader("Authorization") String token);
	
	@PutMapping("/api/v1/SubServicesInfo/updateBySubServiceId/{subServiceId}")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = "updateBySubServiceIdFallBack")
	public ResponseEntity<Response> updateBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable("subServiceId") String subServiceId,
			@RequestBody SubServicesInfoDto domainServices);
	
	@DeleteMapping("/api/v1/SubServicesInfo/deleteSubService/{subServiceId}")
	 @CircuitBreaker(name = "circuitBreaker", fallbackMethod = " deleteSubServiceFallBack")
	public ResponseEntity<Response> deleteSubServiceInfo(@RequestHeader("Authorization") String token,@PathVariable("subServiceId") String subServiceId);
	
	
///PROCEDURE CRUD
  	
 
	 @PostMapping("/api/v1/subServices/addSubService/{subServiceId}")
		public ResponseEntity<ResponseStructure<SubServicesDto>> addService(@RequestHeader("Authorization") String token,@PathVariable String subServiceId, @RequestBody SubServicesDto dto);

	    @GetMapping("/api/v1/subServices/getSubServicesbycategoryId/{categoryId}")
	    ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceByIdCategory(@RequestHeader("Authorization") String token,@PathVariable String categoryId);

	    @GetMapping("/api/v1/subServices/getSubServicesbyserviceId/{serviceId}")
	    ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServicesByServiceId(@RequestHeader("Authorization") String token,@PathVariable String serviceId);

	    @GetMapping("/api/v1/subServices/getSubService/{subServiceId}")
	    ResponseEntity<ResponseStructure<SubServicesDto>> retrieveSubServicesBySubServiceId(@RequestHeader("Authorization") String token, @PathVariable String subServiceId);


		@DeleteMapping("/api/v1/subServices/deleteBySubServiceId/{hospitalId}/{subServiceId}")
		public ResponseEntity<ResponseStructure<SubServicesDto>> deleteSubService(@RequestHeader("Authorization") String token,@PathVariable String hospitalId,@PathVariable String subServiceId);

		@PutMapping("/api/v1/subServices/updateSubService/{hospitalId}/{subServiceId}")
		public ResponseEntity<ResponseStructure<SubServicesDto>> updateBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable String hospitalId,@PathVariable String subServiceId,
				@RequestBody SubServicesDto domainServices);

	    @GetMapping("/api/v1/subServices/getAllSubServices")  
	    ResponseEntity<ResponseStructure<List<SubServicesDto>>> getAllSubServices(@RequestHeader("Authorization") String token);
	    
		@GetMapping("/api/v1/subServices/getSubService/{hospitalId}/{subServiceId}")
		public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceBySubServiceId(@RequestHeader("Authorization") String token,@PathVariable String hospitalId, @PathVariable String subServiceId);
		
		@GetMapping("/api/v1/subServices/getSubService/{hospitalId}")
		public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceByHospitalId(@RequestHeader("Authorization") String token,@PathVariable String hospitalId);
	    
		@GetMapping("/api/v1/SubServicesInfo/exists/{id}")
		public boolean isSubServiceExists(@RequestHeader("Authorization") String token,@PathVariable("id") String id);
	    

	//CATEGORY FALLBACK METHODS
	
	default ResponseEntity<?> addNewCategoryFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<CategoryDto>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	
	default ResponseEntity<?> getAllCategoryFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<List<CategoryDto>>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	default ResponseEntity<?> getCategoryByIdFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<CategoryDto>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	default ResponseEntity<?> deleteCategoryFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<CategoryDto>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	default ResponseEntity<?> updateCategoryFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<CategoryDto>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	///SERVICE FALLBACK METHODS
	
	default ResponseEntity<?> addServiceFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<CategoryDto>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	default ResponseEntity<?> getServiceByIdFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<List<ServicesDto>>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	default ResponseEntity<?> getServiceByServiceIdFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<CategoryDto>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	default ResponseEntity<?> deleteServiceFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<CategoryDto>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	default ResponseEntity<?> updateByServiceIdFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<CategoryDto>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	default ResponseEntity<?> getAllServicesFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new ResponseStructure<List<ServicesDto>>(null,ExtractFeignMessage.clearMessage(e),null,e.status()));
		}
	
	
	/// SUBSERVICE FALLBACK METHODS
	
	default ResponseEntity<?> addSubServiceFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));}
	
	default ResponseEntity<?> getSubServiceByIdCategoryFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));}
	
	default ResponseEntity<?> getSubServicesByServiceIdFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));}
	
	default ResponseEntity<?> getSubServiceBySubServiceIdFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));}
	
	default ResponseEntity<?> getAllSubServicesFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));}
	
	default ResponseEntity<?> updateBySubServiceIdFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));}
	
	default ResponseEntity<?> deleteSubServiceFallBack(FeignException e){		 
		return ResponseEntity.status(e.status()).body(new Response(false,null,ExtractFeignMessage.clearMessage(e),e.status(),null,null));}
	
    
}
