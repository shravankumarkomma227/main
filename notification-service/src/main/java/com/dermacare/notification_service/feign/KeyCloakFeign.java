package com.dermacare.notification_service.feign;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.dermacare.notification_service.dto.JwksResponse;


@FeignClient(name = "keycloak-client" , url = "http://localhost:9091/realms/Dermacare/protocol/openid-connect" )
public interface KeyCloakFeign {

	  @PostMapping(value = "/token", 
              consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	  Map<String, Object> getToken(@RequestBody MultiValueMap<String, String> form);
	  
	  @GetMapping("/certs")
	  JwksResponse getJWKS();
}
