package com.personal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.personal.dto.PatientDetailResponse;
import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import com.personal.entities.Patient;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientResponse toResponse(Patient patient);

    PatientDetailResponse toDetailResponse(Patient patient);

    Patient toEntity(PatientRequest request);

    void updateEntity(
            PatientRequest request,
            @MappingTarget Patient patient
    );
}