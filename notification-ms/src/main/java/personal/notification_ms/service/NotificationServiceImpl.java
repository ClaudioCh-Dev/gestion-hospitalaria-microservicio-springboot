package personal.notification_ms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal.notification_ms.dto.AdminNotificationResponse;
import personal.notification_ms.dto.DoctorNotificationResponse;
import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.mapper.NotificationMapper;
import personal.notification_ms.model.Notification;
import personal.notification_ms.model.NotificationRecipient;
import personal.notification_ms.repository.NotificationRecipientRepository;
import personal.notification_ms.repository.NotificationRepository;
import personal.notification_ms.security.UserContextHolder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository repository;
    private final NotificationRecipientRepository recipientRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse save(NotificationRequest request) {

        Notification notification = notificationMapper.toEntity(request);

        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = repository.save(notification);

        return notificationMapper.toResponse(saved);
    }

    @Override
    public List<DoctorNotificationResponse> findMyDoctorNotifications(Long doctorId) {
        return repository.findDoctorNotifications(doctorId);
    }

    @Override
    public List<AdminNotificationResponse> findForAdmin() {
        return repository.findAdminNotifications();
    }

    @Override
    public void markAsRead(Long notificationId) {

        Long userId = UserContextHolder.get().userId();

        NotificationRecipient recipient = recipientRepository
                .findByNotificationIdAndUserId(notificationId, userId)
                .orElseGet(() -> NotificationRecipient.builder()
                        .notificationId(notificationId)
                        .userId(userId)
                        .build());

        recipient.setRead(true);
        recipient.setReadAt(LocalDateTime.now());

        recipientRepository.save(recipient);
    }
}