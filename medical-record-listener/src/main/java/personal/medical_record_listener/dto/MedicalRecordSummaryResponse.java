package personal.medical_record_listener.dto;

import java.math.BigDecimal;
import java.util.List;

// Totales del historial calculados sobre toda la colección
public record MedicalRecordSummaryResponse(
        long totalRecords,
        long uniquePatients,
        long completedRecords,
        BigDecimal totalAmount,
        List<String> specialties
) {
}
