package com.aps.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

	private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				logger.info("Configuring CORS mappings");

				// Allow specific origins for credentials
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:4200")  // Specify allowed origin
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
						.allowedHeaders("*")
						.exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
						.allowCredentials(true)
						.maxAge(3600); // Cache preflight requests for 1 hour

				logger.info("CORS configuration applied successfully");
			}
		};
	}


}
