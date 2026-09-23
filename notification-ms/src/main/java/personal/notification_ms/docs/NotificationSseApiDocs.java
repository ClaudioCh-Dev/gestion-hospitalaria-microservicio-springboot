package personal.notification_ms.docs;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Documentación OpenAPI de NotificationSseController.
 */
@Tag(name = "Notificaciones en tiempo real", description = "Stream Server-Sent Events con las notificaciones nuevas")
public interface NotificationSseApiDocs {

    @Operation(summary = "Suscribirse al stream de notificaciones",
            description = "Abre una conexión SSE sin timeout. Cada notificación nueva llega como un evento llamado 'notification'. "
                    + "Swagger/Scalar no muestran bien el streaming: pruébalo con EventSource en el navegador o con curl -N.")
    @ApiResponse(responseCode = "200", description = "Conexión SSE abierta",
            content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                    examples = @ExampleObject(value = NotificationExamples.SSE_EVENT)))
    SseEmitter stream();
}
