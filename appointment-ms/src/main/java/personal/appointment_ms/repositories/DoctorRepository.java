package personal.appointment_ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.appointment_ms.entities.DoctorEntity;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    
}
