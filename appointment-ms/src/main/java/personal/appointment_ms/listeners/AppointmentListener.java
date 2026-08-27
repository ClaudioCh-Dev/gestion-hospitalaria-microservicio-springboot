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
import personal.shared.event.PatientCreatedEvent;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AppointmentListener {

        private final DoctorRepository doctorRepository;
        private final PatientRepository patientRepository;

        @Bean
        public Consumer<Message<DoctorCreatedEvent>> doctorCreatedConsumer() {

                return message -> {

                        try {

                                UserContext context = buildUserContext(message);
                                UserContextHolder.set(context);

                                DoctorCreatedEvent event = message.getPayload();

                                DoctorEntity doctor = new DoctorEntity();
                                doctor.setId(event.doctorId());
                                doctor.setUserId(event.userId());
                                doctor.setFullName(
                                                event.firstName() + " " + event.lastName());

                                doctor.setSpecialty(event.specialty());
                                doctorRepository.save(doctor);

                                log.info(
                                                "Doctor guardado en appointment-ms. doctorId={}",
                                                event.doctorId());

                        } catch (Exception e) {

                                log.error(
                                                "Error procesando DoctorCreatedEvent",
                                                e);

                        } finally {

                                UserContextHolder.clear();
                        }
                };
        }

        @Bean
        public Consumer<Message<PatientCreatedEvent>> patientCreatedConsumer() {

                return message -> {

                        try {

                                UserContext context = buildUserContext(message);
                                UserContextHolder.set(context);

                                PatientCreatedEvent event = message.getPayload();

                                PatientEntity patient = new PatientEntity();

                                patient.setId(event.id());
                                patient.setFullName(
                                                event.firstName() + " " + event.lastName());

                                patientRepository.save(patient);

                                log.info(
                                                "Paciente guardado en appointment-ms. patientId={}",
                                                event.id());

                        } catch (Exception e) {

                                log.error(
                                                "Error procesando PatientCreatedEvent",
                                                e);

                        } finally {

                                UserContextHolder.clear();
                        }
                };
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