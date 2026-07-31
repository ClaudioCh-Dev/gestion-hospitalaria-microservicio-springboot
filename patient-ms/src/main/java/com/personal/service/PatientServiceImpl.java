package com.personal.service;

import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import com.personal.entities.Patient;
import com.personal.repository.IPatientRepository;
import com.personal.streams.PatientPublisher;
import com.personal.service.IPatientService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientServiceImpl implements IPatientService {

    private final IPatientRepository patientRepository;
    private final PatientPublisher patientPublisher;

    public PatientServiceImpl(IPatientRepository patientRepository, PatientPublisher patientPublisher) {
        this.patientRepository = patientRepository;
        this.patientPublisher = patientPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        return mapToResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse findByDocumentNumber(String documentNumber) {
        Patient patient = patientRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new RuntimeException("Patient not found with document number: " + documentNumber));
        return mapToResponse(patient);
    }

    @Override
    @Transactional
    public PatientResponse create(PatientRequest request) {
        Patient patient = mapToEntity(request);
        Patient savedPatient = patientRepository.save(patient);

        patientPublisher.publishPatientCreated(savedPatient);

        return mapToResponse(savedPatient);
    }

    @Override
    @Transactional
    public PatientResponse update(Long id, PatientRequest request) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        // Actualizar campos permitidos desde el request (Record)
        if(request.firstName() != null) {
            existingPatient.setFirstName(request.firstName());
        }
        if(request.lastName() != null) {
            existingPatient.setLastName(request.lastName());
        }
        if(request.documentNumber() != null) {
            existingPatient.setDocumentNumber(request.documentNumber());
        }
        if(request.phone() != null) {
            existingPatient.setPhone(request.phone());
        }
        if(request.email() != null) {
            existingPatient.setEmail(request.email());
        }
        if(request.address() != null) {
            existingPatient.setAddress(request.address());
        }
        if(request.bloodType() != null) {
            existingPatient.setBloodType(request.bloodType());
        }
        if(request.allergies() != null) {
            existingPatient.setAllergies(request.allergies());
        }
        if(request.birthDate() != null) {
            existingPatient.setBirthDate(request.birthDate());
        }
        if(request.gender() != null) {
            existingPatient.setGender(request.gender());
        }

        Patient updatedPatient = patientRepository.save(existingPatient);
        return mapToResponse(updatedPatient);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    // --- Métodos de Mapeo Corregidos ---

    private PatientResponse mapToResponse(Patient patient) {
        // En los Records se retorna construyéndolo en una sola instrucción
        // Usamos "get..." asumiendo que Patient es una Entidad tradicional de Hibernate
        return new PatientResponse(
                patient.getId(),
                patient.getDocumentNumber(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getBirthDate(),
                patient.getGender(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getBloodType(),
                patient.getAllergies(),
                patient.getActive(), // Ojo: verifica si tu entidad usa getActive() o getIsActive()
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    private Patient mapToEntity(PatientRequest request) {
        Patient patient = new Patient();
        // Al crear la entidad, leemos del Record sin "get" y seteamos con "set"
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setDocumentNumber(request.documentNumber());
        patient.setBirthDate(request.birthDate());
        patient.setGender(request.gender());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setBloodType(request.bloodType());
        patient.setAllergies(request.allergies());

        return patient;
    }
}