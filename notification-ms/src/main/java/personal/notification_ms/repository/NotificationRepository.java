package personal.notification_ms.repository;

import personal.notification_ms.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);

    List<Notification> findAllByOrderByCreatedAtDesc();
}