package personal.medical_record_listener.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.medical_record_listener.service.MedicalRecordService;

import java.util.List;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService service;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> findByPatientId(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(
                service.findByPatientId(patientId)
        );
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> findAll() {
        return ResponseEntity.ok(
                service.findAll()
        );
    }
}
