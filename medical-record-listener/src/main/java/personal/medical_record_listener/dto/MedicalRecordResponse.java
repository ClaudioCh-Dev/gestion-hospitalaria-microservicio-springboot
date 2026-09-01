package personal.medical_record_listener.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MedicalRecordResponse(
        String id,
        Long appointmentId,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        String specialty,
        LocalDateTime scheduledAt,
        String reason,
        String status,
        BigDecimal amount
) {
}