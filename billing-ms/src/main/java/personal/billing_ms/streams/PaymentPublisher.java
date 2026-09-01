package personal.billing_ms.streams;

import java.util.Set;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import personal.billing_ms.security.UserContext;
import personal.billing_ms.security.UserContextHolder;
import personal.shared.event.PaymentUpdateStatus;

import org.springframework.messaging.Message;

@Component
@AllArgsConstructor
public class PaymentPublisher {

    private final StreamBridge streamBridge;

    public void publishPaymentUpdateStatus(
            PaymentUpdateStatus paymentEvent) {

        streamBridge.send(
                "paymentUpdateStatusProducer-out-0",
                buildMessage(paymentEvent));
    }

    private <T> Message<T> buildMessage(T event) {

        UserContext context = UserContextHolder.get();

        MessageBuilder<T> builder = MessageBuilder.withPayload(event);

        if (context != null) {

            if (context.userId() != null) {
                builder.setHeader(
                        "X-User-Id",
                        context.userId().toString());
            }

            if (context.role() != null) {
                builder.setHeader(
                        "X-Role",
                        context.role());
            }

            Set<String> permissions = context.permissions();

            if (permissions != null && !permissions.isEmpty()) {
                builder.setHeader(
                        "X-Permissions",
                        String.join(",", permissions));
            }
        }

        return builder.build();
    }
}