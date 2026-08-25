package personal.notification_ms.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import personal.notification_ms.dto.AdminNotificationResponse;
import personal.notification_ms.dto.DoctorNotificationResponse;
import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.mapper.NotificationMapper;
import personal.notification_ms.model.Notification;
import personal.notification_ms.repository.NotificationRepository;

import personal.shared.exception.BusinessException;
import personal.notification_ms.exceptions.NotificationErrorCode;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse save(NotificationRequest request) {

        Notification notification = notificationMapper.toEntity(request);

        notification.setDoctorRead(false);
        notification.setAdminRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = repository.save(notification);

        return notificationMapper.toResponse(saved);
    }

    @Override
    public List<DoctorNotificationResponse> findByDoctorId(Long doctorId) {

        return repository.findDoctorNotifications(doctorId);
    }

    @Override
    public List<AdminNotificationResponse> findForAdmin() {

        return repository.findAdminNotifications();
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
                .orElseThrow(() -> new BusinessException(
                        NotificationErrorCode.NOTIFICATION_NOT_FOUND,
                        "Notificación no encontrada"
                ));
    }
}