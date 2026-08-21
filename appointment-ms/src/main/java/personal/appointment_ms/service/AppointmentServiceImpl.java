package personal.appointment_ms.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import personal.appointment_ms.client.DoctorClient;
import personal.appointment_ms.client.PatientClient;
import personal.appointment_ms.dto.AppointmentResponse;
import personal.appointment_ms.dto.CreateAppointmentRequest;
import personal.appointment_ms.dto.UpdateAppointmentStatusRequest;
import personal.appointment_ms.entities.Appointment;
import personal.appointment_ms.entities.AppointmentStatus;
import personal.appointment_ms.entities.AppointmentType;
import personal.appointment_ms.exceptions.ErrorCode;
import personal.appointment_ms.repositories.AppointmentRepository;
import personal.appointment_ms.repositories.AppointmentTypeRepository;
import personal.appointment_ms.streams.AppointmentPublisher;
import personal.shared.event.AppointmentEvent;
import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;
    private final AppointmentPublisher appointmentPublisher;

    @Override
    public AppointmentResponse createAppointment(
            CreateAppointmentRequest request) {

        // Feign client call
        patientClient.findById(request.patientId());
        doctorClient.findById(request.doctorId());

        AppointmentType appointmentType = appointmentTypeRepository
                .findById(request.appointmentTypeId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.APPOINTMENT_TYPE_NOT_FOUND,
                        "Tipo de cita no encontrado"
                ));

        Appointment appointment = Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .appointmentType(appointmentType)
                .scheduledAt(request.scheduledAt())
                .durationMinutes(
                        request.durationMinutes() != null
                                ? request.durationMinutes()
                                : 30)
                .reason(request.reason())
                .status(AppointmentStatus.SCHEDULED)
                .notes(request.notes())
                .build();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        AppointmentEvent appointmentEvent =
                buildAppointmentEvent(savedAppointment);

        publishAppointmentEvent(
                savedAppointment.getStatus(),
                appointmentEvent);

        return toResponse(savedAppointment);
    }

    @Override
    public Page<AppointmentResponse> getAppointments(
            Pageable pageable) {

        return appointmentRepository
                .findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.APPOINTMENT_NOT_FOUND,
                        "Cita no encontrada"
                ));

        return toResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatient(
            Long patientId) {

        return appointmentRepository
                .findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDoctor(
            Long doctorId) {

        return appointmentRepository
                .findByDoctorId(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse updateStatus(
            Long id,
            UpdateAppointmentStatusRequest request) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.APPOINTMENT_NOT_FOUND,
                        "Cita no encontrada"
                ));

        appointment.setStatus(request.status());

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        AppointmentEvent appointmentEvent =
                buildAppointmentEvent(updatedAppointment);

        publishAppointmentEvent(
                updatedAppointment.getStatus(),
                appointmentEvent);

        return toResponse(updatedAppointment);
    }

    @Override
    public void cancelAppointment(Long id) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.APPOINTMENT_NOT_FOUND,
                        "Cita no encontrada"
                ));

        appointment.setStatus(AppointmentStatus.CANCELLED);

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        AppointmentEvent appointmentEvent =
                buildAppointmentEvent(updatedAppointment);

        publishAppointmentEvent(
                updatedAppointment.getStatus(),
                appointmentEvent);
    }

    private AppointmentResponse toResponse(
            Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getScheduledAt(),
                appointment.getDurationMinutes(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt()
        );
    }

    // TODO: Implementar Outbox Pattern para garantizar
    // la publicación del AppointmentEvent en Kafka.
    private AppointmentEvent buildAppointmentEvent(
            Appointment appointment) {

        String eventType = switch (appointment.getStatus()) {
            case SCHEDULED -> "appointment-created";
            case CONFIRMED -> "appointment-confirmed";
            case COMPLETED -> "appointment-completed";
            case CANCELLED -> "appointment-cancelled";
        };

        return new AppointmentEvent(
                appointment.getId(),
                appointment.getPatientId(),
                "TODO",
                appointment.getDoctorId(),
                "TODO",
                "TODO",
                appointment.getScheduledAt().toString(),
                appointment.getReason(),
                appointment.getStatus().name(),
                appointment.getAppointmentType().getPrice(),
                eventType
        );
    }

    private void publishAppointmentEvent(
            AppointmentStatus status,
            AppointmentEvent appointmentEvent) {

        switch (status) {
            case SCHEDULED ->
                    appointmentPublisher.publishAppointmentScheduled(
                            appointmentEvent);

            case CONFIRMED ->
                    appointmentPublisher.publishAppointmentConfirmed(
                            appointmentEvent);

            case COMPLETED ->
                    appointmentPublisher.publishAppointmentCompleted(
                            appointmentEvent);

            case CANCELLED ->
                    appointmentPublisher.publishAppointmentCanceled(
                            appointmentEvent);
        }
    }
}