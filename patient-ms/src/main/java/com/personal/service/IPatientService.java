package com.personal.service;

import com.personal.dto.PatientDetailResponse;
import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPatientService {

    Page<PatientResponse> findAll(Pageable pageable);

    PatientDetailResponse findById(Long id);

    PatientResponse findByDocumentNumber(String documentNumber);

    PatientResponse create(PatientRequest request);

    PatientResponse update(Long id, PatientRequest request);

    void delete(Long id);
}