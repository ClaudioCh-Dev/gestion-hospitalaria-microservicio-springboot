package personal.medical_record_listener.controller;
    
import lombok.RequiredArgsConstructor;
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
    public List<MedicalRecordResponse> findByPatientId(
            @PathVariable Long patientId) {
        return service.findByPatientId(patientId);
    }

    @GetMapping
    public List<MedicalRecordResponse> findAll() {
        return service.findAll();
    }
}