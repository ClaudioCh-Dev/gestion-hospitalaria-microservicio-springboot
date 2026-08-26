package personal.notification_ms.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import personal.notification_ms.dto.AdminNotificationResponse;
import personal.notification_ms.dto.DoctorNotificationResponse;
import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.service.INotificationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class NotificationController {

        private final INotificationService service;

        @PreAuthorize("@auth.hasPermission('NOTIFICATION_CREATE')")
        @PostMapping
        public ResponseEntity<NotificationResponse> save(
                        @RequestBody NotificationRequest request) {

                return ResponseEntity.ok(
                                service.save(request));
        }

        @PreAuthorize("@auth.hasPermission('NOTIFICATION_READ_DOCTOR')")
        @GetMapping("/doctor/{doctorId}")
        public ResponseEntity<List<DoctorNotificationResponse>> findMyDoctorNotifications(
                        @PathVariable Long doctorId) {

                return ResponseEntity.ok(
                                service.findMyDoctorNotifications(doctorId));
        }

        @PreAuthorize("@auth.hasPermission('NOTIFICATION_READ_ADMIN')")
        @GetMapping("/admin")
        public ResponseEntity<List<AdminNotificationResponse>> findForAdmin() {

                return ResponseEntity.ok(
                                service.findForAdmin());
        }

        @PreAuthorize("@auth.hasPermission('NOTIFICATION_MARK_READ_DOCTOR')")
        @PatchMapping("/{notificationId}/read")
        public ResponseEntity<Void> markAsRead(
                        @PathVariable Long notificationId) {

                service.markAsRead(notificationId);

                return ResponseEntity.noContent().build();
        }
}