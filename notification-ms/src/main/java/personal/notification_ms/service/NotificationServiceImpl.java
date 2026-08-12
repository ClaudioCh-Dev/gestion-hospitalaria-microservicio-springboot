package personal.notification_ms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.exceptions.NotificationNotFoundException;
import personal.notification_ms.model.Notification;
import personal.notification_ms.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

// TODO: Add scheduled notification cleanup and retention policy
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository repository;

    @Override
    public NotificationResponse save(NotificationRequest request) {

        Notification notification = Notification.builder()
                .appointmentId(request.appointmentId())
                .patientId(request.patientId())
                .patientName(request.patientName())
                .doctorId(request.doctorId())
                .doctorName(request.doctorName())
                .specialty(request.specialty())
                .eventType(request.eventType())
                .status(request.status())
                .reason(request.reason())
                .scheduledAt(request.scheduledAt())
                .amount(request.amount())
                .doctorRead(false)
                .adminRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = repository.save(notification);

        return toResponse(saved);
    }

    @Override
    public List<NotificationResponse> findByDoctorId(Long doctorId) {

        return repository
                .findByDoctorIdOrderByCreatedAtDesc(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> findForAdmin() {

        return repository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void markAsReadByDoctor(Long notificationId) {

        Notification notification = findNotification(notificationId);

        notification.setDoctorRead(true);

        repository.save(notification);
    }

    @Override
    public void markAsReadByAdmin(Long notificationId) {

        Notification notification = findNotification(notificationId);

        notification.setAdminRead(true);

        repository.save(notification);
    }

    private Notification findNotification(Long notificationId) {

        return repository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(notificationId)
                );
    }

    private NotificationResponse toResponse(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getAppointmentId(),
                notification.getPatientId(),
                notification.getPatientName(),
                notification.getDoctorId(),
                notification.getDoctorName(),
                notification.getSpecialty(),
                notification.getEventType(),
                notification.getStatus(),
                notification.getReason(),
                notification.getScheduledAt(),
                notification.getAmount(),
                notification.isDoctorRead(),
                notification.isAdminRead(),
                notification.getCreatedAt()
        );
    }
}