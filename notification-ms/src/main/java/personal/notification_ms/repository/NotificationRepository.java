package personal.notification_ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.notification_ms.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // La campana solo muestra las últimas 50
    List<Notification> findTop50ByOrderByCreatedAtDesc();

    List<Notification> findTop50ByDoctorUserIdOrderByCreatedAtDesc(Long doctorUserId);

    // Primera notificación de la cita (la de creación): tiene paciente, médico, fecha...
    Optional<Notification> findFirstByReferenceTypeAndReferenceIdOrderByIdAsc(
            String referenceType,
            Long referenceId
    );
}
