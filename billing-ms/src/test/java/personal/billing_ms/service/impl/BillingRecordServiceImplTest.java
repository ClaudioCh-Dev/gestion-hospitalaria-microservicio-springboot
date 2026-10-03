package personal.billing_ms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import org.springframework.data.jpa.domain.Specification;

import personal.billing_ms.client.AppointmentClient;
import personal.billing_ms.client.dto.AppointmentResponse;
import personal.billing_ms.dto.AppointmentEventRequest;
import personal.billing_ms.dto.BillingRecordResponse;
import personal.billing_ms.dto.BillingSummaryResponse;
import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingRecord;
import personal.billing_ms.entities.BillingStatus;
import personal.billing_ms.exceptions.BillingErrorCode;
import personal.billing_ms.repositories.BillingRepository;
import personal.billing_ms.streams.PaymentPublisher;
import personal.shared.event.PaymentUpdateStatus;
import personal.shared.event.status.StatusPayment;
import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class BillingRecordServiceImplTest {

    // El servicio tiene 3 dependencias, así que hay 3 mocks
    @Mock
    private BillingRepository billingRepository;

    @Mock // cliente Feign: en vez de llamar por HTTP a appointment-ms, responde lo que digamos
    private AppointmentClient appointmentClient;

    @Mock // publicador de Kafka: no envía nada, solo registra que lo llamaron
    private PaymentPublisher paymentPublisher;

    @InjectMocks
    private BillingRecordServiceImpl service;

    // Factura existente con el estado que necesite cada test
    private BillingRecord facturaConEstado(BillingStatus status) {
        return BillingRecord.builder()
                .id(10L)
                .appointmentId(5L)
                .patientId(3L)
                .amount(new BigDecimal("80.00"))
                .status(status)
                .issuedAt(LocalDateTime.of(2026, 10, 1, 9, 0))
                .build();
    }

    // ---------- createBilling ----------
    @Test
    void createBilling_tomaPacienteDeLaCitaYQuedaPendiente() {
        // La cita "viene" de appointment-ms: el mock del cliente Feign la devuelve
        AppointmentResponse cita = new AppointmentResponse(
                5L, 3L, 7L, null, 30, "Control", "CONFIRMED", null, null);
        when(appointmentClient.findById(5L)).thenReturn(cita);
        when(billingRepository.save(any(BillingRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BillingRecordResponse response =
                service.createBilling(new CreateBillingRequest(5L, new BigDecimal("80.00")));

        assertEquals(5L, response.appointmentId());
        assertEquals(3L, response.patientId()); // sale de la cita, no del request
        assertEquals(new BigDecimal("80.00"), response.amount());
        assertEquals(BillingStatus.PENDING, response.status());
        assertNotNull(response.issuedAt()); // usa now(): solo revisamos que se haya puesto
    }

    // ---------- createBillingFromAppointment ----------
    @Test
    void createBillingFromAppointment_creaFacturaPendienteConDatosDelEvento() {
        when(billingRepository.save(any(BillingRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BillingRecordResponse response = service.createBillingFromAppointment(
                new AppointmentEventRequest(5L, 3L, new BigDecimal("80.00")));

        assertEquals(5L, response.appointmentId());
        assertEquals(3L, response.patientId());
        assertEquals(BillingStatus.PENDING, response.status());
        assertNotNull(response.issuedAt());
    }

    // ---------- payBilling ----------
    @Test
    void payBilling_cuandoPendiente_marcaPagadaYPublicaEvento() {
        BillingRecord factura = facturaConEstado(BillingStatus.PENDING);
        when(billingRepository.findById(10L)).thenReturn(Optional.of(factura));
        when(billingRepository.save(any(BillingRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BillingRecordResponse response = service.payBilling(10L);

        assertEquals(BillingStatus.PAID, response.status());
        assertNotNull(response.paidAt());

        // Atrapamos el evento que se mandó a Kafka y revisamos su contenido
        ArgumentCaptor<PaymentUpdateStatus> captor =
                ArgumentCaptor.forClass(PaymentUpdateStatus.class);
        verify(paymentPublisher).publishPaymentUpdateStatus(captor.capture());
        PaymentUpdateStatus evento = captor.getValue();
        assertEquals(10L, evento.billingId());
        assertEquals(5L, evento.appointmentId());
        assertEquals(StatusPayment.PAID, evento.status());
        assertEquals(response.paidAt(), evento.paidAt());
    }

    @Test
    void payBilling_cuandoNoExiste_lanzaNotFound() {
        when(billingRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.payBilling(99L));

        assertEquals(BillingErrorCode.BILLING_RECORD_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
        verify(paymentPublisher, never()).publishPaymentUpdateStatus(any());
        verify(billingRepository, never()).save(any());
    }

    @Test
    void payBilling_cuandoYaPagada_lanzaErrorYNoPublica() {
        when(billingRepository.findById(10L))
                .thenReturn(Optional.of(facturaConEstado(BillingStatus.PAID)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.payBilling(10L));

        assertEquals(BillingErrorCode.BILLING_RECORD_ALREADY_PAID.toString(), ex.getCode());
        assertEquals(400, ex.getStatus());
        verify(paymentPublisher, never()).publishPaymentUpdateStatus(any());
        verify(billingRepository, never()).save(any());
    }

    @Test
    void payBilling_cuandoCancelada_lanzaErrorYNoPublica() {
        when(billingRepository.findById(10L))
                .thenReturn(Optional.of(facturaConEstado(BillingStatus.CANCELLED)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.payBilling(10L));

        assertEquals(BillingErrorCode.BILLING_RECORD_ALREADY_CANCELLED.toString(), ex.getCode());
        verify(paymentPublisher, never()).publishPaymentUpdateStatus(any());
        verify(billingRepository, never()).save(any());
    }

    // ---------- cancelBillingRecord: recibe el id de la CITA, no el de la factura ----------
    @Test
    void cancelBillingRecord_cuandoPendiente_marcaCanceladaYPublicaEvento() {
        BillingRecord factura = facturaConEstado(BillingStatus.PENDING);
        when(billingRepository.findByAppointmentId(5L)).thenReturn(Optional.of(factura));
        when(billingRepository.save(any(BillingRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BillingRecordResponse response = service.cancelBillingRecord(5L);

        assertEquals(BillingStatus.CANCELLED, response.status());
        assertNull(response.paidAt()); // cancelar no debe poner fecha de pago

        ArgumentCaptor<PaymentUpdateStatus> captor =
                ArgumentCaptor.forClass(PaymentUpdateStatus.class);
        verify(paymentPublisher).publishPaymentUpdateStatus(captor.capture());
        assertEquals(StatusPayment.CANCELLED, captor.getValue().status());
        assertNull(captor.getValue().paidAt());
    }

    @Test
    void cancelBillingRecord_cuandoNoExiste_lanzaNotFound() {
        when(billingRepository.findByAppointmentId(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.cancelBillingRecord(99L));

        assertEquals(BillingErrorCode.BILLING_RECORD_NOT_FOUND.toString(), ex.getCode());
        verify(paymentPublisher, never()).publishPaymentUpdateStatus(any());
    }

    @Test
    void cancelBillingRecord_cuandoYaPagada_lanzaErrorYNoPublica() {
        when(billingRepository.findByAppointmentId(5L))
                .thenReturn(Optional.of(facturaConEstado(BillingStatus.PAID)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.cancelBillingRecord(5L));

        assertEquals(BillingErrorCode.BILLING_RECORD_ALREADY_PAID.toString(), ex.getCode());
        verify(paymentPublisher, never()).publishPaymentUpdateStatus(any());
        verify(billingRepository, never()).save(any());
    }

    @Test
    void cancelBillingRecord_cuandoYaCancelada_lanzaErrorYNoPublica() {
        when(billingRepository.findByAppointmentId(5L))
                .thenReturn(Optional.of(facturaConEstado(BillingStatus.CANCELLED)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.cancelBillingRecord(5L));

        assertEquals(BillingErrorCode.BILLING_RECORD_ALREADY_CANCELLED.toString(), ex.getCode());
        verify(paymentPublisher, never()).publishPaymentUpdateStatus(any());
        verify(billingRepository, never()).save(any());
    }

    // ---------- Paginación: getBillingByPatient / getBillings ----------
    @Test
    void getBillingByPatient_devuelvePaginaConvertidaAResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        // PageImpl es una página "de verdad" armada a mano: contenido + pageable + total de registros
        Page<BillingRecord> pagina = new PageImpl<>(
                List.of(facturaConEstado(BillingStatus.PENDING)), pageable, 1);
        when(billingRepository.findByPatientId(3L, pageable)).thenReturn(pagina);

        Page<BillingRecordResponse> response = service.getBillingByPatient(3L, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(10L, response.getContent().get(0).id());
        assertEquals(BillingStatus.PENDING, response.getContent().get(0).status());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getBillings_pasaPaginacionAlRepositorioYConvierteResultado() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<BillingRecord> pagina = new PageImpl<>(
                List.of(facturaConEstado(BillingStatus.PAID)), pageable, 6);
        // La Specification se arma dentro del servicio: no tenemos esa instancia, por eso any()
        when(billingRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pagina);

        Page<BillingRecordResponse> response =
                service.getBillings(pageable, BillingStatus.PAID, null, null);

        assertEquals(6, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertEquals(BillingStatus.PAID, response.getContent().get(0).status());
    }

    // ---------- getSummary: proyección StatusTotal ----------

    // StatusTotal es una interfaz (proyección de Spring Data): la creamos con mock() en vez de "new"
    private BillingRepository.StatusTotal total(BillingStatus status, long count, String amount) {
        BillingRepository.StatusTotal total = mock(BillingRepository.StatusTotal.class);
        when(total.getStatus()).thenReturn(status);
        when(total.getCount()).thenReturn(count);
        when(total.getAmount()).thenReturn(new BigDecimal(amount));
        return total;
    }

    @Test
    void getSummary_sumaCantidadesYMontosDeLosTresEstados() {
        // Los totales se arman ANTES del when: no se puede configurar un mock dentro de otro when
        List<BillingRepository.StatusTotal> totales = List.of(
                total(BillingStatus.PENDING, 2, "100.00"),
                total(BillingStatus.PAID, 3, "240.00"),
                total(BillingStatus.CANCELLED, 1, "80.00"));
        when(billingRepository.totalsByStatus()).thenReturn(totales);
        when(billingRepository.sumByStatusSince(eq(BillingStatus.PAID), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("160.00"));

        BillingSummaryResponse summary = service.getSummary();

        assertEquals(6, summary.totalCount());
        assertEquals(new BigDecimal("420.00"), summary.totalAmount());
        assertEquals(2, summary.pendingCount());
        assertEquals(new BigDecimal("240.00"), summary.paidAmount());
        assertEquals(1, summary.cancelledCount());
        assertEquals(new BigDecimal("160.00"), summary.paidThisMonthAmount());
    }

    @Test
    void getSummary_cuandoFaltaUnEstado_lotomaComoCero() {
        // Solo hay pendientes: PAID y CANCELLED no vienen en la consulta
        List<BillingRepository.StatusTotal> totales = List.of(
                total(BillingStatus.PENDING, 2, "100.00"));
        when(billingRepository.totalsByStatus()).thenReturn(totales);
        when(billingRepository.sumByStatusSince(eq(BillingStatus.PAID), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ZERO);

        BillingSummaryResponse summary = service.getSummary();

        assertEquals(2, summary.totalCount());
        assertEquals(new BigDecimal("100.00"), summary.totalAmount());
        assertEquals(0, summary.paidCount());
        assertEquals(BigDecimal.ZERO, summary.paidAmount());
        assertEquals(0, summary.cancelledCount());
    }

    @Test
    void getSummary_consultaPagadoDesdeElPrimerDiaDelMes() {
        when(billingRepository.totalsByStatus()).thenReturn(List.of());
        when(billingRepository.sumByStatusSince(eq(BillingStatus.PAID), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ZERO);

        service.getSummary();

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(billingRepository).sumByStatusSince(eq(BillingStatus.PAID), captor.capture());
        LocalDateTime desde = captor.getValue();
        assertEquals(LocalDate.now().withDayOfMonth(1).atStartOfDay(), desde);
    }
}
