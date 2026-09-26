package personal.shared.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import personal.shared.event.status.StatusAppointment;

public record AppointmentCreatedEvent(
        Long appointmentId,
        String appointmentType,
        Long patientId,
        String patientName,
        Long doctorId,
        // userId (auth-server) del médico: notification-ms le envía el evento solo a él (y a los admins)
        Long doctorUserId,
        String doctorName,
        String specialty,
        LocalDateTime scheduledAt,
        String reason,
        StatusAppointment status,
        BigDecimal amount,
        String currency) {
}