package personal.doctor_ms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import personal.doctor_ms.dtos.CreateDoctorRequest;
import personal.doctor_ms.dtos.CreateSpecialtyRequest;
import personal.doctor_ms.dtos.DoctorResponse;
import personal.doctor_ms.dtos.SpecialtyResponse;
import personal.doctor_ms.dtos.UpdateDoctorRequest;

import java.util.List;

public interface IDoctorService {

    Page<DoctorResponse> findAll(Pageable pageable, String search);

    DoctorResponse findById(Long id);

    // Médico vinculado al usuario autenticado
    DoctorResponse findMe();

    Page<DoctorResponse> findBySpecialty(
            Long specialtyId,
            Pageable pageable,
            String search
    );

    DoctorResponse create(CreateDoctorRequest request);

    DoctorResponse update(
            Long id,
            UpdateDoctorRequest request
    );

    List<SpecialtyResponse> findAllSpecialties();

    SpecialtyResponse createSpecialty(
            CreateSpecialtyRequest request
    );
}