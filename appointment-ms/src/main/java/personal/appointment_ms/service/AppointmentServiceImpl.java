package personal.appointment_ms.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import personal.appointment_ms.client.BillingClient;
import personal.appointment_ms.client.DoctorClient;
import personal.appointment_ms.client.PatientClient;
import personal.appointment_ms.client.dto.BillingTariffResponse;
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
import personal.shared.event.AppointmentCreatedEvent;
import personal.shared.event.AppointmentUpdateStatusEvent;
import personal.shared.event.status.StatusAppointment;
import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements IAppointmentService {

        private final AppointmentRepository appointmentRepository;
        private final AppointmentTypeRepository appointmentTypeRepository;
        private final PatientClient patientClient;
        private final DoctorClient doctorClient;
        private final BillingClient billingClient;
        private final AppointmentPublisher appointmentPublisher;

        private final PatientRepository patientRepository;
        private final DoctorRepository doctorRepository;

        @Override
        public AppointmentResponse createAppointment(
                        CreateAppointmentRequest request) {

                // 1. Buscar paciente en la BD local
                PatientEntity patientEntity = patientRepository
                                .findById(request.patientId())
                                .orElseGet(() -> {

                                        PatientResponse patient = patientClient.findById(request.patientId());

                                        PatientEntity entity = new PatientEntity();
                                        entity.setId(patient.id());
                                        entity.setFullName(
                                                        patient.firstName() + " " + patient.lastName());

                                        return patientRepository.save(entity);
                                });

                // 2. Buscar doctor en la BD local
                DoctorEntity doctorEntity = doctorRepository
                                .findById(request.doctorId())
                                .orElseGet(() -> {

                                        // Si no existe localmente, buscar en doctor-ms
                                        DoctorResponse doctor = doctorClient.findById(request.doctorId());

                                        // Crear copia local
                                        DoctorEntity entity = new DoctorEntity();
                                        entity.setId(doctor.id());
                                        entity.setUserId(doctor.userId());
                                        entity.setFullName(
                                                        doctor.firstName() + " " + doctor.lastName());
                                        entity.setSpecialty(doctor.specialtyName());

                                        // Guardar y devolver
                                        return doctorRepository.save(entity);
                                });

                // 3. Validar tipo de cita
                AppointmentType appointmentType = appointmentTypeRepository
                                .findById(request.appointmentTypeId())
                                .orElseThrow(() -> new BusinessException(
                                                AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND,
                                                "Tipo de cita no encontrado"));

                if (!appointmentType.getActive()) {
                        throw new BusinessException(
                                        AppointmentErrorCode.APPOINTMENT_TYPE_NOT_ACTIVE,
                                        "El tipo de cita no está activo");
                }

                // 4. Buscar tarifa en bd externa
                BillingTariffResponse tariff = billingClient.findTariffByAppointmentTypeId(request.appointmentTypeId());

                if (tariff.price() == null || tariff.price().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException(
                                        AppointmentErrorCode.APPOINTMENT_TARIFF_INVALID,
                                        "Establece la tarifa para este tipo de cita en gestion Billing");
                }

                // 5. Calcular duración y horario de finalización
                Integer duration = request.durationMinutes() != null
                                ? request.durationMinutes()
                                : 30;

                LocalDateTime start = request.scheduledAt();

                LocalDateTime end = start.plusMinutes(duration);

                // 6. Validar disponibilidad del doctor
                boolean hasOverlap = appointmentRepository.existsOverlappingAppointment(
                                request.doctorId(),
                                start,
                                end);

                if (hasOverlap) {
                        throw new BusinessException(
                                        AppointmentErrorCode.DOCTOR_NOT_AVAILABLE,
                                        "El doctor ya tiene una cita en ese horario");
                }

                // 7. Crear cita
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

                // 8. Guardar
                Appointment savedAppointment = appointmentRepository.save(appointment);

                // 9. Crear evento
                // AppointmentEvent appointmentEvent = buildAppointmentEvent(savedAppointment);
                AppointmentCreatedEvent appointmentEvent = new AppointmentCreatedEvent(
                                appointment.getId(),
                                appointment.getAppointmentType().getTitle(),
                                appointment.getPatientId(),
                                patientEntity.getFullName(),
                                appointment.getDoctorId(),
                                doctorEntity.getFullName(),
                                doctorEntity.getSpecialty(),
                                appointment.getScheduledAt(),
                                appointment.getReason(),
                                StatusAppointment.valueOf(appointment.getStatus().name()),
                                tariff.price(),
                                tariff.currency());

                // 10. Publicar evento
                appointmentPublisher.publishAppointmentScheduled(
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

                if (request.status() == appointment.getStatus()) {
                        throw new BusinessException(
                                        AppointmentErrorCode.APPOINTMENT_STATUS_ALREADY_SET,
                                        "No se puede cambiar el estado de la cita a el mismo estado");
                }

                if (appointment.getStatus().isFinal()) {
                        throw new BusinessException(
                                        AppointmentErrorCode.APPOINTMENT_STATUS_CANNOT_CHANGE,
                                        "No se puede cambiar el estado de una cita completada o cancelada.");
                }

                appointment.setStatus(request.status());
                Appointment updatedAppointment = appointmentRepository.save(appointment);

                AppointmentUpdateStatusEvent appointmentEvent = new AppointmentUpdateStatusEvent(
                                updatedAppointment.getId(),
                                StatusAppointment.valueOf(updatedAppointment.getStatus().name()));

                appointmentPublisher.publishAppointmentStatusUpdated(
                                appointmentEvent);

                return toResponse(updatedAppointment);
        }

        @Override
        public void cancelAppointment(Long id) {
               updateStatus(id, new UpdateAppointmentStatusRequest(AppointmentStatus.CANCELLED));
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

}