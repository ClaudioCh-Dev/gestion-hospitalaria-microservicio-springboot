package personal.notification_ms.service;

import personal.notification_ms.dto.AdminNotificationResponse;
import personal.notification_ms.dto.DoctorNotificationResponse;
import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;

import java.util.List;

public interface INotificationService {

    NotificationResponse save(NotificationRequest request);

    List<DoctorNotificationResponse> findByDoctorId(Long doctorId);

    List<AdminNotificationResponse> findForAdmin();

    void markAsReadByDoctor(Long notificationId);

    void markAsReadByAdmin(Long notificationId);
}