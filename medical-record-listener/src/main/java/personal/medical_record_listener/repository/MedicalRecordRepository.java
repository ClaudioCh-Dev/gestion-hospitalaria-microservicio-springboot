package personal.medical_record_listener.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import personal.medical_record_listener.model.MedicalRecord;

public interface MedicalRecordRepository
        extends MongoRepository<MedicalRecord, String> {

    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);

    Page<MedicalRecord> findByPatientId(
            Long patientId,
            Pageable pageable
    );
}