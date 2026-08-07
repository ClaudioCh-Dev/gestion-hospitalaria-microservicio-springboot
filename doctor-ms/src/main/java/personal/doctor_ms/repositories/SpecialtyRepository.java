package personal.doctor_ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.doctor_ms.entities.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty,Long> {
    
    
}
