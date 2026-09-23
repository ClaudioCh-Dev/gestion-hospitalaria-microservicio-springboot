package personal.medical_record_listener.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.medical_record_listener.service.IMedicalRecordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class MedicalRecordController {

    private final IMedicalRecordService service;

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("@auth.hasPermission('MEDICAL_RECORD_READ_BY_PATIENT')")
    public ResponseEntity<Page<MedicalRecordResponse>> findByPatientId(
            @PathVariable Long patientId,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.findByPatientId(patientId, pageable)
        );
    }

    @GetMapping
    @PreAuthorize("@auth.hasPermission('MEDICAL_RECORD_READ')")
    public ResponseEntity<Page<MedicalRecordResponse>> findAll(
            Pageable pageable) {

        return ResponseEntity.ok(
                service.findAll(pageable)
        );
    }
}