package com.personal.repository;

import com.personal.dto.PatientResponse;
import com.personal.entities.Patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.personal.enums.Gender;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IPatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmail(String email);

    boolean existsByDocumentNumberAndIdNot(String documentNumber, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
                SELECT new com.personal.dto.PatientResponse(
                    p.id,
                    p.documentNumber,
                    p.firstName,
                    p.lastName,
                    p.gender,
                    p.birthDate,
                    p.phone,
                    p.email,
                    p.active
                )
                FROM Patient p
                WHERE (CAST(:gender AS string) IS NULL OR p.gender = :gender)
                  AND (CAST(:search AS string) IS NULL
                       OR LOWER(p.firstName) LIKE :search
                       OR LOWER(p.lastName) LIKE :search
                       OR LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE :search
                       OR p.documentNumber LIKE :search
                       OR LOWER(COALESCE(p.email, '')) LIKE :search)
            """)
    Page<PatientResponse> findAllResponses(
            @Param("gender") Gender gender,
            @Param("search") String search,
            Pageable pageable);
}