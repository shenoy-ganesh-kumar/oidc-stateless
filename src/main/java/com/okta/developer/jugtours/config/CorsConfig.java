package com.okta.developer.jugtours.config;

import org.springframework.context.annotation.Configuration;
//import org.apache.commons.logging.Log;
///import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration
 */
//@Configuration
//@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

	//private static final Log log = LogFactory.getLog(CorsConfig.class);

	@Override
	public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
	        .allowedOrigins("*")
	        .allowedMethods("POST, GET, OPTIONS,PUT, DELETE")
	        .allowedHeaders("Content-Type, Accept, X-Requested-With,")
	        //.exposedHeaders(cors.getExposedHeaders())
	        .allowCredentials(true)
	        .maxAge(1800);
	}
}