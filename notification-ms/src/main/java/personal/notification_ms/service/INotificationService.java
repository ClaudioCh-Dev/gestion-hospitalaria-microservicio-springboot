package personal.notification_ms.service;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;

import java.util.List;
import java.util.Map;

public interface INotificationService {

    NotificationResponse save(NotificationRequest request);

    // Notificaciones de las citas del médico autenticado (doctorUserId = userId del token)
    List<NotificationResponse> findMine();

    List<NotificationResponse> findForAdmin();

    void markAsRead(Long notificationId);

    /**
     * Datos de la cita guardados con su primera notificación (paciente, médico, fecha...).
     * Los eventos de cambio de estado solo traen el estado: con esto se completan.
     * Vacío si la cita no tiene notificaciones previas.
     */
    Map<String, Object> findAppointmentMetadata(Long appointmentId);
}
