package personal.medical_record_listener.dto;

import java.math.BigDecimal;

public record MedicalRecordResponse(
        String id,
        Long appointmentId,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        String specialty,
        String scheduledAt,
        String reason,
        String status,
        BigDecimal amount
) {
}