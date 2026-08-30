package personal.shared.event;

import java.math.BigDecimal;

public record AppointmentEventRequest(
    Long appointmentId,
    Long patientId,
    BigDecimal amount
) {
}
