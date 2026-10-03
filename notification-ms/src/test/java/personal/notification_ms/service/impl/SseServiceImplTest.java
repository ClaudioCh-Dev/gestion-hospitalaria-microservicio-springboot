package personal.notification_ms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import personal.notification_ms.exceptions.NotificationErrorCode;
import personal.notification_ms.security.UserContext;
import personal.notification_ms.security.UserContextHolder;
import personal.shared.exception.BusinessException;

/*
 * SseServiceImpl no tiene dependencias: crea los SseEmitter por dentro, así que no hay mocks.
 * Se revisan sus mapas internos con ReflectionTestUtils. Un emitter sin conexión HTTP acepta
 * envíos (los guarda en memoria); uno ya completado lanza IllegalStateException al enviarle algo,
 * que es justo lo que pasa cuando el cliente cerró la pestaña.
 */
class SseServiceImplTest {

    private final SseServiceImpl service = new SseServiceImpl();

    @AfterEach
    void limpiarUsuario() {
        UserContextHolder.clear();
    }

    private SseEmitter conectar(Long userId, String... permisos) {
        UserContextHolder.set(new UserContext(userId, "DOCTOR", Set.of(permisos)));
        SseEmitter emitter = service.subscribe();
        UserContextHolder.clear();
        return emitter;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, List<SseEmitter>> conexionesPorUsuario() {
        return (Map<Long, List<SseEmitter>>) ReflectionTestUtils.getField(service, "emittersByUser");
    }

    @SuppressWarnings("unchecked")
    private Map<SseEmitter, Long> conexionesAdmin() {
        return (Map<SseEmitter, Long>) ReflectionTestUtils.getField(service, "adminEmitters");
    }

    // ---------- subscribe ----------
    @Test
    void subscribe_sinUsuario_lanzaUsuarioRequerido() {
        BusinessException ex = assertThrows(BusinessException.class, service::subscribe);

        assertEquals(NotificationErrorCode.SSE_USER_REQUIRED.toString(), ex.getCode());
        assertEquals(401, ex.getStatus());
    }

    @Test
    void subscribe_medico_registraLaConexionSoloEnSuUsuario() {
        SseEmitter emitter = conectar(20L);

        assertNotNull(emitter);
        assertEquals(List.of(emitter), conexionesPorUsuario().get(20L));
        assertTrue(conexionesAdmin().isEmpty());
    }

    @Test
    void subscribe_admin_registraLaConexionTambienComoAdmin() {
        SseEmitter emitter = conectar(1L, "NOTIFICATION_READ_ADMIN");

        assertEquals(1L, conexionesAdmin().get(emitter));
        assertEquals(List.of(emitter), conexionesPorUsuario().get(1L));
    }

    @Test
    void subscribe_variasPestanas_guardaTodasLasConexiones() {
        conectar(20L);
        conectar(20L);

        assertEquals(2, conexionesPorUsuario().get(20L).size());
    }

    // ---------- sendNotification ----------
    @Test
    void sendNotification_conexionCerrada_laQuitaDeLosMapas() {
        SseEmitter cerrada = conectar(20L);
        SseEmitter abierta = conectar(20L);
        cerrada.complete(); // el cliente cerró la pestaña

        service.sendNotification(20L, "evento");

        assertEquals(List.of(abierta), conexionesPorUsuario().get(20L));
    }

    @Test
    void sendNotification_ultimaConexionCerrada_eliminaAlUsuario() {
        SseEmitter cerrada = conectar(20L);
        cerrada.complete();

        service.sendNotification(20L, "evento");

        assertFalse(conexionesPorUsuario().containsKey(20L));
    }

    @Test
    void sendNotification_adminCerrado_seQuitaDeAdminsAunqueElEventoNoSeaParaEl() {
        SseEmitter admin = conectar(1L, "NOTIFICATION_READ_ADMIN");
        admin.complete();

        service.sendNotification(null, "evento"); // sin médico: solo admins

        assertTrue(conexionesAdmin().isEmpty());
        assertFalse(conexionesPorUsuario().containsKey(1L));
    }

    @Test
    void sendNotification_noTocaConexionesDeOtrosMedicos() {
        SseEmitter otroMedico = conectar(30L);
        otroMedico.complete(); // cerrada, pero el evento no es para él

        service.sendNotification(20L, "evento");

        // Como no se le envió nada, todavía no se detectó que estaba cerrada
        assertEquals(List.of(otroMedico), conexionesPorUsuario().get(30L));
    }

    // ---------- heartbeat ----------
    @Test
    void heartbeat_detectaYQuitaConexionesCaidas() {
        SseEmitter caida = conectar(20L);
        SseEmitter viva = conectar(30L);
        caida.complete();

        service.heartbeat();

        assertFalse(conexionesPorUsuario().containsKey(20L));
        assertEquals(List.of(viva), conexionesPorUsuario().get(30L));
    }
}
