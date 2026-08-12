package personal.medical_record_listener.repository;

import personal.medical_record_listener.model.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MedicalRecordRepository
        extends MongoRepository<MedicalRecord, String> {

    List<MedicalRecord> findByPatientId(Long patientId);
}