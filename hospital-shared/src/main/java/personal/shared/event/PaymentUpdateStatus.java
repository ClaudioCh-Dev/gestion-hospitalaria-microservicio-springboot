package personal.shared.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentUpdateStatus(
    Long billingId,
    Long appointmentId,
    BigDecimal amount,
    String currency,
    PaymentStatus status,
    LocalDateTime issuedAt,
    LocalDateTime paidAt
) {

}