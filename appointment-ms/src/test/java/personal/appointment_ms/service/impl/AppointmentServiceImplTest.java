package personal.appointment_ms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import personal.appointment_ms.security.UserContext;
import personal.appointment_ms.security.UserContextHolder;
import personal.appointment_ms.streams.AppointmentPublisher;
import personal.shared.event.AppointmentCreatedEvent;
import personal.shared.event.AppointmentUpdateStatusEvent;
import personal.shared.event.status.StatusAppointment;
import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private AppointmentTypeRepository appointmentTypeRepository;
    @Mock private PatientClient patientClient;
    @Mock private DoctorClient doctorClient;
    @Mock private BillingClient billingClient;
    @Mock private AppointmentPublisher appointmentPublisher;
    @Mock private PatientRepository patientRepository;
    @Mock private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentServiceImpl service;

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 10, 10, 9, 0);

    @AfterEach
    void limpiarUsuario() {
        UserContextHolder.clear();
    }

    // ---------- datos de prueba ----------

    private CreateAppointmentRequest crearRequest(Integer duracion) {
        return new CreateAppointmentRequest(3L, 7L, 1L, INICIO, duracion, "Dolor de pecho", null);
    }

    private AppointmentType tipo(boolean active) {
        return AppointmentType.builder().id(1L).title("Consulta").active(active).build();
    }

    private PatientEntity pacienteLocal() {
        PatientEntity p = new PatientEntity();
        p.setId(3L);
        p.setFullName("Ana Pérez");
        return p;
    }

    private DoctorEntity doctorLocal() {
        DoctorEntity d = new DoctorEntity();
        d.setId(7L);
        d.setFullName("Luis Ruiz");
        d.setSpecialty("Cardiología");
        return d;
    }

    private DoctorResponse doctorRemoto(Long userId) {
        return new DoctorResponse(7L, "CMP-1", "Luis", "Ruiz", null, null, userId, 2L,
                "Cardiología", null, null, true);
    }

    private Appointment cita(AppointmentStatus status) {
        return Appointment.builder()
                .id(100L).patientId(3L).doctorId(7L)
                .appointmentType(tipo(true))
                .scheduledAt(INICIO).status(status)
                .build();
    }

    private void loguear(Long userId, String... permisos) {
        UserContextHolder.set(new UserContext(userId, "DOCTOR", Set.of(permisos)));
    }

    // Deja listo el camino feliz de createAppointment (paciente y doctor ya copiados localmente)
    private void prepararCreacionValida() {
        when(patientRepository.findById(3L)).thenReturn(Optional.of(pacienteLocal()));
        when(doctorRepository.findById(7L)).thenReturn(Optional.of(doctorLocal()));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(true)));
        when(billingClient.findTariffByAppointmentTypeId(1L))
                .thenReturn(new BillingTariffResponse(1L, new BigDecimal("80.00"), "PEN"));
        when(appointmentRepository.existsOverlappingAppointment(anyLong(), any(), any())).thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment entity = invocation.getArgument(0);
            entity.setId(100L);
            return entity;
        });
    }

    // ---------- createAppointment ----------
    @Test
    void createAppointment_valida_guardaProgramadaYPublicaEvento() {
        prepararCreacionValida();
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));

        AppointmentResponse response = service.createAppointment(crearRequest(45));

        assertEquals(100L, response.id());
        assertEquals(AppointmentStatus.SCHEDULED, response.status());
        assertEquals(45, response.durationMinutes());
        verify(appointmentRepository).existsOverlappingAppointment(7L, INICIO, INICIO.plusMinutes(45));

        ArgumentCaptor<AppointmentCreatedEvent> captor = ArgumentCaptor.forClass(AppointmentCreatedEvent.class);
        verify(appointmentPublisher).publishAppointmentScheduled(captor.capture());
        AppointmentCreatedEvent evento = captor.getValue();
        assertEquals(100L, evento.appointmentId());
        assertEquals("Ana Pérez", evento.patientName());
        assertEquals("Luis Ruiz", evento.doctorName());
        assertEquals(20L, evento.doctorUserId());
        assertEquals(new BigDecimal("80.00"), evento.amount());
        assertEquals(StatusAppointment.SCHEDULED, evento.status());
    }

    @Test
    void createAppointment_sinDuracion_usa30Minutos() {
        prepararCreacionValida();
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));

        AppointmentResponse response = service.createAppointment(crearRequest(null));

        assertEquals(30, response.durationMinutes());
        verify(appointmentRepository).existsOverlappingAppointment(7L, INICIO, INICIO.plusMinutes(30));
    }

    @Test
    void createAppointment_pacienteNoCopiado_loTraeDePatientMsYLoGuarda() {
        prepararCreacionValida();
        when(patientRepository.findById(3L)).thenReturn(Optional.empty());
        when(patientClient.findById(3L)).thenReturn(new PatientResponse(
                3L, "123", "Ana", "Pérez", null, null, null, null, null, null, null, true));
        when(patientRepository.save(any(PatientEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));

        service.createAppointment(crearRequest(30));

        ArgumentCaptor<PatientEntity> captor = ArgumentCaptor.forClass(PatientEntity.class);
        verify(patientRepository).save(captor.capture());
        assertEquals(3L, captor.getValue().getId());
        assertEquals("Ana Pérez", captor.getValue().getFullName());
    }

    @Test
    void createAppointment_doctorNoCopiado_loTraeDeDoctorMsYLoGuarda() {
        prepararCreacionValida();
        when(doctorRepository.findById(7L)).thenReturn(Optional.empty());
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));
        when(doctorRepository.save(any(DoctorEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.createAppointment(crearRequest(30));

        ArgumentCaptor<DoctorEntity> captor = ArgumentCaptor.forClass(DoctorEntity.class);
        verify(doctorRepository).save(captor.capture());
        assertEquals("Luis Ruiz", captor.getValue().getFullName());
        assertEquals("Cardiología", captor.getValue().getSpecialty());
    }

    @Test
    void createAppointment_doctorMsCaidoAlBuscarUserId_igualCreaLaCita() {
        prepararCreacionValida();
        when(doctorClient.findById(7L)).thenThrow(new RuntimeException("doctor-ms caído"));

        AppointmentResponse response = service.createAppointment(crearRequest(30));

        assertEquals(100L, response.id());
        ArgumentCaptor<AppointmentCreatedEvent> captor = ArgumentCaptor.forClass(AppointmentCreatedEvent.class);
        verify(appointmentPublisher).publishAppointmentScheduled(captor.capture());
        assertNull(captor.getValue().doctorUserId()); // sale sin destinatario médico
    }

    @Test
    void createAppointment_tipoNoExiste_lanzaNotFoundYNoGuarda() {
        when(patientRepository.findById(3L)).thenReturn(Optional.of(pacienteLocal()));
        when(doctorRepository.findById(7L)).thenReturn(Optional.of(doctorLocal()));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAppointment(crearRequest(30)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND.toString(), ex.getCode());
        verify(appointmentRepository, never()).save(any());
        verify(appointmentPublisher, never()).publishAppointmentScheduled(any());
    }

    @Test
    void createAppointment_tipoInactivo_lanzaConflict() {
        when(patientRepository.findById(3L)).thenReturn(Optional.of(pacienteLocal()));
        when(doctorRepository.findById(7L)).thenReturn(Optional.of(doctorLocal()));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(false)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAppointment(crearRequest(30)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_TYPE_NOT_ACTIVE.toString(), ex.getCode());
        verify(billingClient, never()).findTariffByAppointmentTypeId(anyLong());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_tarifaEnCero_lanzaTarifaInvalida() {
        when(patientRepository.findById(3L)).thenReturn(Optional.of(pacienteLocal()));
        when(doctorRepository.findById(7L)).thenReturn(Optional.of(doctorLocal()));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(true)));
        when(billingClient.findTariffByAppointmentTypeId(1L))
                .thenReturn(new BillingTariffResponse(1L, BigDecimal.ZERO, "PEN"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAppointment(crearRequest(30)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_TARIFF_INVALID.toString(), ex.getCode());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_tarifaSinPrecio_lanzaTarifaInvalida() {
        when(patientRepository.findById(3L)).thenReturn(Optional.of(pacienteLocal()));
        when(doctorRepository.findById(7L)).thenReturn(Optional.of(doctorLocal()));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(true)));
        when(billingClient.findTariffByAppointmentTypeId(1L))
                .thenReturn(new BillingTariffResponse(1L, null, "PEN"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAppointment(crearRequest(30)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_TARIFF_INVALID.toString(), ex.getCode());
    }

    @Test
    void createAppointment_doctorOcupado_lanzaNoDisponibleYNoGuarda() {
        when(patientRepository.findById(3L)).thenReturn(Optional.of(pacienteLocal()));
        when(doctorRepository.findById(7L)).thenReturn(Optional.of(doctorLocal()));
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(true)));
        when(billingClient.findTariffByAppointmentTypeId(1L))
                .thenReturn(new BillingTariffResponse(1L, new BigDecimal("80.00"), "PEN"));
        when(appointmentRepository.existsOverlappingAppointment(7L, INICIO, INICIO.plusMinutes(30)))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createAppointment(crearRequest(30)));

        assertEquals(AppointmentErrorCode.DOCTOR_NOT_AVAILABLE.toString(), ex.getCode());
        assertEquals(409, ex.getStatus());
        verify(appointmentRepository, never()).save(any());
        verify(appointmentPublisher, never()).publishAppointmentScheduled(any());
    }

    // ---------- consultas ----------
    @Test
    void getAppointments_devuelvePaginaConvertida() {
        Pageable pageable = PageRequest.of(0, 10);
        when(appointmentRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(cita(AppointmentStatus.SCHEDULED)), pageable, 1));

        Page<AppointmentResponse> response = service.getAppointments(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(1L, response.getContent().get(0).appointmentTypeId());
    }

    @Test
    void getAppointmentById_cuandoExiste_devuelveRespuesta() {
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(cita(AppointmentStatus.SCHEDULED)));

        assertEquals(100L, service.getAppointmentById(100L).id());
    }

    @Test
    void getAppointmentById_cuandoNoExiste_lanzaNotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getAppointmentById(99L));

        assertEquals(AppointmentErrorCode.APPOINTMENT_NOT_FOUND.toString(), ex.getCode());
    }

    @Test
    void getAppointmentsByPatient_devuelveLasDelPaciente() {
        when(appointmentRepository.findByPatientId(3L)).thenReturn(List.of(cita(AppointmentStatus.SCHEDULED)));

        assertEquals(1, service.getAppointmentsByPatient(3L).size());
    }

    @Test
    void getAppointmentsByDate_buscaDesdeMedianocheHastaElDiaSiguiente() {
        LocalDate dia = LocalDate.of(2026, 10, 10);
        when(appointmentRepository.findByScheduledAtGreaterThanEqualAndScheduledAtLessThan(
                dia.atStartOfDay(), dia.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(cita(AppointmentStatus.SCHEDULED)));

        assertEquals(1, service.getAppointmentsByDate(dia).size());
    }

    // ---------- getAppointmentsByDoctor: control de acceso ----------
    @Test
    void getAppointmentsByDoctor_conPermisoDeLectura_noConsultaDoctorMs() {
        loguear(1L, "APPOINTMENT_READ");
        when(appointmentRepository.findByDoctorId(7L)).thenReturn(List.of(cita(AppointmentStatus.SCHEDULED)));

        assertEquals(1, service.getAppointmentsByDoctor(7L).size());
        verify(doctorClient, never()).findById(anyLong());
    }

    @Test
    void getAppointmentsByDoctor_medicoConsultaSusPropiasCitas_permite() {
        loguear(20L);
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));
        when(appointmentRepository.findByDoctorId(7L)).thenReturn(List.of(cita(AppointmentStatus.SCHEDULED)));

        assertEquals(1, service.getAppointmentsByDoctor(7L).size());
    }

    @Test
    void getAppointmentsByDoctor_medicoConsultaCitasDeOtro_lanzaForbidden() {
        loguear(99L);
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getAppointmentsByDoctor(7L));

        assertEquals(AppointmentErrorCode.APPOINTMENT_ACCESS_DENIED.toString(), ex.getCode());
        assertEquals(403, ex.getStatus());
        verify(appointmentRepository, never()).findByDoctorId(anyLong());
    }

    @Test
    void getAppointmentsByDoctor_sinUsuario_lanzaForbidden() {
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.getAppointmentsByDoctor(7L));

        assertEquals(AppointmentErrorCode.APPOINTMENT_ACCESS_DENIED.toString(), ex.getCode());
    }

    // ---------- updateStatus / cancelAppointment ----------
    @Test
    void updateStatus_valido_guardaYPublicaEvento() {
        loguear(1L, "APPOINTMENT_READ");
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(cita(AppointmentStatus.SCHEDULED)));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));

        AppointmentResponse response = service.updateStatus(100L,
                new UpdateAppointmentStatusRequest(AppointmentStatus.CONFIRMED));

        assertEquals(AppointmentStatus.CONFIRMED, response.status());
        ArgumentCaptor<AppointmentUpdateStatusEvent> captor =
                ArgumentCaptor.forClass(AppointmentUpdateStatusEvent.class);
        verify(appointmentPublisher).publishAppointmentStatusUpdated(captor.capture());
        assertEquals(StatusAppointment.CONFIRMED, captor.getValue().status());
        assertEquals(20L, captor.getValue().doctorUserId());
    }

    @Test
    void updateStatus_mismoEstado_lanzaConflict() {
        loguear(1L, "APPOINTMENT_READ");
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(cita(AppointmentStatus.CONFIRMED)));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.updateStatus(100L,
                new UpdateAppointmentStatusRequest(AppointmentStatus.CONFIRMED)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_STATUS_ALREADY_SET.toString(), ex.getCode());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void updateStatus_citaYaCompletada_noPermiteCambiar() {
        loguear(1L, "APPOINTMENT_READ");
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(cita(AppointmentStatus.COMPLETED)));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.updateStatus(100L,
                new UpdateAppointmentStatusRequest(AppointmentStatus.CANCELLED)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_STATUS_CANNOT_CHANGE.toString(), ex.getCode());
        verify(appointmentRepository, never()).save(any());
        verify(appointmentPublisher, never()).publishAppointmentStatusUpdated(any());
    }

    @Test
    void updateStatus_citaNoExiste_lanzaNotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.updateStatus(99L,
                new UpdateAppointmentStatusRequest(AppointmentStatus.CONFIRMED)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_NOT_FOUND.toString(), ex.getCode());
    }

    @Test
    void updateStatus_citaDeOtroMedico_lanzaForbiddenYNoGuarda() {
        loguear(99L);
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(cita(AppointmentStatus.SCHEDULED)));
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.updateStatus(100L,
                new UpdateAppointmentStatusRequest(AppointmentStatus.CONFIRMED)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_ACCESS_DENIED.toString(), ex.getCode());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_cambiaEstadoACancelada() {
        loguear(1L, "APPOINTMENT_READ");
        Appointment cita = cita(AppointmentStatus.SCHEDULED);
        when(appointmentRepository.findById(100L)).thenReturn(Optional.of(cita));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(doctorClient.findById(7L)).thenReturn(doctorRemoto(20L));

        service.cancelAppointment(100L);

        assertEquals(AppointmentStatus.CANCELLED, cita.getStatus());
        verify(appointmentPublisher).publishAppointmentStatusUpdated(any(AppointmentUpdateStatusEvent.class));
    }
}
