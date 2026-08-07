package com.personal.controller;

import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import com.personal.service.IPatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final IPatientService patientService;

    public PatientController(IPatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponse>> findAll(Pageable pageable) {
        Page<PatientResponse> patients = patientService.findAll(pageable);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> findById(@PathVariable Long id) {
        PatientResponse patient = patientService.findById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/document/{documentNumber}")
    public ResponseEntity<PatientResponse> findByDocumentNumber(@PathVariable String documentNumber) {
        PatientResponse patient = patientService.findByDocumentNumber(documentNumber);
        return ResponseEntity.ok(patient);
    }

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest request) {
        PatientResponse createdPatient = patientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> update(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        PatientResponse updatedPatient = patientService.update(id, request);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')") // Restringe el acceso solo a usuarios con rol ADMIN
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id); // Internamente en el Service se realiza el soft delete
        return ResponseEntity.noContent().build();
    }
}