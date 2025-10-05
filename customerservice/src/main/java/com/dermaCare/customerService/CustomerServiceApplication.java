package com.dermaCare.customerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@EnableRetry
public class CustomerServiceApplication {
	public static void main(String[] args)throws Exception {
		SpringApplication.run(CustomerServiceApplication.class, args);	
//	  KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//      SecretKey secretKey = keyGen.generateKey();
//      String encoded = Base64.getEncoder().encodeToString(secretKey.getEncoded());
//      System.out.println("Secret Key (Base64): " + encoded);     		 
}
}
