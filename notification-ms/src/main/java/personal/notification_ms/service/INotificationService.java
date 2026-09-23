package personal.notification_ms.service;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;

import java.util.List;

public interface INotificationService {

    NotificationResponse save(NotificationRequest request);

    List<NotificationResponse> findMyDoctorNotifications(Long doctorId);

    List<NotificationResponse> findForAdmin();

    void markAsRead(Long notificationId);
}