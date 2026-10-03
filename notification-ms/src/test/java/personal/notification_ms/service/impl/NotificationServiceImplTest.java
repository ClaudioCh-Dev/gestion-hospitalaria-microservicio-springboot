package personal.notification_ms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.exceptions.NotificationErrorCode;
import personal.notification_ms.mapper.NotificationMapper;
import personal.notification_ms.mapper.NotificationMapperImpl;
import personal.notification_ms.model.Notification;
import personal.notification_ms.model.NotificationRecipient;
import personal.notification_ms.model.NotificationType;
import personal.notification_ms.repository.NotificationRecipientRepository;
import personal.notification_ms.repository.NotificationRepository;
import personal.notification_ms.security.UserContext;
import personal.notification_ms.security.UserContextHolder;
import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationRecipientRepository recipientRepository;

    // @Spy: usamos el mapper REAL (la clase que genera MapStruct), no uno falso.
    // Así el test también comprueba que el mapeo funciona.
    @Spy
    private NotificationMapper notificationMapper = new NotificationMapperImpl();

    @InjectMocks
    private NotificationServiceImpl service;

    // El servicio lee el usuario logueado de un ThreadLocal estático:
    // hay que limpiarlo después de cada test para no "contagiar" al siguiente
    @AfterEach
    void limpiarUsuario() {
        UserContextHolder.clear();
    }

    private void loguearUsuario(Long userId) {
        UserContextHolder.set(new UserContext(userId, "DOCTOR", Set.of()));
    }

    private Notification notificacion(Long id) {
        return Notification.builder()
                .id(id)
                .type("APPOINTMENT_CREATED")
                .title("Nueva cita")
                .referenceType("APPOINTMENT")
                .referenceId(5L)
                .doctorUserId(20L)
                .createdAt(LocalDateTime.of(2026, 10, 1, 9, 0))
                .build();
    }

    // ---------- save ----------
    @Test
    void save_copiaMetadataAColumnasYPoneFechaDeCreacion() {
        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        NotificationRequest request = NotificationRequest.builder()
                .type(NotificationType.values()[0])
                .title("Nueva cita")
                .referenceType("APPOINTMENT")
                .referenceId(5L)
                // Desde el POST (JSON) los números llegan como Integer: el mapper debe pasarlos a Long
                .metadata(Map.of("doctorId", 7, "patientName", "Ana Pérez"))
                .build();

        NotificationResponse response = service.save(request);

        assertEquals(7L, response.doctorId());
        assertEquals("Ana Pérez", response.patientName());
        assertNotNull(response.createdAt());
        assertFalse(response.read()); // recién creada: nadie la leyó
    }

    // ---------- findMine ----------
    @Test
    void findMine_sinUsuarioLogueado_devuelveVacioSinConsultar() {
        // No llamamos a loguearUsuario: UserContextHolder.get() devuelve null

        List<NotificationResponse> response = service.findMine();

        assertTrue(response.isEmpty());
        verify(repository, never()).findTop50ByDoctorUserIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    void findMine_marcaComoLeidasSoloLasQueElUsuarioLeyo() {
        loguearUsuario(20L);
        when(repository.findTop50ByDoctorUserIdOrderByCreatedAtDesc(20L))
                .thenReturn(List.of(notificacion(1L), notificacion(2L)));
        when(recipientRepository.findReadNotificationIds(20L, List.of(1L, 2L)))
                .thenReturn(Set.of(2L));

        List<NotificationResponse> response = service.findMine();

        assertEquals(2, response.size());
        assertFalse(response.get(0).read()); // id 1: no leída
        assertTrue(response.get(1).read());  // id 2: leída
    }

    @Test
    void findMine_sinNotificaciones_noConsultaLeidas() {
        loguearUsuario(20L);
        when(repository.findTop50ByDoctorUserIdOrderByCreatedAtDesc(20L)).thenReturn(List.of());

        List<NotificationResponse> response = service.findMine();

        assertTrue(response.isEmpty());
        verify(recipientRepository, never()).findReadNotificationIds(any(), any());
    }

    // ---------- findForAdmin ----------
    @Test
    void findForAdmin_sinUsuario_devuelveTodasComoNoLeidas() {
        when(repository.findTop50ByOrderByCreatedAtDesc()).thenReturn(List.of(notificacion(1L)));

        List<NotificationResponse> response = service.findForAdmin();

        assertEquals(1, response.size());
        assertFalse(response.get(0).read());
        verify(recipientRepository, never()).findReadNotificationIds(any(), any());
    }

    // ---------- markAsRead ----------
    @Test
    void markAsRead_cuandoNoExiste_lanzaNotFoundYNoGuarda() {
        when(repository.existsById(99L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.markAsRead(99L));

        assertEquals(NotificationErrorCode.NOTIFICATION_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
        verify(recipientRepository, never()).save(any());
    }

    @Test
    void markAsRead_primeraVez_creaRegistroDeLecturaParaElUsuario() {
        loguearUsuario(20L);
        when(repository.existsById(1L)).thenReturn(true);
        when(recipientRepository.findByNotificationIdAndUserId(1L, 20L))
                .thenReturn(Optional.empty()); // camino orElseGet: se crea uno nuevo

        service.markAsRead(1L);

        ArgumentCaptor<NotificationRecipient> captor =
                ArgumentCaptor.forClass(NotificationRecipient.class);
        verify(recipientRepository).save(captor.capture());
        NotificationRecipient guardado = captor.getValue();
        assertEquals(1L, guardado.getNotificationId());
        assertEquals(20L, guardado.getUserId());
        assertTrue(guardado.isRead());
        assertNotNull(guardado.getReadAt());
    }

    @Test
    void markAsRead_yaTeniaRegistro_actualizaElMismo() {
        loguearUsuario(20L);
        NotificationRecipient existente = NotificationRecipient.builder()
                .id(50L).notificationId(1L).userId(20L).read(false).build();
        when(repository.existsById(1L)).thenReturn(true);
        when(recipientRepository.findByNotificationIdAndUserId(1L, 20L))
                .thenReturn(Optional.of(existente));

        service.markAsRead(1L);

        ArgumentCaptor<NotificationRecipient> captor =
                ArgumentCaptor.forClass(NotificationRecipient.class);
        verify(recipientRepository).save(captor.capture());
        assertSame(existente, captor.getValue()); // el mismo objeto, no uno nuevo (no duplica filas)
        assertTrue(existente.isRead());
    }

    // ---------- findAppointmentMetadata ----------
    @Test
    void findAppointmentMetadata_soloIncluyeLosCamposConValor() {
        Notification origen = Notification.builder()
                .doctorId(7L)
                .patientName("Ana Pérez")
                .scheduledAt(LocalDateTime.of(2026, 10, 5, 10, 30))
                .build(); // doctorName, specialty, reason quedan en null
        when(repository.findFirstByReferenceTypeAndReferenceIdOrderByIdAsc("APPOINTMENT", 5L))
                .thenReturn(Optional.of(origen));

        Map<String, Object> metadata = service.findAppointmentMetadata(5L);

        assertEquals(7L, metadata.get("doctorId"));
        assertEquals("Ana Pérez", metadata.get("patientName"));
        assertEquals("2026-10-05T10:30", metadata.get("scheduledAt"));
        assertFalse(metadata.containsKey("doctorName"));
        assertEquals(3, metadata.size());
    }

    @Test
    void findAppointmentMetadata_sinNotificacionDeOrigen_devuelveMapaVacio() {
        when(repository.findFirstByReferenceTypeAndReferenceIdOrderByIdAsc("APPOINTMENT", 5L))
                .thenReturn(Optional.empty());

        assertTrue(service.findAppointmentMetadata(5L).isEmpty());
    }
}
