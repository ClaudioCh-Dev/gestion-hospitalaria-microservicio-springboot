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
        public ResponseEntity<List<DoctorNotificationResponse>> findByDoctorId(
                        @PathVariable Long doctorId) {
                return ResponseEntity.ok(
                                service.findByDoctorId(doctorId));
        }

        @PreAuthorize("@auth.hasPermission('NOTIFICATION_READ_ADMIN')")
        @GetMapping("/admin")
        public ResponseEntity<List<AdminNotificationResponse>> findForAdmin() {
                return ResponseEntity.ok(
                                service.findForAdmin());
        }

        @PreAuthorize("@auth.hasPermission('NOTIFICATION_MARK_READ_DOCTOR')")
        @PatchMapping("/{notificationId}/read/doctor")
        public ResponseEntity<Void> markAsReadByDoctor(
                        @PathVariable Long notificationId) {
                service.markAsReadByDoctor(notificationId);
                return ResponseEntity.noContent().build();
        }

        @PreAuthorize("@auth.hasPermission('NOTIFICATION_MARK_READ_ADMIN')")
        @PatchMapping("/{notificationId}/read/admin")
        public ResponseEntity<Void> markAsReadByAdmin(
                        @PathVariable Long notificationId) {
                service.markAsReadByAdmin(notificationId);
                return ResponseEntity.noContent().build();
        }
}