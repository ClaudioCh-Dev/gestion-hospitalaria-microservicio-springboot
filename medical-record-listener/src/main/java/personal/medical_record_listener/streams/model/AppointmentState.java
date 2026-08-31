package personal.medical_record_listener.streams.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import personal.shared.event.status.StatusAppointment;

public record AppointmentState(

        Long appointmentId,

        String appointmentType,

        Long patientId,
        String patientName,

        Long doctorId,
        String doctorName,

        String specialty,

        LocalDateTime scheduledAt,

        String reason,

        StatusAppointment status,

        BigDecimal amount,

        String currency

) {

    public AppointmentState withStatus(
            StatusAppointment newStatus) {

        return new AppointmentState(
                appointmentId,
                appointmentType,
                patientId,
                patientName,
                doctorId,
                doctorName,
                specialty,
                scheduledAt,
                reason,
                newStatus,
                amount,
                currency
        );
    }
}