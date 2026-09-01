package personal.appointment_ms.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

import personal.appointment_ms.dto.AppointmentResponse;
import personal.appointment_ms.dto.CreateAppointmentRequest;
import personal.appointment_ms.dto.UpdateAppointmentStatusRequest;
import personal.appointment_ms.service.IAppointmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class AppointmentController {

        private final IAppointmentService appointmentService;

        @PostMapping
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_CREATE')")
        public ResponseEntity<AppointmentResponse> createAppointment(
                        @Valid @RequestBody CreateAppointmentRequest request) {

                AppointmentResponse response = appointmentService.createAppointment(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_READ')")
        public ResponseEntity<Page<AppointmentResponse>> getAppointments(
                        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

                return ResponseEntity.ok(
                                appointmentService.getAppointments(pageable));
        }

        @GetMapping("/{id}")
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_READ')")
        public ResponseEntity<AppointmentResponse> getAppointmentById(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                appointmentService.getAppointmentById(id));
        }

        @GetMapping("/patient/{patientId}")
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_READ_BY_PATIENT')")
        public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(
                        @PathVariable Long patientId) {

                return ResponseEntity.ok(
                                appointmentService.getAppointmentsByPatient(patientId));
        }

        @GetMapping("/doctor/{doctorId}")
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_READ_BY_DOCTOR')")
        public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(
                        @PathVariable Long doctorId) {

                return ResponseEntity.ok(
                                appointmentService.getAppointmentsByDoctor(doctorId));
        }

        @PatchMapping("/{id}/status")
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_UPDATE_STATUS')")
        public ResponseEntity<AppointmentResponse> updateStatus(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateAppointmentStatusRequest request) {

                return ResponseEntity.ok(
                                appointmentService.updateStatus(id, request));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_CANCEL')")
        public ResponseEntity<Void> cancelAppointment(
                        @PathVariable Long id) {

                appointmentService.cancelAppointment(id);

                return ResponseEntity.noContent().build();
        }
}