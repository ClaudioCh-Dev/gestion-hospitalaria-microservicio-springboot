package com.personal.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import personal.shared.docs.ErrorExamples;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Patient API", version = "1.0", description = "API for managing patients"))
public class OpenApiConfig {

    /**
     * Agrega a TODOS los endpoints las respuestas 403 y 500, que son comunes
     * y no hace falta repetirlas en cada interfaz de documentación.
     */
    @Bean
    public OpenApiCustomizer globalErrorResponsesCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    operation.getResponses().putIfAbsent("403",
                            problemResponse("Sin permisos para esta operación", ErrorExamples.ACCESS_DENIED));
                    operation.getResponses().putIfAbsent("500",
                            problemResponse("Error interno del servidor", ErrorExamples.INTERNAL_ERROR));
                }));
    }

    private static ApiResponse problemResponse(String description, String example) {
        try {
            return new ApiResponse()
                    .description(description)
                    .content(new Content().addMediaType(
                            ErrorExamples.PROBLEM_JSON,
                            new MediaType().example(Json.mapper().readTree(example))));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ejemplo OpenAPI inválido", e);
        }
    }
}
