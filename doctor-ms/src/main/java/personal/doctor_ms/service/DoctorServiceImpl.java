package personal.doctor_ms.service;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import personal.doctor_ms.dtos.*;
import personal.doctor_ms.entities.Doctor;
import personal.doctor_ms.entities.Specialty;
import personal.doctor_ms.exceptions.ErrorCode;
import personal.doctor_ms.mapper.DoctorMapper;
import personal.doctor_ms.mapper.SpecialtyMapper;
import personal.doctor_ms.repositories.DoctorRepository;
import personal.doctor_ms.repositories.SpecialtyRepository;

import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements IDoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorMapper doctorMapper;
    private final SpecialtyMapper specialtyMapper;

    @Override
    public Page<DoctorResponse> findAll(Pageable pageable) {

        return doctorRepository.findAll(pageable)
                .map(doctorMapper::toResponse);
    }

    @Override
    public DoctorResponse findById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.DOCTOR_NOT_FOUND,
                        "Doctor no encontrado"
                ));

        return doctorMapper.toResponse(doctor);
    }

    @Override
    public Page<DoctorResponse> findBySpecialty(
            Long specialtyId,
            Pageable pageable
    ) {

        return doctorRepository.findBySpecialtyId(specialtyId, pageable)
                .map(doctorMapper::toResponse);
    }

    @Override
    public DoctorResponse create(CreateDoctorRequest request) {

        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.SPECIALTY_NOT_FOUND,
                        "Especialidad no encontrada"
                ));

        Doctor doctor = Doctor.builder()
                .licenseNumber(request.licenseNumber())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .specialty(specialty)
                .scheduleStart(request.scheduleStart())
                .scheduleEnd(request.scheduleEnd())
                .build();

        return doctorMapper.toResponse(
                doctorRepository.save(doctor)
        );
    }

    @Override
    public Page<SpecialtyResponse> findAllSpecialties(Pageable pageable) {

        return specialtyRepository.findAll(pageable)
                .map(specialtyMapper::toResponse);
    }

    @Override
    public SpecialtyResponse createSpecialty(
            CreateSpecialtyRequest request
    ) {

        Specialty specialty = Specialty.builder()
                .name(request.name())
                .description(request.description())
                .build();

        return specialtyMapper.toResponse(
                specialtyRepository.save(specialty)
        );
    }

    @Override
    public DoctorResponse update(
            Long id,
            UpdateDoctorRequest request
    ) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.DOCTOR_NOT_FOUND,
                        "Doctor no encontrado"
                ));

        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.SPECIALTY_NOT_FOUND,
                        "Especialidad no encontrada"
                ));

        doctor.setFirstName(request.firstName());
        doctor.setLastName(request.lastName());
        doctor.setEmail(request.email());
        doctor.setPhone(request.phone());
        doctor.setSpecialty(specialty);
        doctor.setScheduleStart(request.scheduleStart());
        doctor.setScheduleEnd(request.scheduleEnd());
        doctor.setActive(request.active());

        return doctorMapper.toResponse(
                doctorRepository.save(doctor)
        );
    }
}