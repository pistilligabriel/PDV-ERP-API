package com.learning.api.angularsystem;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SysSellAplication {

	public static void main(String[] args) {
		SpringApplication.run(SysSellAplication.class, args);
	}


	@Bean
	public WebMvcConfigurer corsLocalConfig() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
						.allowedHeaders("Origin","X-Requested-With", "Content-Type", "Accept", "Authorization")
						.allowedOrigins("http://localhost:4200")
						.allowCredentials(true);
			}
		};	
	}


	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Angular System")
						.version("1.0.0"));
	}


}