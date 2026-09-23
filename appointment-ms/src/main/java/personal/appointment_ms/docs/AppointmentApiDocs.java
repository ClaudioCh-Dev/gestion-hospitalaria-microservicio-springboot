package personal.appointment_ms.docs;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import personal.appointment_ms.dto.AppointmentResponse;
import personal.appointment_ms.dto.CreateAppointmentRequest;
import personal.appointment_ms.dto.UpdateAppointmentStatusRequest;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de AppointmentController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Citas", description = "Programación y seguimiento de citas médicas")
public interface AppointmentApiDocs {

    @Operation(summary = "Programar cita",
            description = "Crea una cita validando paciente, doctor, tipo de cita, tarifa y disponibilidad del doctor. Requiere APPOINTMENT_CREATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateAppointmentRequest.class),
                            examples = @ExampleObject(value = AppointmentExamples.CREATE_APPOINTMENT_REQUEST))))
    @ApiResponse(responseCode = "201", description = "Cita creada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentResponse.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Validación", value = ErrorExamples.VALIDATION_ERROR),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    @ApiResponse(responseCode = "404", description = "Paciente, doctor o tipo de cita no encontrado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Paciente", value = AppointmentExamples.PATIENT_NOT_FOUND),
                            @ExampleObject(name = "Doctor", value = AppointmentExamples.DOCTOR_NOT_FOUND),
                            @ExampleObject(name = "Tipo de cita", value = AppointmentExamples.APPOINTMENT_TYPE_NOT_FOUND)
                    }))
    @ApiResponse(responseCode = "409", description = "Conflicto de negocio al programar la cita",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Doctor ocupado", value = AppointmentExamples.DOCTOR_NOT_AVAILABLE),
                            @ExampleObject(name = "Tipo inactivo", value = AppointmentExamples.APPOINTMENT_TYPE_NOT_ACTIVE),
                            @ExampleObject(name = "Sin tarifa", value = AppointmentExamples.APPOINTMENT_TARIFF_INVALID)
                    }))
    @ApiResponse(responseCode = "503", description = "Un microservicio dependiente no está disponible",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Patient MS", value = AppointmentExamples.PATIENT_SERVICE_UNAVAILABLE),
                            @ExampleObject(name = "Doctor MS", value = AppointmentExamples.DOCTOR_SERVICE_UNAVAILABLE),
                            @ExampleObject(name = "Billing MS", value = AppointmentExamples.BILLING_SERVICE_UNAVAILABLE)
                    }))
    ResponseEntity<AppointmentResponse> createAppointment(CreateAppointmentRequest request);

    @Operation(summary = "Listar citas",
            description = "Devuelve las citas paginadas (por defecto 10 por página, ordenadas por fecha de creación). Requiere APPOINTMENT_READ.")
    @ApiResponse(responseCode = "200", description = "Página de citas",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_PAGE)))
    ResponseEntity<Page<AppointmentResponse>> getAppointments(Pageable pageable);

    @Operation(summary = "Obtener cita por ID", description = "Requiere APPOINTMENT_READ.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID de la cita", example = "10")
    @ApiResponse(responseCode = "200", description = "Cita encontrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentResponse.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    @ApiResponse(responseCode = "404", description = "Cita no encontrada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_NOT_FOUND)))
    ResponseEntity<AppointmentResponse> getAppointmentById(Long id);

    @Operation(summary = "Citas de un paciente", description = "Requiere APPOINTMENT_READ_BY_PATIENT.")
    @Parameter(name = "patientId", in = ParameterIn.PATH, description = "ID del paciente", example = "1")
    @ApiResponse(responseCode = "200", description = "Citas del paciente (lista vacía si no tiene)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class)),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_LIST)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(Long patientId);

    @Operation(summary = "Citas de un doctor", description = "Requiere APPOINTMENT_READ_BY_DOCTOR.")
    @Parameter(name = "doctorId", in = ParameterIn.PATH, description = "ID del doctor", example = "3")
    @ApiResponse(responseCode = "200", description = "Citas del doctor (lista vacía si no tiene)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class)),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_LIST)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(Long doctorId);

    @Operation(summary = "Citas de una fecha", description = "Devuelve las citas programadas en el día indicado. Requiere APPOINTMENT_READ.")
    @Parameter(name = "date", in = ParameterIn.PATH, description = "Fecha en formato ISO (yyyy-MM-dd)", example = "2026-10-05")
    @ApiResponse(responseCode = "200", description = "Citas del día (lista vacía si no hay)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AppointmentResponse.class)),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_LIST)))
    @ApiResponse(responseCode = "400", description = "Fecha con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    ResponseEntity<List<AppointmentResponse>> getAppointmentsByDate(LocalDate date);

    @Operation(summary = "Cambiar estado de la cita",
            description = "Cambia el estado (SCHEDULED, CONFIRMED, COMPLETED, CANCELLED). Una cita completada o cancelada ya no puede cambiar. Requiere APPOINTMENT_UPDATE_STATUS.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateAppointmentStatusRequest.class),
                            examples = @ExampleObject(value = AppointmentExamples.UPDATE_STATUS_REQUEST))))
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID de la cita", example = "10")
    @ApiResponse(responseCode = "200", description = "Estado actualizado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentResponse.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "Estado inválido o JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Validación", value = ErrorExamples.VALIDATION_ERROR),
                            @ExampleObject(name = "JSON inválido", value = ErrorExamples.INVALID_REQUEST_BODY)
                    }))
    @ApiResponse(responseCode = "404", description = "Cita no encontrada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_NOT_FOUND)))
    @ApiResponse(responseCode = "409", description = "Transición de estado no permitida",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Mismo estado", value = AppointmentExamples.APPOINTMENT_STATUS_ALREADY_SET),
                            @ExampleObject(name = "Estado final", value = AppointmentExamples.APPOINTMENT_STATUS_CANNOT_CHANGE)
                    }))
    ResponseEntity<AppointmentResponse> updateStatus(Long id, UpdateAppointmentStatusRequest request);

    @Operation(summary = "Cancelar cita", description = "Cambia el estado de la cita a CANCELLED. Requiere APPOINTMENT_CANCEL.")
    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID de la cita", example = "10")
    @ApiResponse(responseCode = "204", description = "Cita cancelada", content = @Content)
    @ApiResponse(responseCode = "404", description = "Cita no encontrada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = AppointmentExamples.APPOINTMENT_NOT_FOUND)))
    @ApiResponse(responseCode = "409", description = "La cita ya está cancelada o completada",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = {
                            @ExampleObject(name = "Ya cancelada", value = AppointmentExamples.APPOINTMENT_STATUS_ALREADY_SET),
                            @ExampleObject(name = "Estado final", value = AppointmentExamples.APPOINTMENT_STATUS_CANNOT_CHANGE)
                    }))
    ResponseEntity<Void> cancelAppointment(Long id);
}
