package personal.notification_ms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.exceptions.NotificationErrorCode;
import personal.notification_ms.mapper.NotificationMapper;
import personal.notification_ms.model.Notification;
import personal.notification_ms.model.NotificationRecipient;
import personal.notification_ms.repository.NotificationRecipientRepository;
import personal.notification_ms.repository.NotificationRepository;
import personal.notification_ms.security.UserContext;
import personal.notification_ms.security.UserContextHolder;
import personal.notification_ms.service.INotificationService;
import personal.shared.exception.BusinessException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private static final String APPOINTMENT_REFERENCE = "APPOINTMENT";

    private final NotificationRepository repository;
    private final NotificationRecipientRepository recipientRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse save(NotificationRequest request) {

        Notification notification = notificationMapper.toEntity(request);

        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = repository.save(notification);

        return notificationMapper.toResponse(saved, false);
    }

    @Override
    public List<NotificationResponse> findMine() {

        Long userId = currentUserId();

        if (userId == null) {
            return List.of();
        }

        return withReadState(
                repository.findTop50ByDoctorUserIdOrderByCreatedAtDesc(userId),
                userId
        );
    }

    @Override
    public List<NotificationResponse> findForAdmin() {

        return withReadState(
                repository.findTop50ByOrderByCreatedAtDesc(),
                currentUserId()
        );
    }

    @Override
    public void markAsRead(Long notificationId) {

        if (!repository.existsById(notificationId)) {
            throw new BusinessException(
                    NotificationErrorCode.NOTIFICATION_NOT_FOUND,
                    "Notificación no encontrada");
        }

        Long userId = currentUserId();

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

    @Override
    public Map<String, Object> findAppointmentMetadata(Long appointmentId) {

        Map<String, Object> metadata = new HashMap<>();

        repository.findFirstByReferenceTypeAndReferenceIdOrderByIdAsc(APPOINTMENT_REFERENCE, appointmentId)
                .ifPresent(origin -> {
                    putIfPresent(metadata, "doctorId", origin.getDoctorId());
                    putIfPresent(metadata, "doctorUserId", origin.getDoctorUserId());
                    putIfPresent(metadata, "patientName", origin.getPatientName());
                    putIfPresent(metadata, "doctorName", origin.getDoctorName());
                    putIfPresent(metadata, "specialty", origin.getSpecialty());
                    putIfPresent(metadata, "reason", origin.getReason());
                    putIfPresent(metadata, "scheduledAt",
                            origin.getScheduledAt() != null ? origin.getScheduledAt().toString() : null);
                });

        return metadata;
    }

    // La lectura es por usuario: una fila en notification_recipients por (notificación, userId)
    private List<NotificationResponse> withReadState(List<Notification> notifications, Long userId) {

        if (notifications.isEmpty()) {
            return List.of();
        }

        Set<Long> readIds = userId == null
                ? Set.of()
                : recipientRepository.findReadNotificationIds(
                        userId,
                        notifications.stream().map(Notification::getId).toList());

        return notifications.stream()
                .map(notification -> notificationMapper.toResponse(
                        notification,
                        readIds.contains(notification.getId())))
                .toList();
    }

    private Long currentUserId() {
        UserContext context = UserContextHolder.get();
        return context != null ? context.userId() : null;
    }

    private static void putIfPresent(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
