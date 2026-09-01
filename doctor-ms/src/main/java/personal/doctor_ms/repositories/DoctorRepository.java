package personal.doctor_ms.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import personal.doctor_ms.entities.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
     Page<Doctor> findBySpecialtyId(Long specialtyId, Pageable pageable);

     boolean existsByEmail(String email);
}
