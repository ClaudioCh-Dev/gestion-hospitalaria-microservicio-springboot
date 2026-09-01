package personal.doctor_ms.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import personal.doctor_ms.dtos.*;
import personal.doctor_ms.service.IDoctorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class DoctorController {

        private final IDoctorService doctorService;

        @GetMapping()
        @PreAuthorize("@auth.hasPermission('DOCTOR_READ')")
        public ResponseEntity<Page<DoctorResponse>> findAll(
                        Pageable pageable) {
                return ResponseEntity.ok(
                                doctorService.findAll(pageable));
        }

        @GetMapping("/{id}")
        @PreAuthorize("@auth.hasPermission('DOCTOR_READ')")
        public ResponseEntity<DoctorResponse> findById(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                doctorService.findById(id));
        }

        @GetMapping("/specialty/{specialtyId}")
        @PreAuthorize("@auth.hasPermission('DOCTOR_READ_BY_SPECIALTY')")
        public ResponseEntity<Page<DoctorResponse>> findBySpecialty(
                        @PathVariable Long specialtyId,
                        Pageable pageable) {
                return ResponseEntity.ok(
                                doctorService.findBySpecialty(specialtyId, pageable));
        }

        @PostMapping()
        @PreAuthorize("@auth.hasPermission('DOCTOR_CREATE')")
        public ResponseEntity<DoctorResponse> create(
                        @Valid @RequestBody CreateDoctorRequest request) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(doctorService.create(request));
        }

        @PutMapping("/{id}")
        @PreAuthorize("@auth.hasPermission('DOCTOR_UPDATE')")
        public ResponseEntity<DoctorResponse> update(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateDoctorRequest request) {
                return ResponseEntity.ok(
                                doctorService.update(id, request));
        }
}