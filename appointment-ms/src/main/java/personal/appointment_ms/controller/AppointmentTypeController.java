package personal.appointment_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

import personal.appointment_ms.dto.AppointmentTypeResponse;
import personal.appointment_ms.dto.CreateAppointmentTypeRequest;
import personal.appointment_ms.dto.UpdateAppointmentTypeRequest;
import personal.appointment_ms.service.IAppointmentTypeService;

@RestController
@RequestMapping("/appointment-types")
@RequiredArgsConstructor
public class AppointmentTypeController {

        private final IAppointmentTypeService appointmentTypeService;

        @PostMapping
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_TYPE_CREATE')")
        public ResponseEntity<AppointmentTypeResponse> create(
                        @RequestBody CreateAppointmentTypeRequest request) {

                AppointmentTypeResponse response = appointmentTypeService.create(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_TYPE_READ')")
        public ResponseEntity<List<AppointmentTypeResponse>> findAll() {

                return ResponseEntity.ok(
                                appointmentTypeService.findAll());
        }

        @GetMapping("/{id}")
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_TYPE_READ')")
        public ResponseEntity<AppointmentTypeResponse> findById(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                appointmentTypeService.findById(id));
        }

        @PutMapping("/{id}")
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_TYPE_UPDATE')")
        public ResponseEntity<AppointmentTypeResponse> update(
                        @PathVariable Long id,
                        @RequestBody UpdateAppointmentTypeRequest request) {

                return ResponseEntity.ok(
                                appointmentTypeService.update(id, request));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("@auth.hasPermission('APPOINTMENT_TYPE_DELETE')")
        public ResponseEntity<Void> deactivate(
                        @PathVariable Long id) {

                appointmentTypeService.deactivate(id);

                return ResponseEntity.noContent().build();
        }
}