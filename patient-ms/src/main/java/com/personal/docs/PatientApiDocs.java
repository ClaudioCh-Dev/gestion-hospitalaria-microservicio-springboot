package com.personal.docs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import com.personal.dto.PatientDetailResponse;
import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import com.personal.enums.Gender;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de PatientController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Pacientes", description = "Gestión de pacientes del hospital")
public interface PatientApiDocs {

    @Operation(summary = "Listar pacientes", description = "Devuelve los pacientes paginados. Permite filtrar por género. Requiere PATIENT_READ.")
    @Parameter(name = "gender", in = ParameterIn.QUERY, description = "Filtro opcional por género", example = "FEMALE")
    @ApiResponse(responseCode = "200", description = "Página de pacientes",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = PatientExamples.PATIENT_PAGE)))
    @ApiResponse(responseCode = "400", description = "Parámetro inválido (por ejemplo, género inexistente)",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    ResponseEntity<Page<PatientResponse>> findAll(Gender gender, Pageable pageable);

    @Operation(summary = "Obtener paciente por ID", description = "Devuelve el detalle completo del paciente. Requiere PATIENT_READ.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del paciente", example = "1")
    @ApiResponse(responseCode = "200", description = "Paciente encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientDetailResponse.class),
                    examples = @ExampleObject(value = PatientExamples.PATIENT_DETAIL_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    @ApiResponse(responseCode = "404", description = "Paciente no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = PatientExamples.PATIENT_NOT_FOUND)))
    ResponseEntity<PatientDetailResponse> findById(Long id);

    @Operation(summary = "Buscar paciente por documento", description = "Busca un paciente por su número de documento. Requiere PATIENT_READ.")
    @Parameter(name = "documentNumber", in = ParameterIn.PATH, description = "Número de documento", example = "74859612")
    @ApiResponse(responseCode = "200", description = "Paciente encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientResponse.class),
                    examples = @ExampleObject(value = PatientExamples.PATIENT_RESPONSE)))
    @ApiResponse(responseCode = "404", description = "Paciente no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = PatientExamples.PATIENT_NOT_FOUND)))
    ResponseEntity<PatientResponse> findByDocumentNumber(String documentNumber);

    @Operation(summary = "Registrar paciente", description = "Crea un nuevo paciente. Requiere PATIENT_CREATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientRequest.class),
                            examples = @ExampleObject(value = PatientExamples.PATIENT_REQUEST))))
    @ApiResponse(responseCode = "201", description = "Paciente creado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientResponse.class),
                    examples = @ExampleObject(value = PatientExamples.PATIENT_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Validación", value = ErrorExamples.VALIDATION_ERROR),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    @ApiResponse(responseCode = "409", description = "Documento o correo ya registrados",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Documento duplicado", value = PatientExamples.PATIENT_DOCUMENT_ALREADY_EXISTS),
                            @ExampleObject(name = "Correo duplicado", value = PatientExamples.PATIENT_EMAIL_ALREADY_EXISTS)
                    }))
    ResponseEntity<PatientResponse> create(PatientRequest request);

    @Operation(summary = "Actualizar paciente", description = "Actualiza los datos de un paciente existente. Requiere PATIENT_UPDATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientRequest.class),
                            examples = @ExampleObject(value = PatientExamples.PATIENT_REQUEST))))
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del paciente", example = "1")
    @ApiResponse(responseCode = "200", description = "Paciente actualizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientResponse.class),
                    examples = @ExampleObject(value = PatientExamples.PATIENT_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Validación", value = ErrorExamples.VALIDATION_ERROR),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    @ApiResponse(responseCode = "404", description = "Paciente no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = PatientExamples.PATIENT_NOT_FOUND)))
    @ApiResponse(responseCode = "409", description = "Documento o correo ya registrados por otro paciente",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Documento duplicado", value = PatientExamples.PATIENT_DOCUMENT_ALREADY_EXISTS),
                            @ExampleObject(name = "Correo duplicado", value = PatientExamples.PATIENT_EMAIL_ALREADY_EXISTS)
                    }))
    ResponseEntity<PatientResponse> update(Long id, PatientRequest request);

    @Operation(summary = "Eliminar paciente", description = "Elimina (desactiva) un paciente. Requiere PATIENT_DELETE.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del paciente", example = "1")
    @ApiResponse(responseCode = "204", description = "Paciente eliminado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Paciente no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = PatientExamples.PATIENT_NOT_FOUND)))
    ResponseEntity<Void> delete(Long id);
}
