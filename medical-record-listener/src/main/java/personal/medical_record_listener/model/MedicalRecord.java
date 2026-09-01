package personal.medical_record_listener.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medical_records")
public class MedicalRecord {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long appointmentId;
    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;
    private String specialty;

    private LocalDateTime scheduledAt;
    private String reason;

    private String status;
    private BigDecimal amount;
}