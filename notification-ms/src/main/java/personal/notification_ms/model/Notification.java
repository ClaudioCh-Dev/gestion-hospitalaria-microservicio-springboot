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

    private String type;        // APPOINTMENT_CREATED, PAYMENT_FAILED, etc.
    private String title;
    private String message;

    private String referenceType; // APPOINTMENT, PAYMENT, PATIENT, etc.
    private Long referenceId;     // ID del recurso relacionado

    private LocalDateTime createdAt;
}