package com.dermaCare.customerService.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dermaCare.customerService.dto.ClinicDTO;
import com.dermaCare.customerService.util.Response;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(value = "adminservice" )
@CircuitBreaker(name = "circuitBreaker", fallbackMethod = "adminServiceFallBack")
public interface AdminFeign {
	 @GetMapping("/admin/getClinicById/{clinicId}")
	    public Response getClinicById(@RequestHeader("Authorization") String token,@PathVariable String clinicId) ;
	 
	 @PutMapping("/admin/updateClinic/{clinicId}")
	    public Response updateClinic(@RequestHeader("Authorization") String token,@PathVariable String clinicId, @RequestBody ClinicDTO clinic);
	 
	 
//	//FALLBACK METHODS		
		default Response adminServiceFallBack(FeignException e){		 
		return new Response(e.getMessage(),e.status(),false,null);
			}
				

}
