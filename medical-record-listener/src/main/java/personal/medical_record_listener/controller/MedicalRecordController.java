package personal.medical_record_listener.controller;

import personal.medical_record_listener.docs.MedicalRecordApiDocs;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.medical_record_listener.dto.MedicalRecordSummaryResponse;
import personal.medical_record_listener.service.IMedicalRecordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class MedicalRecordController implements MedicalRecordApiDocs {

    private final IMedicalRecordService service;

    @Override
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("@auth.hasPermission('MEDICAL_RECORD_READ_BY_PATIENT')")
    public ResponseEntity<Page<MedicalRecordResponse>> findByPatientId(
            @PathVariable Long patientId,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(
                service.findByPatientId(patientId, pageable)
        );
    }

    @Override
    @GetMapping
    @PreAuthorize("@auth.hasPermission('MEDICAL_RECORD_READ')")
    public ResponseEntity<Page<MedicalRecordResponse>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialty,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(
                service.findAll(pageable, search, specialty)
        );
    }

    @Override
    @GetMapping("/summary")
    @PreAuthorize("@auth.hasPermission('MEDICAL_RECORD_READ')")
    public ResponseEntity<MedicalRecordSummaryResponse> getSummary() {

        return ResponseEntity.ok(
                service.getSummary()
        );
    }
}