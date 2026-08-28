package personal.shared.event;

import java.math.BigDecimal;

public record AppointmentCreatedEvent(
    Long   appointmentId,
    Long   patientId,
    String patientName,
    Long   doctorId,
    String doctorName,
    String specialty,
    String scheduledAt,   // ISO 8601
    String reason,
    EnumStatusAppointment status,        // SCHEDULED | COMPLETED | CANCELLED
    BigDecimal amount,
    String currency     // appointment-created | appointment-cancelled | appointment-completed
) {}