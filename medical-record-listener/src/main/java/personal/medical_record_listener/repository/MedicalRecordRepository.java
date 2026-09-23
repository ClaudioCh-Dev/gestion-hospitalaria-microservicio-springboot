package personal.medical_record_listener.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import personal.medical_record_listener.model.MedicalRecord;

public interface MedicalRecordRepository
        extends MongoRepository<MedicalRecord, String> {

    Page<MedicalRecord> findByPatientId(
            Long patientId,
            Pageable pageable
    );
}