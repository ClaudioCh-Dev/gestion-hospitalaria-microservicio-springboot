package personal.appointment_ms.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import personal.appointment_ms.client.DoctorClient;
import personal.appointment_ms.client.PatientClient;
import personal.appointment_ms.dto.AppointmentResponse;
import personal.appointment_ms.dto.CreateAppointmentRequest;
import personal.appointment_ms.dto.UpdateAppointmentStatusRequest;
import personal.appointment_ms.entities.Appointment;
import personal.appointment_ms.entities.AppointmentStatus;
import personal.appointment_ms.exceptions.AppointmentNotFoundException;
import personal.appointment_ms.repositories.AppointmentRepository;
import personal.appointment_ms.streams.AppointmentPublisher;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;
    private final AppointmentPublisher appointmentPublisher;

    @Override
    public AppointmentResponse createAppointment(
            CreateAppointmentRequest request
    ) {

        patientClient.findById(request.patientId());

        doctorClient.findById(request.doctorId());

        Appointment appointment = Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .scheduledAt(request.scheduledAt())
                .durationMinutes(
                        request.durationMinutes() != null
                                ? request.durationMinutes()
                                : 30
                )
                .reason(request.reason())
                .status(AppointmentStatus.SCHEDULED)
                .notes(request.notes())
                .build();

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        appointmentPublisher.publishAppointmentCreated(savedAppointment);
                
        return toResponse(savedAppointment);
    }

    @Override
    public Page<AppointmentResponse> getAppointments(
            Pageable pageable
    ) {

        return appointmentRepository
                .findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public AppointmentResponse getAppointmentById(
            Long id
    ) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(id)
                );

        return toResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatient(
            Long patientId
    ) {

        return appointmentRepository
                .findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDoctor(
            Long doctorId
    ) {

        return appointmentRepository
                .findByDoctorId(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AppointmentResponse updateStatus(
            Long id,
            UpdateAppointmentStatusRequest request
    ) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(id)
                );

        appointment.setStatus(request.status());

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        if (request.status() == AppointmentStatus.COMPLETED) {
            appointmentPublisher.publishAppointmentCompleted(updatedAppointment);
        }

        if(request.status() == AppointmentStatus.CANCELLED) {
            appointmentPublisher.publishAppointmentCanceled(updatedAppointment);
        }

        return toResponse(updatedAppointment);
    }

    @Override
    public void cancelAppointment(Long id) {

        Appointment appointment = appointmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(id)
                );

        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.save(appointment);
        
        appointmentPublisher.publishAppointmentCanceled(appointment);
    }

    private AppointmentResponse toResponse(
            Appointment appointment
    ) {

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
}
