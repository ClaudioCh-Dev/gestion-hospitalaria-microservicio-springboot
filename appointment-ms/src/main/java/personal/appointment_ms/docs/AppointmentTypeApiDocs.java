package personal.appointment_ms.docs;

import java.util.List;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import personal.appointment_ms.dto.AppointmentTypeResponse;
import personal.appointment_ms.dto.CreateAppointmentTypeRequest;
import personal.appointment_ms.dto.UpdateAppointmentTypeRequest;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de AppointmentTypeController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Tipos de cita", description = "Catálogo de tipos de cita (consulta general, control, etc.)")
public interface AppointmentTypeApiDocs {

    @Operation(summary = "Crear tipo de cita",
            description = "Crea el tipo de cita INACTIVO y publica un evento para que billing-ms registre su tarifa. Requiere APPOINTMENT_TYPE_CREATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateAppointmentTypeRequest.class),
                            examples = @ExampleObject(value = AppointmentExamples.CREATE_APPOINTMENT_TYPE_REQUEST))))
    @ApiResponse(responseCode = "201", description = "Tipo de cita creado (inactivo)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentTypeResponse.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_TYPE_CREATED)))
    @ApiResponse(responseCode = "400", description = "JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_REQUEST_BODY)))
    @ApiResponse(responseCode = "409", description = "Violación de una restricción de la base de datos",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.DATA_INTEGRITY_ERROR)))
    ResponseEntity<AppointmentTypeResponse> create(CreateAppointmentTypeRequest request);

    @Operation(summary = "Listar tipos de cita", description = "Requiere APPOINTMENT_TYPE_READ.")
    @ApiResponse(responseCode = "200", description = "Lista de tipos de cita",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AppointmentTypeResponse.class)),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_TYPE_LIST)))
    ResponseEntity<List<AppointmentTypeResponse>> findAll();

    @Operation(summary = "Obtener tipo de cita por ID", description = "Requiere APPOINTMENT_TYPE_READ.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del tipo de cita", example = "2")
    @ApiResponse(responseCode = "200", description = "Tipo de cita encontrado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentTypeResponse.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_TYPE_RESPONSE)))
    @ApiResponse(responseCode = "404", description = "Tipo de cita no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_TYPE_NOT_FOUND)))
    ResponseEntity<AppointmentTypeResponse> findById(Long id);

    @Operation(summary = "Actualizar tipo de cita", description = "Requiere APPOINTMENT_TYPE_UPDATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateAppointmentTypeRequest.class),
                            examples = @ExampleObject(value = AppointmentExamples.UPDATE_APPOINTMENT_TYPE_REQUEST))))
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del tipo de cita", example = "2")
    @ApiResponse(responseCode = "200", description = "Tipo de cita actualizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentTypeResponse.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_TYPE_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_REQUEST_BODY)))
    @ApiResponse(responseCode = "404", description = "Tipo de cita no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_TYPE_NOT_FOUND)))
    ResponseEntity<AppointmentTypeResponse> update(Long id, UpdateAppointmentTypeRequest request);

    @Operation(summary = "Desactivar tipo de cita", description = "Marca el tipo de cita como inactivo. Requiere APPOINTMENT_TYPE_DELETE.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del tipo de cita", example = "2")
    @ApiResponse(responseCode = "204", description = "Tipo de cita desactivado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Tipo de cita no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_TYPE_NOT_FOUND)))
    @ApiResponse(responseCode = "409", description = "El tipo de cita ya estaba inactivo",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_TYPE_ALREADY_INACTIVE)))
    ResponseEntity<Void> deactivate(Long id);
}
