package personal.medical_record_listener.streams.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import personal.shared.event.EnumStatusAppointment;

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

        EnumStatusAppointment status,

        BigDecimal amount,

        String currency

) {

    public AppointmentState withStatus(
            EnumStatusAppointment newStatus) {

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