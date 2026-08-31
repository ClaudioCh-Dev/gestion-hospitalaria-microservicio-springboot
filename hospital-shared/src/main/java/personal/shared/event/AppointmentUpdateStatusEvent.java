package personal.shared.event;

import java.time.LocalDateTime;

public record AppointmentUpdateStatusEvent(
    Long   appointmentId,
    String appointmentType,
    EnumStatusAppointment status,
    String patientName,
    String doctorName,
    Long patientId,
    Long doctorId,
    String specialty,
    LocalDateTime scheduledAt,
    String reason
) {}