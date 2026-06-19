package com.unmsm.marketplace.ordenes_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.unmsm.marketplace.ordenes_service.client")
public class OrdenesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdenesServiceApplication.class, args);
	}

}
