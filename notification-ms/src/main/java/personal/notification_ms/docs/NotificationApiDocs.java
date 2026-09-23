package personal.notification_ms.docs;

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
import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.shared.docs.ErrorExamples;

/**
 * Documentación OpenAPI de NotificationController.
 * Las respuestas 403 y 500 se agregan globalmente en OpenApiConfig.
 */
@Tag(name = "Notificaciones", description = "Notificaciones de citas para doctores y administradores")
public interface NotificationApiDocs {

    @Operation(summary = "Registrar notificación",
            description = "Guarda una notificación manualmente (normalmente se crean desde los eventos de Kafka). Requiere NOTIFICATION_CREATE.",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationRequest.class),
                            examples = @ExampleObject(value = NotificationExamples.NOTIFICATION_REQUEST))))
    @ApiResponse(responseCode = "200", description = "Notificación registrada",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = NotificationResponse.class),
                    examples = @ExampleObject(value = NotificationExamples.NOTIFICATION_RESPONSE)))
    @ApiResponse(responseCode = "400", description = "JSON mal formado",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_REQUEST_BODY)))
    ResponseEntity<NotificationResponse> save(NotificationRequest request);

    @Operation(summary = "Notificaciones de un doctor", description = "Requiere NOTIFICATION_READ_DOCTOR.")
    @Parameter(name = "doctorId", in = ParameterIn.PATH, description = "ID del doctor", example = "3")
    @ApiResponse(responseCode = "200", description = "Notificaciones del doctor (lista vacía si no tiene)",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class)),
                    examples = @ExampleObject(value = NotificationExamples.NOTIFICATION_LIST)))
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    ResponseEntity<List<NotificationResponse>> findMyDoctorNotifications(Long doctorId);

    @Operation(summary = "Notificaciones para administración",
            description = "Todas las notificaciones, de la más reciente a la más antigua. Requiere NOTIFICATION_READ_ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class)),
                    examples = @ExampleObject(value = NotificationExamples.NOTIFICATION_LIST)))
    ResponseEntity<List<NotificationResponse>> findForAdmin();

    @Operation(summary = "Marcar notificación como leída",
            description = "Marca la notificación como leída para el usuario autenticado. Requiere NOTIFICATION_MARK_READ_DOCTOR.")
    @Parameter(name = "notificationId", in = ParameterIn.PATH, description = "ID de la notificación", example = "25")
    @ApiResponse(responseCode = "204", description = "Notificación marcada como leída", content = @Content)
    @ApiResponse(responseCode = "400", description = "ID con formato inválido",
            content = @Content(mediaType = ErrorExamples.PROBLEM_JSON,
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = ErrorExamples.INVALID_PARAMETER)))
    ResponseEntity<Void> markAsRead(Long notificationId);
}
