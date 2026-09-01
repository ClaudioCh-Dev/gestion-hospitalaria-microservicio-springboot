package personal.notification_ms.repository;

import personal.notification_ms.dto.AdminNotificationResponse;
import personal.notification_ms.dto.DoctorNotificationResponse;
import personal.notification_ms.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    List<Notification> findAllByOrderByCreatedAtDesc();

     @Query("""
        SELECT new personal.notification_ms.dto.DoctorNotificationResponse(
            n.id,
            n.patientName,
            n.eventType,
            n.status,
            n.reason,
            n.scheduledAt,
            n.doctorRead
        )
        FROM Notification n
        WHERE n.doctorId = :doctorId
        ORDER BY n.createdAt DESC
    """)
    List<DoctorNotificationResponse> findDoctorNotifications(
            @Param("doctorId") Long doctorId
    );

    @Query("""
        SELECT new personal.notification_ms.dto.AdminNotificationResponse(
            n.id,
            n.patientName,
            n.doctorName,
            n.specialty,
            n.eventType,
            n.status,
            n.reason,
            n.scheduledAt,
            n.adminRead
        )
        FROM Notification n
        ORDER BY n.createdAt DESC
    """)
    List<AdminNotificationResponse> findAdminNotifications();
}