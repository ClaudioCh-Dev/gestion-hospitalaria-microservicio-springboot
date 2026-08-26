package personal.notification_ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.notification_ms.model.NotificationRecipient;

import java.util.Optional;

public interface NotificationRecipientRepository
        extends JpaRepository<NotificationRecipient, Long> {

    Optional<NotificationRecipient> findByNotificationIdAndUserId(
            Long notificationId,
            Long userId
    );
}