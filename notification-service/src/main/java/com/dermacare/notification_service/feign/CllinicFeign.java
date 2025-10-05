package com.dermacare.notification_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.dermacare.notification_service.dto.Response;



@FeignClient(value = "clinicadmin")
public interface CllinicFeign {

	@GetMapping("/clinic-admin/doctor/{id}")
	public ResponseEntity<Response> getDoctorById(@RequestHeader("Authorization")String token,@PathVariable String id);
	
	@PutMapping("/clinic-admin/updateDoctorSlotWhileBooking/{doctorId}/{date}/{time}")
	public Boolean updateDoctorSlotWhileBooking(@RequestHeader("Authorization")String token,@PathVariable String doctorId, @PathVariable String date,
			@PathVariable String time);
	
	@PutMapping("/clinic-admin/makingFalseDoctorSlot/{doctorId}/{date}/{time}")
	public Boolean makingFalseDoctorSlot(@RequestHeader("Authorization")String token,@PathVariable String doctorId, @PathVariable String date,
			@PathVariable String time);
	
	
}
