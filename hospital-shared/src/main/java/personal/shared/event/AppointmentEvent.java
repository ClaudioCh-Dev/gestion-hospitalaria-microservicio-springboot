package personal.shared.event;

import java.math.BigDecimal;

public record AppointmentEvent(
    Long   appointmentId,
    Long   patientId,
    String patientName,
    Long   doctorId,
    String doctorName,
    String specialty,
    String scheduledAt,   // ISO 8601
    String reason,
    String status,        // SCHEDULED | COMPLETED | CANCELLED
    BigDecimal amount,
    String eventType      // appointment-created | appointment-cancelled | appointment-completed
) {}