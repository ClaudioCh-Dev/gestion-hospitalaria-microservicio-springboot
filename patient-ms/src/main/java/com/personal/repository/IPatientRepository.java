package com.personal.repository;

import com.personal.dto.PatientResponse;
import com.personal.entities.Patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
        """)
    Page<PatientResponse> findAllResponses(Pageable pageable);
}