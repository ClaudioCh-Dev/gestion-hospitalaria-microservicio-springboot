package personal.appointment_ms.listeners;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import personal.appointment_ms.entities.DoctorEntity;
import personal.appointment_ms.entities.PatientEntity;
import personal.appointment_ms.repositories.DoctorRepository;
import personal.appointment_ms.repositories.PatientRepository;
import personal.appointment_ms.security.UserContext;
import personal.appointment_ms.security.UserContextHolder;
import personal.shared.event.DoctorCreatedEvent;
import personal.shared.event.DoctorUpdateEvent;
import personal.shared.event.PatientCreateEvent;
import personal.shared.event.PatientUpdateEvent;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AppointmentListener {

        private final DoctorRepository doctorRepository;
        private final PatientRepository patientRepository;

        @Bean
        public Consumer<Message<DoctorCreatedEvent>> doctorCreatedConsumer() {

                return message -> executeWithContext(message, () -> {

                        DoctorCreatedEvent event = message.getPayload();

                        DoctorEntity doctor = doctorRepository
                                        .findById(event.doctorId())
                                        .orElseGet(DoctorEntity::new);

                        doctor.setId(event.doctorId());
                        doctor.setFullName(
                                        event.firstName() + " " + event.lastName());
                        doctor.setSpecialty(event.specialty());

                        doctorRepository.save(doctor);

                        log.info(
                                        "Doctor creado/actualizado en appointment-ms. doctorId={}",
                                        event.doctorId());
                });
        }

        @Bean
        public Consumer<Message<DoctorUpdateEvent>> doctorUpdatedConsumer() {

                return message -> executeWithContext(message, () -> {

                        DoctorUpdateEvent event = message.getPayload();

                        DoctorEntity doctor = doctorRepository
                                        .findById(event.doctorId())
                                        .orElseGet(DoctorEntity::new);

                        doctor.setId(event.doctorId());
                        doctor.setFullName(
                                        event.firstName() + " " + event.lastName());
                        doctor.setSpecialty(event.specialty());

                        doctorRepository.save(doctor);

                        log.info(
                                        "Doctor actualizado en appointment-ms. doctorId={}",
                                        event.doctorId());
                });
        }

        @Bean
        public Consumer<Message<PatientCreateEvent>> patientCreatedConsumer() {

                return message -> executeWithContext(message, () -> {

                        PatientCreateEvent event = message.getPayload();

                        PatientEntity patient = patientRepository
                                        .findById(event.id())
                                        .orElseGet(PatientEntity::new);

                        patient.setId(event.id());
                        patient.setFullName(
                                        event.firstName() + " " + event.lastName());

                        patientRepository.save(patient);

                        log.info(
                                        "Paciente creado/actualizado en appointment-ms. patientId={}",
                                        event.id());
                });
        }

        @Bean
        public Consumer<Message<PatientUpdateEvent>> patientUpdatedConsumer() {

                return message -> executeWithContext(message, () -> {

                        PatientUpdateEvent event = message.getPayload();

                        PatientEntity patient = patientRepository
                                        .findById(event.id())
                                        .orElseGet(PatientEntity::new);

                        patient.setId(event.id());
                        patient.setFullName(
                                        event.firstName() + " " + event.lastName());

                        patientRepository.save(patient);

                        log.info(
                                        "Paciente actualizado en appointment-ms. patientId={}",
                                        event.id());
                });
        }

        private <T> void executeWithContext(
                        Message<T> message,
                        Runnable action) {

                try {

                        UserContext context = buildUserContext(message);

                        UserContextHolder.set(context);

                        action.run();

                } catch (Exception e) {

                        log.error(
                                        "Error procesando evento en appointment-ms",
                                        e);

                } finally {

                        UserContextHolder.clear();
                }
        }

        private UserContext buildUserContext(
                        Message<?> message) {

                String userId = (String) message.getHeaders()
                                .get("X-User-Id");

                String role = (String) message.getHeaders()
                                .get("X-Role");

                String permissionsHeader = (String) message.getHeaders()
                                .get("X-Permissions");

                Set<String> permissions = permissionsHeader == null
                                ? Set.of()
                                : Arrays.stream(permissionsHeader.split(","))
                                                .map(String::trim)
                                                .collect(Collectors.toSet());

                return new UserContext(
                                userId != null
                                                ? Long.valueOf(userId)
                                                : null,
                                role,
                                permissions);
        }
}