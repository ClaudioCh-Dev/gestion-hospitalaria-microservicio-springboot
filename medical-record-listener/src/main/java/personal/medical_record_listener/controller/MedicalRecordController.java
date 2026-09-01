package personal.medical_record_listener.controller;

import java.util.List;

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
    public ResponseEntity<List<MedicalRecordResponse>> findByPatientId(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                service.findByPatientId(patientId));
    }

    @GetMapping
    @PreAuthorize("@auth.hasPermission('MEDICAL_RECORD_READ')")
    public ResponseEntity<List<MedicalRecordResponse>> findAll() {
        return ResponseEntity.ok(
                service.findAll());
    }
}