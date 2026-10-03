package com.hospital.auth_ms.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.hospital.auth_ms.exceptions.AuthErrorCode;

import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    // redisTemplate.opsForValue() y opsForSet() devuelven otros objetos: también son mocks
    @Mock
    private ValueOperations<String, String> valueOps;

    @Mock
    private SetOperations<String, String> setOps;

    @InjectMocks
    private RefreshTokenServiceImpl service;

    @BeforeEach
    void configurar() {
        // @Value no funciona sin Spring: se asigna el campo privado a mano
        ReflectionTestUtils.setField(service, "refreshTokenExpiration", 3600L);
    }

    // ---------- createRefreshToken ----------
    @Test
    void createRefreshToken_guardaElHashConExpiracionYLoIndexaPorUsuario() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);

        String token = service.createRefreshToken(20L);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOps).set(keyCaptor.capture(), eq("20"), eq(Duration.ofSeconds(3600)));
        String key = keyCaptor.getValue();
        assertTrue(key.startsWith("refresh:"));
        // En Redis se guarda el HASH, nunca el token en claro
        assertNotEquals("refresh:" + token, key);

        String hash = key.substring("refresh:".length());
        verify(setOps).add("user-refresh:20", hash);
        verify(redisTemplate).expire("user-refresh:20", Duration.ofSeconds(3600));
    }

    @Test
    void createRefreshToken_cadaLlamadaGeneraUnTokenDistinto() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);

        assertNotEquals(service.createRefreshToken(20L), service.createRefreshToken(20L));
    }

    // ---------- validateRefreshToken ----------
    @Test
    void validateRefreshToken_existente_devuelveElUserId() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(anyString())).thenReturn("20");

        assertEquals(20L, service.validateRefreshToken("token"));
    }

    @Test
    void validateRefreshToken_mismoTokenSiempreBuscaLaMismaClave() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(anyString())).thenReturn("20");

        service.validateRefreshToken("token");
        service.validateRefreshToken("token");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(valueOps, org.mockito.Mockito.times(2)).get(captor.capture());
        assertEquals(captor.getAllValues().get(0), captor.getAllValues().get(1)); // el hash es determinista
    }

    @Test
    void validateRefreshToken_vencidoONoExiste_lanzaRefreshInvalido() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(anyString())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validateRefreshToken("token"));

        assertEquals(AuthErrorCode.AUTH_INVALID_REFRESH_TOKEN.toString(), ex.getCode());
        assertEquals(401, ex.getStatus());
    }

    // ---------- revokeRefreshToken ----------
    @Test
    void revokeRefreshToken_existente_loBorraYLoQuitaDelIndice() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        when(valueOps.get(anyString())).thenReturn("20");

        service.revokeRefreshToken("token");

        verify(setOps).remove(eq("user-refresh:20"), anyString());
        verify(redisTemplate).delete(anyString());
    }

    @Test
    void revokeRefreshToken_yaVencido_soloIntentaBorrarLaClave() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(anyString())).thenReturn(null);

        service.revokeRefreshToken("token");

        verify(redisTemplate, never()).opsForSet();
        verify(redisTemplate).delete(anyString());
    }

    // ---------- revokeAllByUserId ----------
    @Test
    void revokeAllByUserId_borraCadaTokenYElIndice() {
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        when(setOps.members("user-refresh:20")).thenReturn(Set.of("h1", "h2"));

        service.revokeAllByUserId(20L);

        verify(redisTemplate).delete("refresh:h1");
        verify(redisTemplate).delete("refresh:h2");
        verify(redisTemplate).delete("user-refresh:20");
    }

    @Test
    void revokeAllByUserId_sinTokens_noBorraNada() {
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        when(setOps.members("user-refresh:20")).thenReturn(Set.of());

        service.revokeAllByUserId(20L);

        verify(redisTemplate, never()).delete(any(String.class));
    }
}
