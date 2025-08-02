package com.example.WoollyProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WoollyProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WoollyProjectApplication.class, args);
	}

}
