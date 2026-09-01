package personal.appointment_ms.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import personal.appointment_ms.dto.AppointmentTypeResponse;
import personal.appointment_ms.dto.CreateAppointmentTypeRequest;
import personal.appointment_ms.dto.UpdateAppointmentTypeRequest;
import personal.appointment_ms.entities.AppointmentType;
import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.appointment_ms.repositories.AppointmentTypeRepository;
import personal.appointment_ms.service.IAppointmentTypeService;
import personal.appointment_ms.streams.AppointmentPublisher;
import personal.shared.event.AppointmentCreatedTypeEvent;
import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class AppointmentTypeServiceImpl implements IAppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentPublisher publisher;

    @Override
    @Transactional
    public AppointmentTypeResponse create(
            CreateAppointmentTypeRequest request) {

        AppointmentType appointmentType = AppointmentType.builder()
                .title(request.title())
                .description(request.description())
                .active(false)
                .build();

        AppointmentType saved =
                appointmentTypeRepository.save(appointmentType);

        publisher.publishAppointmentCreatedType(
                new AppointmentCreatedTypeEvent(
                        saved.getId(),
                        saved.getTitle(),
                        saved.getDescription(),
                        saved.getActive()
                )
        );

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentTypeResponse> findAll() {

        return appointmentTypeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentTypeResponse findById(Long id) {

        AppointmentType appointmentType =
                appointmentTypeRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND,
                                "Tipo de cita no encontrado"
                        ));

        return toResponse(appointmentType);
    }

    @Override
    @Transactional
    public AppointmentTypeResponse update(
            Long id,
            UpdateAppointmentTypeRequest request) {

        AppointmentType appointmentType =
                appointmentTypeRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND,
                                "Tipo de cita no encontrado"
                        ));

        appointmentType.setTitle(request.title());
        appointmentType.setDescription(request.description());

        AppointmentType updated =
                appointmentTypeRepository.save(appointmentType);

        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {

        AppointmentType appointmentType =
                appointmentTypeRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND,
                                "Tipo de cita no encontrado"
                        ));

        if (!appointmentType.getActive()) {
            throw new BusinessException(
                    AppointmentErrorCode.APPOINTMENT_TYPE_ALREADY_INACTIVE,
                    "El tipo de cita ya está inactivo"
            );
        }

        appointmentType.setActive(false);

        appointmentTypeRepository.save(appointmentType);
    }

    private AppointmentTypeResponse toResponse(
            AppointmentType appointmentType) {

        return new AppointmentTypeResponse(
                appointmentType.getId(),
                appointmentType.getTitle(),
                appointmentType.getDescription(),
                appointmentType.getActive()
        );
    }
}