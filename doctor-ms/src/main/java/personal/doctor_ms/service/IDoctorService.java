package personal.doctor_ms.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import personal.doctor_ms.dtos.CreateDoctorRequest;
import personal.doctor_ms.dtos.CreateSpecialtyRequest;
import personal.doctor_ms.dtos.DoctorResponse;
import personal.doctor_ms.dtos.SpecialtyResponse;
import personal.doctor_ms.dtos.UpdateDoctorRequest; 

public interface IDoctorService {

    Page<DoctorResponse> findAll(Pageable pageable);

    DoctorResponse findById(Long id);

    Page<DoctorResponse> findBySpecialty(Long specialtyId, Pageable pageable);

    DoctorResponse create(CreateDoctorRequest request);

    DoctorResponse update(Long id, UpdateDoctorRequest request);

    Page<SpecialtyResponse> findAllSpecialties(Pageable pageable);

    SpecialtyResponse createSpecialty(CreateSpecialtyRequest request);
    
}
