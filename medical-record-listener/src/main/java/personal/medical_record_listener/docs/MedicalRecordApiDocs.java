package personal.medical_record_listener.docs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.medical_record_listener.dto.MedicalRecordSummaryResponse;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de MedicalRecordController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Historial médico", description = "Historial de citas de los pacientes, construido a partir de eventos")
public interface MedicalRecordApiDocs {

    @Operation(summary = "Historial de un paciente",
            description = "Devuelve el historial médico del paciente, paginado. Requiere MEDICAL_RECORD_READ_BY_PATIENT.")
    @Parameter(name = "patientId", in = ParameterIn.PATH, description = "ID del paciente", example = "1")
    @ApiResponse(responseCode = "200", description = "Página del historial del paciente",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = MedicalRecordExamples.MEDICAL_RECORD_PAGE)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    @ApiResponse(responseCode = "404", description = "El paciente no tiene historial",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = MedicalRecordExamples.MEDICAL_RECORD_NOT_FOUND)))
    ResponseEntity<Page<MedicalRecordResponse>> findByPatientId(Long patientId, Pageable pageable);

    @Operation(summary = "Listar historiales", description = "Devuelve los registros del historial paginados, con búsqueda y filtro por especialidad opcionales. Requiere MEDICAL_RECORD_READ.")
    @Parameter(name = "search", in = ParameterIn.QUERY, description = "Búsqueda opcional por paciente, médico, especialidad o motivo (sin distinguir mayúsculas)", example = "cardio")
    @Parameter(name = "specialty", in = ParameterIn.QUERY, description = "Filtro opcional por especialidad exacta", example = "Cardiología")
    @ApiResponse(responseCode = "200", description = "Página de historiales",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = MedicalRecordExamples.MEDICAL_RECORD_PAGE)))
    ResponseEntity<Page<MedicalRecordResponse>> findAll(String search, String specialty, Pageable pageable);

    @Operation(summary = "Resumen del historial", description = "Total de consultas, pacientes únicos, consultas completadas, ingresos y especialidades registradas. Requiere MEDICAL_RECORD_READ.")
    @ApiResponse(responseCode = "200", description = "Resumen del historial",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MedicalRecordSummaryResponse.class)))
    ResponseEntity<MedicalRecordSummaryResponse> getSummary();
}
