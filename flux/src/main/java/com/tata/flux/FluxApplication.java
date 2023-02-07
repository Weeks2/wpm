package com.tata.flux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * http://localhost:8080/posts?uri=https://despertardelacosta.net
 * http://localhost:8080/posts?uri=https://suracapulco.mx/
 */
@SpringBootApplication
@EnableScheduling
public class FluxApplication {
	public static void main(String[] args) {
		SpringApplication.run(FluxApplication.class, args);
	}

}
