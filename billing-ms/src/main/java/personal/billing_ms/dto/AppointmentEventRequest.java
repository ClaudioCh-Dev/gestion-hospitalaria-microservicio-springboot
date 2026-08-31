package personal.billing_ms.dto;

import java.math.BigDecimal;

public record AppointmentEventRequest(
    Long appointmentId,
    Long patientId,
    BigDecimal amount
) {
}
