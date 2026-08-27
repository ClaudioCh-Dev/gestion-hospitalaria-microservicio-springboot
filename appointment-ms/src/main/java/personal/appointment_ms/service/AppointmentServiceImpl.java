package personal.appointment_ms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import personal.appointment_ms.client.DoctorClient;
import personal.appointment_ms.client.PatientClient;
import personal.appointment_ms.client.dto.DoctorResponse;
import personal.appointment_ms.client.dto.PatientResponse;
import personal.appointment_ms.dto.AppointmentResponse;
import personal.appointment_ms.dto.CreateAppointmentRequest;
import personal.appointment_ms.dto.UpdateAppointmentStatusRequest;
import personal.appointment_ms.entities.Appointment;
import personal.appointment_ms.entities.AppointmentStatus;
import personal.appointment_ms.entities.AppointmentType;
import personal.appointment_ms.entities.DoctorEntity;
import personal.appointment_ms.entities.PatientEntity;
import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.appointment_ms.repositories.AppointmentRepository;
import personal.appointment_ms.repositories.AppointmentTypeRepository;
import personal.appointment_ms.repositories.DoctorRepository;
import personal.appointment_ms.repositories.PatientRepository;
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
        private final PatientRepository patientRepository;
        private final DoctorRepository doctorRepository;

        @Override
        public AppointmentResponse createAppointment(
                        CreateAppointmentRequest request) {

                // 1. Buscar paciente en la BD local
                if (!patientRepository.existsById(request.patientId())) {

                        PatientResponse patient = patientClient.findById(request.patientId());

                        PatientEntity patientEntity = new PatientEntity();

                        patientEntity.setId(patient.id());
                        patientEntity.setFullName(
                                        patient.firstName() + " " + patient.lastName());

                        patientRepository.save(patientEntity);
                }

                // 2. Buscar doctor en la BD local
                if (!doctorRepository.existsById(request.doctorId())) {

                        DoctorResponse doctor = doctorClient.findById(request.doctorId());

                        DoctorEntity doctorEntity = new DoctorEntity();

                        doctorEntity.setId(doctor.id());
                        doctorEntity.setUserId(doctor.userId());
                        doctorEntity.setFullName(
                                        doctor.firstName() + " " + doctor.lastName());
                        doctorEntity.setSpecialty(doctor.specialtyName());

                        doctorRepository.save(doctorEntity);
                }

                // 3. Validar tipo de cita
                AppointmentType appointmentType = appointmentTypeRepository
                                .findById(request.appointmentTypeId())
                                .orElseThrow(() -> new BusinessException(
                                                AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND,
                                                "Tipo de cita no encontrado"));

                // 4. Calcular duración y horario de finalización
                Integer duration = request.durationMinutes() != null
                                ? request.durationMinutes()
                                : 30;

                LocalDateTime start = request.scheduledAt();

                LocalDateTime end = start.plusMinutes(duration);

                // 5. Validar disponibilidad del doctor
                boolean hasOverlap = appointmentRepository.existsOverlappingAppointment(
                                request.doctorId(),
                                start,
                                end);

                if (hasOverlap) {
                        throw new BusinessException(
                                        AppointmentErrorCode.DOCTOR_NOT_AVAILABLE,
                                        "El doctor ya tiene una cita en ese horario");
                }

                // 6. Crear cita
                Appointment appointment = Appointment.builder()
                                .patientId(request.patientId())
                                .doctorId(request.doctorId())
                                .appointmentType(appointmentType)
                                .scheduledAt(start)
                                .durationMinutes(duration)
                                .reason(request.reason())
                                .status(AppointmentStatus.SCHEDULED)
                                .notes(request.notes())
                                .build();

                // 7. Guardar
                Appointment savedAppointment = appointmentRepository.save(appointment);

                // 8. Crear evento
                AppointmentEvent appointmentEvent = buildAppointmentEvent(savedAppointment);

                // 9. Publicar evento
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
                                                AppointmentErrorCode.APPOINTMENT_NOT_FOUND,
                                                "Cita no encontrada"));

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
                                                AppointmentErrorCode.APPOINTMENT_NOT_FOUND,
                                                "Cita no encontrada"));

                appointment.setStatus(request.status());

                Appointment updatedAppointment = appointmentRepository.save(appointment);

                AppointmentEvent appointmentEvent = buildAppointmentEvent(updatedAppointment);

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
                                                AppointmentErrorCode.APPOINTMENT_NOT_FOUND,
                                                "Cita no encontrada"));

                appointment.setStatus(AppointmentStatus.CANCELLED);

                Appointment updatedAppointment = appointmentRepository.save(appointment);

                AppointmentEvent appointmentEvent = buildAppointmentEvent(updatedAppointment);

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
                                appointment.getCreatedAt());
        }

        private AppointmentEvent buildAppointmentEvent(
                        Appointment appointment) {

                String eventType = switch (appointment.getStatus()) {
                        case SCHEDULED -> "appointment-created";
                        case CONFIRMED -> "appointment-confirmed";
                        case COMPLETED -> "appointment-completed";
                        case CANCELLED -> "appointment-cancelled";
                };

                // TODO Mejorar esto
                PatientEntity patient = patientRepository.findById(appointment.getPatientId())
                                .orElse(new PatientEntity());
                DoctorEntity doctor = doctorRepository.findById(appointment.getDoctorId()).orElse(new DoctorEntity());

                return new AppointmentEvent(
                                appointment.getId(),
                                appointment.getPatientId(),
                                patient.getFullName(),
                                appointment.getDoctorId(),
                                doctor.getFullName(),
                                doctor.getSpecialty(),
                                appointment.getScheduledAt().toString(),
                                appointment.getReason(),
                                appointment.getStatus().name(),
                                appointment.getAppointmentType().getPrice(),
                                eventType);
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