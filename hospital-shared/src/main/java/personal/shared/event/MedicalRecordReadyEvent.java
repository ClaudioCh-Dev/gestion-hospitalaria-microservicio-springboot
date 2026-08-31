package personal.shared.event;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MedicalRecordReadyEvent(

        Long appointmentId,

        String appointmentType,

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