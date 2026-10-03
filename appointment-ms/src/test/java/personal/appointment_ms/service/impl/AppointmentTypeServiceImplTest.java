package personal.appointment_ms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import personal.appointment_ms.dto.AppointmentTypeResponse;
import personal.appointment_ms.dto.CreateAppointmentTypeRequest;
import personal.appointment_ms.dto.UpdateAppointmentTypeRequest;
import personal.appointment_ms.entities.AppointmentType;
import personal.appointment_ms.entities.AppointmentTypeColor;
import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.appointment_ms.repositories.AppointmentTypeRepository;
import personal.appointment_ms.streams.AppointmentPublisher;
import personal.shared.event.AppointmentCreatedTypeEvent;
import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AppointmentTypeServiceImplTest {

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @Mock
    private AppointmentPublisher publisher;

    @InjectMocks
    private AppointmentTypeServiceImpl service;

    private AppointmentType tipo(boolean active) {
        return AppointmentType.builder()
                .id(1L)
                .title("Consulta general")
                .description("Primera consulta")
                .active(active)
                .color("emerald")
                .build();
    }

    // save simula la BD: le asigna id a la entidad, como haría IDENTITY
    private void saveAsignaId() {
        when(appointmentTypeRepository.save(any(AppointmentType.class)))
                .thenAnswer(invocation -> {
                    AppointmentType entity = invocation.getArgument(0);
                    if (entity.getId() == null) {
                        entity.setId(1L);
                    }
                    return entity;
                });
    }

    // ---------- create ----------
    @Test
    void create_conColor_guardaYPublicaEventoConElId() {
        saveAsignaId();
        CreateAppointmentTypeRequest request = new CreateAppointmentTypeRequest(
                "Consulta general", "Primera consulta", new BigDecimal("80.00"), "violet");

        AppointmentTypeResponse response = service.create(request);

        assertEquals(1L, response.id());
        assertEquals("Consulta general", response.title());
        assertEquals("violet", response.color());
        assertTrue(response.active()); // un tipo nuevo nace activo para poder agendar citas

        // El evento debe llevar el id que asignó la BD (sale de "saved", no del request)
        ArgumentCaptor<AppointmentCreatedTypeEvent> captor =
                ArgumentCaptor.forClass(AppointmentCreatedTypeEvent.class);
        verify(publisher).publishAppointmentCreatedType(captor.capture());
        assertEquals(1L, captor.getValue().id());
        assertEquals("Consulta general", captor.getValue().title());
        // billing-ms crea la tarifa con el precio que vino en el request
        assertEquals(new BigDecimal("80.00"), captor.getValue().price());
    }

    @Test
    void create_sinColor_usaColorPorDefecto() {
        saveAsignaId();
        CreateAppointmentTypeRequest request = new CreateAppointmentTypeRequest(
                "Control", null, null, null);

        AppointmentTypeResponse response = service.create(request);

        assertEquals(AppointmentTypeColor.DEFAULT, response.color());
    }

    // ---------- findAll / findById ----------
    @Test
    void findAll_devuelveTodosConvertidos() {
        when(appointmentTypeRepository.findAll()).thenReturn(List.of(tipo(true), tipo(false)));

        List<AppointmentTypeResponse> response = service.findAll();

        assertEquals(2, response.size());
        assertEquals("emerald", response.get(0).color());
    }

    @Test
    void findById_cuandoExiste_devuelveRespuesta() {
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(true)));

        AppointmentTypeResponse response = service.findById(1L);

        assertEquals("Consulta general", response.title());
    }

    @Test
    void findById_cuandoNoExiste_lanzaNotFound() {
        when(appointmentTypeRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.findById(99L));

        assertEquals(AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
    }

    // ---------- update ----------
    @Test
    void update_conColor_cambiaTituloDescripcionYColor() {
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(true)));
        saveAsignaId();

        AppointmentTypeResponse response = service.update(1L,
                new UpdateAppointmentTypeRequest("Control", "Seguimiento", null, "rose"));

        assertEquals("Control", response.title());
        assertEquals("Seguimiento", response.description());
        assertEquals("rose", response.color());
    }

    @Test
    void update_sinColor_conservaElColorActual() {
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(true)));
        saveAsignaId();

        AppointmentTypeResponse response = service.update(1L,
                new UpdateAppointmentTypeRequest("Control", "Seguimiento", null, null));

        assertEquals("emerald", response.color());
    }

    @Test
    void update_conActive_reactivaElTipo() {
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(false)));
        saveAsignaId();

        AppointmentTypeResponse response = service.update(1L,
                new UpdateAppointmentTypeRequest("Control", null, true, null));

        assertTrue(response.active());
    }

    @Test
    void update_sinActive_conservaElEstado() {
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(false)));
        saveAsignaId();

        AppointmentTypeResponse response = service.update(1L,
                new UpdateAppointmentTypeRequest("Control", null, null, null));

        assertFalse(response.active());
    }

    @Test
    void update_cuandoNoExiste_lanzaNotFoundYNoGuarda() {
        when(appointmentTypeRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(99L,
                        new UpdateAppointmentTypeRequest("Control", null, null, null)));

        assertEquals(AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND.toString(), ex.getCode());
        verify(appointmentTypeRepository, never()).save(any());
    }

    // ---------- deactivate ----------
    @Test
    void deactivate_cuandoActivo_loDesactivaYGuarda() {
        AppointmentType activo = tipo(true);
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(activo));

        service.deactivate(1L);

        // deactivate es void: no hay respuesta, revisamos la entidad que se guardó
        verify(appointmentTypeRepository).save(activo);
        assertFalse(activo.getActive());
    }

    @Test
    void deactivate_cuandoYaInactivo_lanzaConflictYNoGuarda() {
        when(appointmentTypeRepository.findById(1L)).thenReturn(Optional.of(tipo(false)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deactivate(1L));

        assertEquals(AppointmentErrorCode.APPOINTMENT_TYPE_ALREADY_INACTIVE.toString(), ex.getCode());
        assertEquals(409, ex.getStatus());
        verify(appointmentTypeRepository, never()).save(any());
    }

    @Test
    void deactivate_cuandoNoExiste_lanzaNotFound() {
        when(appointmentTypeRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deactivate(99L));

        assertEquals(AppointmentErrorCode.APPOINTMENT_TYPE_NOT_FOUND.toString(), ex.getCode());
        verify(appointmentTypeRepository, never()).save(any());
    }
}
