package com.personal.service;

import com.personal.dto.PatientDetailResponse;
import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import com.personal.entities.Patient;
import com.personal.exceptions.ErrorCode;
import com.personal.mapper.PatientMapper;
import com.personal.repository.IPatientRepository;
import com.personal.streams.PatientPublisher;

import personal.shared.event.PatientCreatedEvent;
import personal.shared.exception.BusinessException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientServiceImpl implements IPatientService {

    private final IPatientRepository patientRepository;
    private final PatientPublisher patientPublisher;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(
            IPatientRepository patientRepository,
            PatientPublisher patientPublisher,
            PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientPublisher = patientPublisher;
        this.patientMapper = patientMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> findAll(Pageable pageable) {
        return patientRepository.findAllResponses(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDetailResponse findById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PATIENT_NOT_FOUND,
                        "Paciente no encontrado"));

        return patientMapper.toDetailResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse findByDocumentNumber(String documentNumber) {
        Patient patient = patientRepository
                .findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PATIENT_NOT_FOUND,
                        "Paciente no encontrado"));

        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional
    public PatientResponse create(PatientRequest request) {

        if (patientRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new BusinessException(
                    ErrorCode.PATIENT_DOCUMENT_ALREADY_EXISTS,
                    "El número de documento ya está registrado");
        }

        if (patientRepository.existsByEmail(request.email())) {
            throw new BusinessException(
                    ErrorCode.PATIENT_EMAIL_ALREADY_EXISTS,
                    "El correo electrónico ya está registrado");
        }

        Patient patient = patientMapper.toEntity(request);

        Patient savedPatient = patientRepository.save(patient);

        PatientCreatedEvent event = new PatientCreatedEvent(
                savedPatient.getId(),
                savedPatient.getFirstName(),
                savedPatient.getLastName(),
                savedPatient.getEmail());

        patientPublisher.publishPatientCreated(event);

        return patientMapper.toResponse(savedPatient);
    }

    @Override
    @Transactional
    public PatientResponse update(
            Long id,
            PatientRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PATIENT_NOT_FOUND,
                        "Paciente no encontrado"));

        if (patientRepository.existsByDocumentNumberAndIdNot(
                request.documentNumber(),
                id)) {
            throw new BusinessException(
                    ErrorCode.PATIENT_DOCUMENT_ALREADY_EXISTS,
                    "El número de documento ya está registrado");
        }

        if (patientRepository.existsByEmailAndIdNot(
                request.email(),
                id)) {
            throw new BusinessException(
                    ErrorCode.PATIENT_EMAIL_ALREADY_EXISTS,
                    "El correo electrónico ya está registrado");
        }

        patientMapper.updateEntity(request, patient);

        Patient updatedPatient = patientRepository.save(patient);

        return patientMapper.toResponse(updatedPatient);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!patientRepository.existsById(id)) {
            throw new BusinessException(
                    ErrorCode.PATIENT_NOT_FOUND,
                    "Paciente no encontrado");
        }

        patientRepository.deleteById(id);
    }
}