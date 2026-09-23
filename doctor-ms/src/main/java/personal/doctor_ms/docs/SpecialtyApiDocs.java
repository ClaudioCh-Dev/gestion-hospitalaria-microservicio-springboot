package personal.doctor_ms.docs;

import java.util.List;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import personal.doctor_ms.dtos.CreateSpecialtyRequest;
import personal.doctor_ms.dtos.SpecialtyResponse;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de SpecialtyController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Especialidades", description = "Catálogo de especialidades médicas")
public interface SpecialtyApiDocs {

    @Operation(summary = "Listar especialidades", description = "Requiere SPECIALTY_READ.")
    @ApiResponse(responseCode = "200", description = "Lista de especialidades",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = SpecialtyResponse.class)),
                    examples = @ExampleObject(value = DoctorExamples.SPECIALTY_LIST)))
    ResponseEntity<List<SpecialtyResponse>> findAllSpecialties();

    @Operation(summary = "Crear especialidad", description = "Requiere SPECIALTY_CREATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateSpecialtyRequest.class),
                            examples = @ExampleObject(value = DoctorExamples.CREATE_SPECIALTY_REQUEST))))
    @ApiResponse(responseCode = "201", description = "Especialidad creada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SpecialtyResponse.class),
                    examples = @ExampleObject(value = DoctorExamples.SPECIALTY_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Validación", value = ErrorExamples.VALIDATION_ERROR),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    @ApiResponse(responseCode = "409", description = "Violación de una restricción de la base de datos",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.DATA_INTEGRITY_ERROR)))
    ResponseEntity<SpecialtyResponse> createSpecialty(CreateSpecialtyRequest request);
}
