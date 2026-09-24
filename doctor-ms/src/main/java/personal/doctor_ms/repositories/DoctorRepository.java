package personal.doctor_ms.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import personal.doctor_ms.entities.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
     Page<Doctor> findBySpecialtyId(Long specialtyId, Pageable pageable);

     boolean existsByEmail(String email);

     Optional<Doctor> findByUserId(Long userId);

     // specialtyId y search son opcionales (null = sin filtro); search llega como patrón LIKE en minúsculas
     @Query("""
             SELECT d FROM Doctor d JOIN d.specialty s
             WHERE (:specialtyId IS NULL OR s.id = :specialtyId)
               AND (CAST(:search AS string) IS NULL
                    OR LOWER(d.firstName) LIKE :search
                    OR LOWER(d.lastName) LIKE :search
                    OR LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE :search
                    OR LOWER(d.licenseNumber) LIKE :search
                    OR LOWER(COALESCE(d.email, '')) LIKE :search
                    OR LOWER(s.name) LIKE :search)
             """)
     Page<Doctor> search(
             @Param("specialtyId") Long specialtyId,
             @Param("search") String search,
             Pageable pageable);
}
