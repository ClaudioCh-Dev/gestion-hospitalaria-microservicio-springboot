package personal.medical_record_listener.service;

import java.util.List;

import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.shared.event.MedicalRecordReadyEvent;

public interface IMedicalRecordService {
    
    void save(MedicalRecordReadyEvent event);

    List<MedicalRecordResponse> findByPatientId(Long patientId);

    List<MedicalRecordResponse> findAll();
}
