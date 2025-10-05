package com.clinicadmin.feignclient;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.clinicadmin.dto.CategoryDto;
import com.clinicadmin.dto.ResponseStructure;
import com.clinicadmin.dto.ServicesDto;
import com.clinicadmin.dto.SubServicesDto;

@FeignClient(name = "category-services")
public interface ServiceFeignClient {


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
	
	@GetMapping("/api/v1/subServices/getSubServiceByHospitalId/{hospitalId}")
	public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceByHospitalId(@RequestHeader("Authorization") String token,@PathVariable String hospitalId);
    
	@GetMapping("/api/v1/SubServicesInfo/exists/{id}")
	public boolean isSubServiceExists(@RequestHeader("Authorization") String token,@PathVariable("id") String id);
    /*  ---------------------------------------------------------------------------------------------------------------------------------------------
	-----------------------------------------------------   Category       ----------------------------------------------------------------------
	--------------------------------------------------------------------------------------------------------------------------------------------- */
	
	@GetMapping("/api/v1/category/getCategory/{categoryId}")
	public ResponseEntity<ResponseStructure<CategoryDto>> getCategoryById(@RequestHeader("Authorization") String token,@PathVariable  String  categoryId);
    
    @GetMapping("/api/v1/category/getCategories")
	public ResponseEntity<ResponseStructure<List<CategoryDto>>> getAllCategory(@RequestHeader("Authorization") String token);
    
    @GetMapping("/api/v1/category/exists/{id}")
    boolean isCategoryExists(@RequestHeader("Authorization") String token,@PathVariable("id") String id);

    /*  -----------------------------------------------------------------------------------------------------------------------------------------
 	-----------------------------------------------------   Services       ----------------------------------------------------------------------
	--------------------------------------------------------------------------------------------------------------------------------------------- */
	@GetMapping("/api/v1/services/getServices/{categoryId}")
	public ResponseEntity<ResponseStructure<List<ServicesDto>>> getServiceById(@RequestHeader("Authorization") String token,@PathVariable String categoryId);
     
	@GetMapping("/api/v1/services/getService/{serviceId}")
	public ResponseEntity<ResponseStructure<ServicesDto>> getServiceByServiceId(@RequestHeader("Authorization") String token,@PathVariable String serviceId);
    
	@GetMapping("/api/v1/services/getAllServices")
	public ResponseEntity<ResponseStructure<List<ServicesDto>>> getAllServices(@RequestHeader("Authorization") String token);
	
	@GetMapping("/api/v1/services/exists/{id}")
	boolean isServiceExists(@RequestHeader("Authorization") String token,@PathVariable("id") String id);
    
    
}