package personal.notification_ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import personal.notification_ms.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByCreatedAtDesc();

    @Query("""
        SELECT n
        FROM Notification n
        JOIN NotificationRecipient r
            ON r.notificationId = n.id
        WHERE r.userId = :doctorId
        ORDER BY n.createdAt DESC
    """)
    List<Notification> findDoctorNotifications(
            @Param("doctorId") Long doctorId
    );
}