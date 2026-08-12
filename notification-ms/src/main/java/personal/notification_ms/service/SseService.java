package personal.notification_ms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {

        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.info("SSE connection completed");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.info("SSE connection timeout");
            emitter.complete();
            emitters.remove(emitter);
        });

        emitter.onError(error -> {
            log.warn("SSE connection error");
            emitters.remove(emitter);
        });

        return emitter;
    }

    public void sendNotification(Object event) {

        for (SseEmitter emitter : emitters) {

            try {
                emitter.send(
                        SseEmitter.event()
                                .name("notification")
                                .data(event)
                );

            } catch (IOException e) {

                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}