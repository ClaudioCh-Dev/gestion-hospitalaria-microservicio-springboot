package personal.notification_ms.controller;

import personal.notification_ms.docs.NotificationApiDocs;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.service.INotificationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class NotificationController implements NotificationApiDocs {

    private final INotificationService service;

    @Override
    @PreAuthorize("@auth.hasPermission('NOTIFICATION_CREATE')")
    @PostMapping
    public ResponseEntity<NotificationResponse> save(
            @RequestBody NotificationRequest request) {

        return ResponseEntity.ok(
                service.save(request)
        );
    }

    @Override
    @PreAuthorize("@auth.hasPermission('NOTIFICATION_READ_DOCTOR')")
    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> findMine() {

        // El médico sale del token (X-User-Id): no se puede pedir las de otro
        return ResponseEntity.ok(
                service.findMine()
        );
    }

    @Override
    @PreAuthorize("@auth.hasPermission('NOTIFICATION_READ_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<NotificationResponse>> findForAdmin() {

        return ResponseEntity.ok(
                service.findForAdmin()
        );
    }

    @Override
    // La lectura se guarda por userId (el del token): el permiso solo decide quién puede llamar
    @PreAuthorize("@auth.hasPermission('NOTIFICATION_MARK_READ_DOCTOR') or @auth.hasPermission('NOTIFICATION_MARK_READ_ADMIN')")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId) {

        service.markAsRead(notificationId);

        return ResponseEntity.noContent().build();
    }
}