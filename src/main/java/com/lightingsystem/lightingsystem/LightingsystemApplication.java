package com.lightingsystem.lightingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @SpringBootApplication
// to run in the teriminal : mvn spring-boot:run
// to stop server: crtl c
// for clean run : mvn clean install

public class LightingsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LightingsystemApplication.class, args);
	}

}
