package personal.notification_ms.exceptions;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(Long notificationId) {
        super("Notification not found with id: " + notificationId);
    }
}