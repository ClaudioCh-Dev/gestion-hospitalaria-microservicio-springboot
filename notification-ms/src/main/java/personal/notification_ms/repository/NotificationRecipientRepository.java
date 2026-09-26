package personal.notification_ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import personal.notification_ms.model.NotificationRecipient;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface NotificationRecipientRepository
        extends JpaRepository<NotificationRecipient, Long> {

    Optional<NotificationRecipient> findByNotificationIdAndUserId(
            Long notificationId,
            Long userId
    );

    // Cuáles de estas notificaciones ya leyó el usuario
    @Query("""
        SELECT r.notificationId
        FROM NotificationRecipient r
        WHERE r.userId = :userId
          AND r.read = true
          AND r.notificationId IN :notificationIds
    """)
    Set<Long> findReadNotificationIds(
            @Param("userId") Long userId,
            @Param("notificationIds") Collection<Long> notificationIds
    );
}
