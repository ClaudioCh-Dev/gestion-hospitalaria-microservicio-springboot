package personal.doctor_ms.docs;

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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import personal.doctor_ms.dtos.CreateDoctorRequest;
import personal.doctor_ms.dtos.DoctorResponse;
import personal.doctor_ms.dtos.UpdateDoctorRequest;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de DoctorController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Doctores", description = "Gestión de doctores del hospital")
public interface DoctorApiDocs {

    @Operation(summary = "Listar doctores", description = "Devuelve los doctores paginados. Permite buscar por texto. Requiere DOCTOR_READ.")
    @Parameter(name = "search", in = ParameterIn.QUERY, description = "Búsqueda opcional por nombre, colegiatura, correo o especialidad (sin distinguir mayúsculas)", example = "ramírez")
    @ApiResponse(responseCode = "200", description = "Página de doctores",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = DoctorExamples.DOCTOR_PAGE)))
    ResponseEntity<Page<DoctorResponse>> findAll(String search, Pageable pageable);

    @Operation(summary = "Obtener doctor por ID", description = "Requiere DOCTOR_READ.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del doctor", example = "3")
    @ApiResponse(responseCode = "200", description = "Doctor encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DoctorResponse.class),
                    examples = @ExampleObject(value = DoctorExamples.DOCTOR_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    @ApiResponse(responseCode = "404", description = "Doctor no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoctorExamples.DOCTOR_NOT_FOUND)))
    ResponseEntity<DoctorResponse> findById(Long id);

    @Operation(summary = "Doctores por especialidad", description = "Devuelve los doctores de una especialidad, paginados. Requiere DOCTOR_READ_BY_SPECIALTY.")
    @Parameter(name = "specialtyId", in = ParameterIn.PATH, description = "ID de la especialidad", example = "1")
    @Parameter(name = "search", in = ParameterIn.QUERY, description = "Búsqueda opcional por nombre, colegiatura, correo o especialidad (sin distinguir mayúsculas)", example = "ramírez")
    @ApiResponse(responseCode = "200", description = "Página de doctores de la especialidad",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = DoctorExamples.DOCTOR_PAGE)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    ResponseEntity<Page<DoctorResponse>> findBySpecialty(Long specialtyId, String search, Pageable pageable);

    @Operation(summary = "Registrar doctor",
            description = "Crea el doctor y su usuario de acceso en auth-server. Requiere DOCTOR_CREATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateDoctorRequest.class),
                            examples = @ExampleObject(value = DoctorExamples.CREATE_DOCTOR_REQUEST))))
    @ApiResponse(responseCode = "201", description = "Doctor creado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DoctorResponse.class),
                    examples = @ExampleObject(value = DoctorExamples.DOCTOR_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Validación", value = ErrorExamples.VALIDATION_ERROR),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    @ApiResponse(responseCode = "404", description = "Especialidad no encontrada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoctorExamples.SPECIALTY_NOT_FOUND)))
    @ApiResponse(responseCode = "409", description = "El correo ya está registrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoctorExamples.DOCTOR_ALREADY_EXISTS)))
    @ApiResponse(responseCode = "503", description = "auth-server no disponible",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = DoctorExamples.AUTH_SERVICE_UNAVAILABLE)))
    ResponseEntity<DoctorResponse> create(CreateDoctorRequest request);

    @Operation(summary = "Actualizar doctor", description = "Requiere DOCTOR_UPDATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateDoctorRequest.class),
                            examples = @ExampleObject(value = DoctorExamples.UPDATE_DOCTOR_REQUEST))))
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del doctor", example = "3")
    @ApiResponse(responseCode = "200", description = "Doctor actualizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DoctorResponse.class),
                    examples = @ExampleObject(value = DoctorExamples.DOCTOR_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Validación", value = ErrorExamples.VALIDATION_ERROR),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    @ApiResponse(responseCode = "404", description = "Doctor o especialidad no encontrados",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Doctor", value = DoctorExamples.DOCTOR_NOT_FOUND),
                            @ExampleObject(name = "Especialidad", value = DoctorExamples.SPECIALTY_NOT_FOUND)
                    }))
    ResponseEntity<DoctorResponse> update(Long id, UpdateDoctorRequest request);
}
