package personal.notification_ms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import personal.notification_ms.service.SseService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationSseController {

    private final SseService sseService;

    @GetMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter stream() {
        return sseService.subscribe();
    }
}