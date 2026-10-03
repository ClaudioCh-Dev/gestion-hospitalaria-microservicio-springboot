package personal.notification_ms.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_recipients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notificationId;

    private Long userId;

    // READ es palabra reservada en MySQL: sin comillas el INSERT falla
    @Column(name = "`read`")
    private boolean read;

    private LocalDateTime readAt;
}