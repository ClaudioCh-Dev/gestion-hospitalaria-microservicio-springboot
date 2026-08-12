package personal.notification_ms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long appointmentId;

    private Long patientId;

    private String patientName;

    private Long doctorId;

    private String doctorName;

    private String specialty;

    private String eventType;

    private String status;

    private String reason;

    private LocalDateTime scheduledAt;

    private BigDecimal amount;

    private boolean doctorRead;
    private boolean adminRead;

    private LocalDateTime createdAt;
}