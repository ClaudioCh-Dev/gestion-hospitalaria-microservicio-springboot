package personal.notification_ms.mapper;

import org.mapstruct.Mapper;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.model.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toEntity(NotificationRequest request);

    NotificationResponse toResponse(Notification notification);

}