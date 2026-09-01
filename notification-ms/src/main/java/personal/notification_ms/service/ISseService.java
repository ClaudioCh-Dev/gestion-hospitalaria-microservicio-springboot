package personal.notification_ms.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ISseService {
    
    SseEmitter subscribe();
    
    void sendNotification(Object event);
}

