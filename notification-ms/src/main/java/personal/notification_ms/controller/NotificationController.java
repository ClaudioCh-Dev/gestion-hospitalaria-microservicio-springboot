package personal.notification_ms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.service.INotificationService;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService service;

    @PostMapping
    public ResponseEntity<NotificationResponse> save(
            @RequestBody NotificationRequest request
    ) {

        return ResponseEntity.ok(
                service.save(request)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<NotificationResponse>> findByDoctorId(
            @PathVariable Long doctorId
    ) {

        return ResponseEntity.ok(
                service.findByDoctorId(doctorId)
        );
    }

    @GetMapping("/admin")
    public ResponseEntity<List<NotificationResponse>> findForAdmin() {

        return ResponseEntity.ok(
                service.findForAdmin()
        );
    }

    @PatchMapping("/{notificationId}/read/doctor")
    public ResponseEntity<Void> markAsReadByDoctor(
            @PathVariable Long notificationId
    ) {

        service.markAsReadByDoctor(notificationId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{notificationId}/read/admin")
    public ResponseEntity<Void> markAsReadByAdmin(
            @PathVariable Long notificationId
    ) {

        service.markAsReadByAdmin(notificationId);

        return ResponseEntity.noContent().build();
    }
}