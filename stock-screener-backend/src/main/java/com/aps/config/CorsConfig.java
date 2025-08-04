package com.aps.config;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
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
				
				// Allow all endpoints
				registry.addMapping("/**") 
						.allowedOriginPatterns("*")  // Use patterns instead of specific origins
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
						.allowedHeaders("*")
						.exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
						.allowCredentials(true)
						.maxAge(3600); // Cache preflight requests for 1 hour
				
				logger.info("CORS configuration applied successfully");
			}
		};
	}

	@Component
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public static class CorsFilter implements Filter {

		@Override
		public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
				throws IOException, ServletException {
			HttpServletResponse response = (HttpServletResponse) res;
			HttpServletRequest request = (HttpServletRequest) req;

			// Allow all origins
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", 
				"Origin, X-Requested-With, Content-Type, Accept, Authorization, Cache-Control");

			// Handle preflight requests
			if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}

			chain.doFilter(req, res);
		}
	}
}
