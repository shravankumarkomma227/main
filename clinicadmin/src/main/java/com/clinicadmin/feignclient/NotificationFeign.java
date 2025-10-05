package com.clinicadmin.feignclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "notification-service")
public interface NotificationFeign {
	
	
	@GetMapping("/api/notificationservice/sendNotificationToClinic/{clinicId}")
	public ResponseEntity<?> sendNotificationToClinic(@RequestHeader("Authorization") String token,@PathVariable String clinicId );
	
	
	
	
}
