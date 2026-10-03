package personal.billing_ms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import personal.billing_ms.dto.BillingTariffResponse;
import personal.billing_ms.dto.CreateBillingTariffRequest;
import personal.billing_ms.dto.UpdateBillingTariffRequest;
import personal.billing_ms.entities.BillingTariff;
import personal.billing_ms.exceptions.BillingErrorCode;
import personal.billing_ms.repositories.BillingTariffRepository;
import personal.shared.exception.BusinessException;

/*
 * Prueba UNITARIA: probamos solo BillingTariffServiceImpl, sin Spring, sin base de datos.
 * El repositorio es un "mock" (un doble falso) cuyo comportamiento definimos nosotros.
 */
@ExtendWith(MockitoExtension.class) // activa Mockito en JUnit 5 (procesa @Mock e @InjectMocks)
class BillingTariffServiceImplTest {

    @Mock // crea un BillingTariffRepository falso: por defecto devuelve null / false / Optional.empty()
    private BillingTariffRepository billingTariffRepository;

    @InjectMocks // crea el servicio REAL y le inyecta los @Mock por constructor
    private BillingTariffServiceImpl service;

    // ---------- Caso feliz ----------
    @Test
    void createTariff_cuandoNoExiste_guardaYDevuelveRespuesta() {
        // ARRANGE (preparar): datos de entrada y comportamiento del mock
        CreateBillingTariffRequest request =
                new CreateBillingTariffRequest(1L, new BigDecimal("80.00"), "PEN");

        when(billingTariffRepository.existsById(1L)).thenReturn(false);
        // save devuelve lo mismo que recibe (simula que la BD lo guardó)
        when(billingTariffRepository.save(any(BillingTariff.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT (ejecutar): llamamos al método que estamos probando
        BillingTariffResponse response = service.createTariff(request);

        // ASSERT (verificar): el resultado es el esperado
        assertEquals(1L, response.appointmentTypeId());
        assertEquals(new BigDecimal("80.00"), response.price());
        assertEquals("PEN", response.currency());

        // Verificamos QUÉ entidad se mandó a guardar usando un ArgumentCaptor
        ArgumentCaptor<BillingTariff> captor = ArgumentCaptor.forClass(BillingTariff.class);
        verify(billingTariffRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getBillingAppointmentTypeId());
    }

    // ---------- Caso de error ----------
    @Test
    void createTariff_cuandoYaExiste_lanzaBusinessExceptionYNoGuarda() {
        // ARRANGE
        CreateBillingTariffRequest request =
                new CreateBillingTariffRequest(1L, new BigDecimal("80.00"), "PEN");
        when(billingTariffRepository.existsById(1L)).thenReturn(true);

        // ACT + ASSERT: assertThrows ejecuta el lambda y exige que lance esa excepción
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.createTariff(request)
        );

        assertEquals(BillingErrorCode.BILLING_TARIFF_ALREADY_EXISTS.toString(), ex.getCode());
        assertEquals(400, ex.getStatus());

        // Nunca debió intentar guardar
        verify(billingTariffRepository, never()).save(any());
    }

    // ---------- Otro método: lectura ----------
    @Test
    void getTariff_cuandoNoExiste_lanzaNotFound() {
        when(billingTariffRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.getTariff(99L)
        );

        assertEquals(BillingErrorCode.BILLING_TARIFF_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
    }

    // ---------- Ejercicio 1: updateTariff, caso feliz ----------
    @Test
    void updateTariff_cuandoExiste_actualizaPrecioYMoneda() {
        // ARRANGE: la tarifa que "ya está en la BD"
        BillingTariff existente = BillingTariff.builder()
                .billingAppointmentTypeId(1L)
                .price(new BigDecimal("50.00"))
                .currency("PEN")
                .build();

        when(billingTariffRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(billingTariffRepository.save(any(BillingTariff.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateBillingTariffRequest request =
                new UpdateBillingTariffRequest(new BigDecimal("75.00"), "USD");

        // ACT
        BillingTariffResponse response = service.updateTariff(1L, request);

        // ASSERT: la respuesta trae los valores nuevos
        assertEquals(1L, response.appointmentTypeId());
        assertEquals(new BigDecimal("75.00"), response.price());
        assertEquals("USD", response.currency());

        // y se guardó la MISMA entidad, ya modificada (no una nueva)
        verify(billingTariffRepository).save(existente);
    }

    // ---------- Ejercicio 2: updateTariff, no existe ----------
    @Test
    void updateTariff_cuandoNoExiste_lanzaNotFoundYNoGuarda() {
        // ARRANGE: no hay tarifa con id 99
        when(billingTariffRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateBillingTariffRequest request =
                new UpdateBillingTariffRequest(new BigDecimal("75.00"), "USD");

        // ACT + ASSERT
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.updateTariff(99L, request)
        );

        assertEquals(BillingErrorCode.BILLING_TARIFF_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());

        // Si no existe, no debe guardar nada
        verify(billingTariffRepository, never()).save(any());
    }

    // ---------- Ejercicio 3: getPriceByAppointmentTypeId ----------
    @Test
    void getPriceByAppointmentTypeId_cuandoExiste_devuelvePrecio() {
        // ARRANGE
        BillingTariff tarifa = BillingTariff.builder()
                .billingAppointmentTypeId(1L)
                .price(new BigDecimal("120.50"))
                .currency("PEN")
                .build();
        when(billingTariffRepository.findById(1L)).thenReturn(Optional.of(tarifa));

        // ACT
        BigDecimal precio = service.getPriceByAppointmentTypeId(1L);

        // ASSERT
        assertEquals(new BigDecimal("120.50"), precio);
    }

    @Test
    void getPriceByAppointmentTypeId_cuandoNoExiste_lanzaNotFound() {
        when(billingTariffRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> service.getPriceByAppointmentTypeId(99L)
        );

        assertEquals(BillingErrorCode.BILLING_TARIFF_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
    }

    // ---------- existsTariff ----------
    @Test
    void existsTariff_delegaEnElRepositorio() {
        when(billingTariffRepository.existsById(1L)).thenReturn(true);

        assertTrue(service.existsTariff(1L));
    }

    // ---------- Reto extra: getTariffs ----------
    @Test
    void getTariffs_cuandoHayDos_devuelveListaConDos() {
        BillingTariff consulta = BillingTariff.builder()
                .billingAppointmentTypeId(1L)
                .price(new BigDecimal("80.00"))
                .currency("PEN")
                .build();
        BillingTariff control = BillingTariff.builder()
                .billingAppointmentTypeId(2L)
                .price(new BigDecimal("50.00"))
                .currency("USD")
                .build();
        when(billingTariffRepository.findAll()).thenReturn(List.of(consulta, control));

        List<BillingTariffResponse> response = service.getTariffs();

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).appointmentTypeId());
        assertEquals(new BigDecimal("50.00"), response.get(1).price());
        assertEquals("USD", response.get(1).currency());
    }

    @Test
    void getTariffs_cuandoNoHay_devuelveListaVacia() {
        when(billingTariffRepository.findAll()).thenReturn(List.of());

        List<BillingTariffResponse> response = service.getTariffs();

        assertTrue(response.isEmpty());
    }
}
