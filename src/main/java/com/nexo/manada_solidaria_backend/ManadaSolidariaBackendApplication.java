package com.nexo.manada_solidaria_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ManadaSolidariaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManadaSolidariaBackendApplication.class, args);
	}

}
