package personal.notification_ms.controller;

import personal.notification_ms.docs.NotificationSseApiDocs;

import lombok.RequiredArgsConstructor;
import personal.notification_ms.service.ISseService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationSseController implements NotificationSseApiDocs {

    private final ISseService sseService;

    @Override
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseService.subscribe();
    }
}