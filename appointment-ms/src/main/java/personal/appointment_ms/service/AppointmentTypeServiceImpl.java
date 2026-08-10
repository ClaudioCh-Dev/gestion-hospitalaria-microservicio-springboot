package personal.appointment_ms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import personal.appointment_ms.dto.AppointmentTypeResponse;
import personal.appointment_ms.dto.CreateAppointmentTypeRequest;
import personal.appointment_ms.dto.UpdateAppointmentTypeRequest;
import personal.appointment_ms.entities.AppointmentType;
import personal.appointment_ms.exceptions.AppointmentTypeAlreadyInactiveException;
import personal.appointment_ms.exceptions.AppointmentTypeNotFoundException;
import personal.appointment_ms.repositories.AppointmentTypeRepository;

@Service
@RequiredArgsConstructor
public class AppointmentTypeServiceImpl implements IAppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepository;

    @Override
    @Transactional
    public AppointmentTypeResponse create(
            CreateAppointmentTypeRequest request) {

        AppointmentType appointmentType = AppointmentType.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .active(true)
                .build();

        AppointmentType saved =
                appointmentTypeRepository.save(appointmentType);

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
                        .orElseThrow(
                                () -> new AppointmentTypeNotFoundException(id));

        return toResponse(appointmentType);
    }

    @Override
    @Transactional
    public AppointmentTypeResponse update(
            Long id,
            UpdateAppointmentTypeRequest request) {

        AppointmentType appointmentType =
                appointmentTypeRepository.findById(id)
                        .orElseThrow(
                                () -> new AppointmentTypeNotFoundException(id));

        appointmentType.setTitle(request.title());
        appointmentType.setDescription(request.description());
        appointmentType.setPrice(request.price());

        AppointmentType updated =
                appointmentTypeRepository.save(appointmentType);

        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {

        AppointmentType appointmentType =
                appointmentTypeRepository.findById(id)
                        .orElseThrow(
                                () -> new AppointmentTypeNotFoundException(id));

        if (!appointmentType.getActive()) {
            throw new AppointmentTypeAlreadyInactiveException(id);
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
                appointmentType.getPrice(),
                appointmentType.getActive());
    }
}