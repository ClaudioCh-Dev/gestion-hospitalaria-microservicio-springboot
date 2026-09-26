package personal.notification_ms.service.impl;

import lombok.extern.slf4j.Slf4j;
import personal.notification_ms.exceptions.NotificationErrorCode;
import personal.notification_ms.security.UserContext;
import personal.notification_ms.security.UserContextHolder;
import personal.notification_ms.service.ISseService;
import personal.shared.exception.BusinessException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseServiceImpl implements ISseService {

    private static final String ADMIN_PERMISSION = "NOTIFICATION_READ_ADMIN";

    // Conexiones por usuario (un usuario puede tener varias pestañas abiertas)
    private final Map<Long, List<SseEmitter>> emittersByUser = new ConcurrentHashMap<>();

    // Conexiones de admins → userId dueño, para poder quitarlas de ambos lados
    private final Map<SseEmitter, Long> adminEmitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe() {

        // X-User-Id y X-Permissions que añade el gateway
        UserContext context = UserContextHolder.get();

        if (context == null || context.userId() == null) {
            throw new BusinessException(
                    NotificationErrorCode.SSE_USER_REQUIRED,
                    "No se pudo identificar al usuario de la conexión SSE");
        }

        Long userId = context.userId();
        SseEmitter emitter = new SseEmitter(0L);

        emittersByUser.compute(userId, (id, list) -> {
            List<SseEmitter> emitters = list != null ? list : new CopyOnWriteArrayList<>();
            emitters.add(emitter);
            return emitters;
        });

        if (context.hasPermission(ADMIN_PERMISSION)) {
            adminEmitters.put(emitter, userId);
        }

        emitter.onCompletion(() -> {
            log.info("SSE connection completed. userId={}", userId);
            remove(userId, emitter);
        });

        emitter.onTimeout(() -> {
            log.info("SSE connection timeout. userId={}", userId);
            emitter.complete();
            remove(userId, emitter);
        });

        emitter.onError(error -> {
            log.warn("SSE connection error. userId={}", userId);
            remove(userId, emitter);
        });

        return emitter;
    }

    @Override
    public void sendNotification(Long doctorUserId, Object event) {

        // emitter → userId; el Map evita enviar dos veces si el médico también es admin
        Map<SseEmitter, Long> recipients = new LinkedHashMap<>(adminEmitters);

        if (doctorUserId != null) {
            emittersByUser.getOrDefault(doctorUserId, List.of())
                    .forEach(emitter -> recipients.put(emitter, doctorUserId));
        }

        recipients.forEach((emitter, userId) -> send(
                userId,
                emitter,
                SseEmitter.event()
                        .name("notification")
                        .data(event)
        ));
    }

    /**
     * Proxies y balanceadores cortan las conexiones inactivas (30-60 s). Un comentario SSE
     * (": ping") mantiene viva la conexión sin disparar onmessage en el cliente y, si el envío
     * falla, detecta clientes caídos y los quita.
     */
    @Scheduled(fixedRate = 25_000)
    public void heartbeat() {

        // Todas las conexiones están en emittersByUser (también las de admins)
        emittersByUser.forEach((userId, emitters) -> emitters.forEach(emitter -> send(
                userId,
                emitter,
                SseEmitter.event().comment("ping")
        )));
    }

    private void send(Long userId, SseEmitter emitter, SseEmitter.SseEventBuilder event) {

        try {
            emitter.send(event);

        } catch (IOException | IllegalStateException e) {

            // IllegalStateException: el emitter ya se había completado
            emitter.completeWithError(e);
            remove(userId, emitter);
        }
    }

    private void remove(Long userId, SseEmitter emitter) {

        // Si la lista queda vacía se elimina la entrada del usuario
        emittersByUser.computeIfPresent(userId, (id, emitters) -> {
            emitters.remove(emitter);
            return emitters.isEmpty() ? null : emitters;
        });

        adminEmitters.remove(emitter);
    }
}
