package personal.doctor_ms.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import personal.doctor_ms.client.UserClient;
import personal.doctor_ms.client.dto.CreateDoctorRequestClient;
import personal.doctor_ms.client.dto.UserResponse;
import personal.doctor_ms.dtos.*;
import personal.doctor_ms.entities.Doctor;
import personal.doctor_ms.entities.Specialty;
import personal.doctor_ms.exceptions.DoctorErrorCode;
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
        private final UserClient userClient;

        @Override
        public Page<DoctorResponse> findAll(Pageable pageable) {

                return doctorRepository.findAll(pageable)
                                .map(doctorMapper::toResponse);
        }

        @Override
        public DoctorResponse findById(Long id) {

                Doctor doctor = doctorRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                DoctorErrorCode.DOCTOR_NOT_FOUND,
                                                "Doctor no encontrado"));

                return doctorMapper.toResponse(doctor);
        }

        @Override
        public Page<DoctorResponse> findBySpecialty(
                        Long specialtyId,
                        Pageable pageable) {

                return doctorRepository.findBySpecialtyId(specialtyId, pageable)
                                .map(doctorMapper::toResponse);
        }

        @Override
        public DoctorResponse create(CreateDoctorRequest request) {

                // 1. Verificar especialidad
                Specialty specialty = specialtyRepository
                                .findById(request.specialtyId())
                                .orElseThrow(() -> new BusinessException(
                                                DoctorErrorCode.SPECIALTY_NOT_FOUND,
                                                "Especialidad no encontrada"));

                // 2. Crear usuario en Auth Server
                CreateDoctorRequestClient userRequest = new CreateDoctorRequestClient(request.email());
                
                UserResponse user = userClient.createDoctor(userRequest);

                // 3. Crear doctor asociado al usuario
                Doctor doctor = Doctor.builder()
                                .userId(user.id())
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
                                doctorRepository.save(doctor));
        }

        @Override
        public Page<SpecialtyResponse> findAllSpecialties(Pageable pageable) {

                return specialtyRepository.findAll(pageable)
                                .map(specialtyMapper::toResponse);
        }

        @Override
        public SpecialtyResponse createSpecialty(
                        CreateSpecialtyRequest request) {

                Specialty specialty = Specialty.builder()
                                .name(request.name())
                                .description(request.description())
                                .build();

                return specialtyMapper.toResponse(
                                specialtyRepository.save(specialty));
        }

        @Override
        public DoctorResponse update(
                        Long id,
                        UpdateDoctorRequest request) {

                Doctor doctor = doctorRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                DoctorErrorCode.DOCTOR_NOT_FOUND,
                                                "Doctor no encontrado"));

                Specialty specialty = specialtyRepository.findById(request.specialtyId())
                                .orElseThrow(() -> new BusinessException(
                                                DoctorErrorCode.SPECIALTY_NOT_FOUND,
                                                "Especialidad no encontrada"));

                doctor.setFirstName(request.firstName());
                doctor.setLastName(request.lastName());
                doctor.setEmail(request.email());
                doctor.setPhone(request.phone());
                doctor.setSpecialty(specialty);
                doctor.setScheduleStart(request.scheduleStart());
                doctor.setScheduleEnd(request.scheduleEnd());
                doctor.setActive(request.active());

                return doctorMapper.toResponse(
                                doctorRepository.save(doctor));
        }
}