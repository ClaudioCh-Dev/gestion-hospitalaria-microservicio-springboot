package personal.notification_ms.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ISseService {
    
    SseEmitter subscribe();
    
    /**
     * Envía el evento a los admins conectados (NOTIFICATION_READ_ADMIN) y a las conexiones
     * del usuario indicado (el médico de la cita). Si doctorUserId es null, solo a los admins.
     */
    void sendNotification(Long doctorUserId, Object event);
}
