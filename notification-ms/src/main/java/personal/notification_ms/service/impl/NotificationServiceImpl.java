package personal.notification_ms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.mapper.NotificationMapper;
import personal.notification_ms.model.Notification;
import personal.notification_ms.model.NotificationRecipient;
import personal.notification_ms.repository.NotificationRecipientRepository;
import personal.notification_ms.repository.NotificationRepository;
import personal.notification_ms.security.UserContextHolder;
import personal.notification_ms.service.INotificationService;

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
    public List<NotificationResponse> findMyDoctorNotifications(Long doctorId) {
        return repository.findDoctorNotifications(doctorId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> findForAdmin() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
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