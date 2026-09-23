package personal.medical_record_listener.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.shared.event.MedicalRecordReadyEvent;

public interface IMedicalRecordService {

    void save(MedicalRecordReadyEvent event);

    Page<MedicalRecordResponse> findByPatientId(
        Long patientId,
        Pageable pageable
    );

    Page<MedicalRecordResponse> findAll(
        Pageable pageable
    );
}