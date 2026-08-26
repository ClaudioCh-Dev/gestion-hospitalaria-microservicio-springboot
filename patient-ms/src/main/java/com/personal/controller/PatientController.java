package com.personal.controller;

import com.personal.dto.PatientDetailResponse;
import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import com.personal.service.IPatientService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crud")
public class PatientController {

    private final IPatientService patientService;

    public PatientController(IPatientService patientService) {
        this.patientService = patientService;
    }

    @PreAuthorize("@auth.hasPermission('PATIENT_READ')")
    @GetMapping
    public ResponseEntity<Page<PatientResponse>> findAll(Pageable pageable) {

        Page<PatientResponse> patients = patientService.findAll(pageable);

        return ResponseEntity.ok(patients);
    }

    @PreAuthorize("@auth.hasPermission('PATIENT_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<PatientDetailResponse> findById(
            @PathVariable Long id) {

        PatientDetailResponse patient = patientService.findById(id);

        return ResponseEntity.ok(patient);
    }

    @PreAuthorize("@auth.hasPermission('PATIENT_READ')")
    @GetMapping("/document/{documentNumber}")
    public ResponseEntity<PatientResponse> findByDocumentNumber(
            @PathVariable String documentNumber) {

        PatientResponse patient = patientService.findByDocumentNumber(documentNumber);

        return ResponseEntity.ok(patient);
    }

    @PreAuthorize("@auth.hasPermission('PATIENT_CREATE')")
    @PostMapping
    public ResponseEntity<PatientResponse> create(
            @Valid @RequestBody PatientRequest request) {

        PatientResponse createdPatient = patientService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdPatient);
    }

    @PreAuthorize("@auth.hasPermission('PATIENT_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {

        PatientResponse updatedPatient = patientService.update(id, request);

        return ResponseEntity.ok(updatedPatient);
    }

    @PreAuthorize("@auth.hasPermission('PATIENT_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        patientService.delete(id);

        return ResponseEntity.noContent().build();
    }
}