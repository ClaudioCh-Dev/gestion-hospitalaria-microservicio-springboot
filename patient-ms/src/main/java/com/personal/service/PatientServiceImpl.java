package com.personal.service;

import com.personal.dto.PatientDetailResponse;
import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import com.personal.entities.Patient;
import com.personal.exceptions.PatientNotFoundException;
import com.personal.repository.IPatientRepository;
import com.personal.streams.PatientPublisher;

import personal.shared.event.PatientCreatedEvent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientServiceImpl implements IPatientService {

    private final IPatientRepository patientRepository;
    private final PatientPublisher patientPublisher;

    public PatientServiceImpl(
            IPatientRepository patientRepository,
            PatientPublisher patientPublisher) {
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
    public PatientDetailResponse findById(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        return mapToDetailResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse findByDocumentNumber(String documentNumber) {

        Patient patient = patientRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new PatientNotFoundException(documentNumber));

        return mapToResponse(patient);
    }

    @Override
    @Transactional
    public PatientResponse create(PatientRequest request) {

        Patient patient = mapToEntity(request);

        Patient savedPatient = patientRepository.save(patient);

        PatientCreatedEvent event = new PatientCreatedEvent(
                savedPatient.getId(),
                savedPatient.getFirstName(),
                savedPatient.getLastName(),
                savedPatient.getEmail());

        patientPublisher.publishPatientCreated(event);

        return mapToResponse(savedPatient);
    }

    @Override
    @Transactional
    public PatientResponse update(
            Long id,
            PatientRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        updatePatientFields(patient, request);

        Patient updatedPatient = patientRepository.save(patient);

        return mapToResponse(updatedPatient);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!patientRepository.existsById(id)) {

            throw new PatientNotFoundException(id);
        }

        patientRepository.deleteById(id);
    }

    private PatientResponse mapToResponse(Patient patient) {

        return new PatientResponse(
                patient.getId(),
                patient.getDocumentNumber(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getBirthDate(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getActive());
    }

    private PatientDetailResponse mapToDetailResponse(Patient patient) {

        return new PatientDetailResponse(
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
                patient.getActive(),
                patient.getCreatedAt(),
                patient.getUpdatedAt());
    }

    private Patient mapToEntity(PatientRequest request) {

        Patient patient = new Patient();

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

    private void updatePatientFields(
            Patient patient,
            PatientRequest request) {

        if (request.firstName() != null) {
            patient.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            patient.setLastName(request.lastName());
        }

        if (request.documentNumber() != null) {
            patient.setDocumentNumber(request.documentNumber());
        }

        if (request.phone() != null) {
            patient.setPhone(request.phone());
        }

        if (request.email() != null) {
            patient.setEmail(request.email());
        }

        if (request.address() != null) {
            patient.setAddress(request.address());
        }

        if (request.bloodType() != null) {
            patient.setBloodType(request.bloodType());
        }

        if (request.allergies() != null) {
            patient.setAllergies(request.allergies());
        }

        if (request.birthDate() != null) {
            patient.setBirthDate(request.birthDate());
        }

        if (request.gender() != null) {
            patient.setGender(request.gender());
        }
    }
}