package personal.appointment_ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.appointment_ms.entities.PatientEntity;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    
}
