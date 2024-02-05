package com.demoProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class StaffOnbaordingApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(StaffOnbaordingApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(StaffOnbaordingApplication.class);
	}

}
