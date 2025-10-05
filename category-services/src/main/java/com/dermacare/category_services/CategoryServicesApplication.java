package com.dermacare.category_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
@EnableFeignClients
@EnableScheduling
public class CategoryServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CategoryServicesApplication.class, args);
	}

}
