package com.personal.repository;

import com.personal.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByDocumentNumber(String documentNumber);
}
