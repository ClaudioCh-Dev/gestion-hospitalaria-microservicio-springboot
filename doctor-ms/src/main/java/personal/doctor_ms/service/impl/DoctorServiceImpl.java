package personal.doctor_ms.service.impl;

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
import personal.doctor_ms.security.UserContext;
import personal.doctor_ms.security.UserContextHolder;
import personal.doctor_ms.service.IDoctorService;
import personal.doctor_ms.stream.DoctorPublisher;
import personal.shared.event.DoctorCreatedEvent;
import personal.shared.event.DoctorUpdateEvent;
import personal.shared.exception.BusinessException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements IDoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorMapper doctorMapper;
    private final SpecialtyMapper specialtyMapper;
    private final UserClient userClient;
    private final DoctorPublisher doctorPublisher;

    @Override
    public Page<DoctorResponse> findAll(Pageable pageable, String search) {
        return doctorRepository.search(null, toLikePattern(search), pageable)
                .map(doctorMapper::toResponse);
    }

    @Override
    public DoctorResponse findById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        DoctorErrorCode.DOCTOR_NOT_FOUND,
                        "Doctor no encontrado"
                ));

        return doctorMapper.toResponse(doctor);
    }

    @Override
    public DoctorResponse findMe() {

        UserContext context = UserContextHolder.get();

        Doctor doctor = (context == null || context.userId() == null
                ? Optional.<Doctor>empty()
                : doctorRepository.findByUserId(context.userId()))
                .orElseThrow(() -> new BusinessException(
                        DoctorErrorCode.DOCTOR_NOT_FOUND,
                        "El usuario no tiene un médico asociado"
                ));

        return doctorMapper.toResponse(doctor);
    }

    @Override
    public Page<DoctorResponse> findBySpecialty(
            Long specialtyId,
            Pageable pageable,
            String search
    ) {
        return doctorRepository
                .search(specialtyId, toLikePattern(search), pageable)
                .map(doctorMapper::toResponse);
    }

    // "  Ana " -> "%ana%"; vacío -> null para que la consulta ignore el filtro
    private String toLikePattern(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }

        return "%" + search.trim().toLowerCase() + "%";
    }

    @Override
    public DoctorResponse create(CreateDoctorRequest request) {

        if (doctorRepository.existsByEmail(request.email())) {
            throw new BusinessException(
                    DoctorErrorCode.DOCTOR_ALREADY_EXISTS,
                    "El correo electrónico ya está registrado"
            );
        }

        // 1. Verificar especialidad
        Specialty specialty = specialtyRepository
                .findById(request.specialtyId())
                .orElseThrow(() -> new BusinessException(
                        DoctorErrorCode.SPECIALTY_NOT_FOUND,
                        "Especialidad no encontrada"
                ));

        // 2. Crear usuario en Auth Server
        CreateDoctorRequestClient userRequest =
                new CreateDoctorRequestClient(request.email());

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

        // 4. Guardar doctor
        Doctor savedDoctor = doctorRepository.save(doctor);

        // 5. Publicar evento
        doctorPublisher.publishDoctorCreated(
                new DoctorCreatedEvent(
                        savedDoctor.getId(),
                        savedDoctor.getLicenseNumber(),
                        savedDoctor.getFirstName(),
                        savedDoctor.getLastName(),
                        savedDoctor.getEmail(),
                        savedDoctor.getPhone(),
                        savedDoctor.getUserId(),
                        savedDoctor.getSpecialty().getName()
                )
        );

        return doctorMapper.toResponse(savedDoctor);
    }

    @Override
    public List<SpecialtyResponse> findAllSpecialties() {
        return specialtyRepository.findAll()
                .stream()
                .map(specialtyMapper::toResponse)
                .toList();
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
                        DoctorErrorCode.DOCTOR_NOT_FOUND,
                        "Doctor no encontrado"
                ));

        Specialty specialty = specialtyRepository
                .findById(request.specialtyId())
                .orElseThrow(() -> new BusinessException(
                        DoctorErrorCode.SPECIALTY_NOT_FOUND,
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

        doctorPublisher.publishDoctorUpdated(
                new DoctorUpdateEvent(
                        doctor.getId(),
                        doctor.getLicenseNumber(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getEmail(),
                        doctor.getPhone(),
                        doctor.getUserId(),
                        doctor.getSpecialty().getName()
                )
        );

        return doctorMapper.toResponse(
                doctorRepository.save(doctor)
        );
    }
}