package personal.notification_ms.service.impl;

import lombok.extern.slf4j.Slf4j;
import personal.notification_ms.service.ISseService;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class SseServiceImpl implements ISseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Override
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

    @Override
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