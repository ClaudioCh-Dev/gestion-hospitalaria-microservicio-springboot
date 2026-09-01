package personal.shared.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import personal.shared.event.status.StatusPayment;

public record PaymentUpdateStatus(
    Long billingId,
    Long appointmentId,
    BigDecimal amount,
    String currency,
    StatusPayment status,
    LocalDateTime issuedAt,
    LocalDateTime paidAt
) {

}