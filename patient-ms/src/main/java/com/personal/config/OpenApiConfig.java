package com.personal.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Patient API", version = "1.0", description = "API for managing patients"))
public class OpenApiConfig {
    
}



