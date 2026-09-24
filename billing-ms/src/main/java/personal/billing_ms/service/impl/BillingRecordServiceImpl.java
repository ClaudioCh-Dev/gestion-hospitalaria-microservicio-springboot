package personal.billing_ms.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import personal.billing_ms.client.AppointmentClient;
import personal.billing_ms.client.dto.AppointmentResponse;
import personal.billing_ms.dto.AppointmentEventRequest;
import personal.billing_ms.dto.BillingRecordResponse;
import personal.billing_ms.dto.BillingSummaryResponse;
import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingRecord;
import personal.billing_ms.entities.BillingStatus;
import personal.billing_ms.exceptions.BillingErrorCode;
import personal.billing_ms.repositories.BillingRecordSpecifications;
import personal.billing_ms.repositories.BillingRepository;
import personal.billing_ms.service.IBillingRecordService;
import personal.billing_ms.streams.PaymentPublisher;
import personal.shared.event.PaymentUpdateStatus;
import personal.shared.event.status.StatusPayment;
import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class BillingRecordServiceImpl
        implements IBillingRecordService {

    private final BillingRepository billingRepository;
    private final AppointmentClient appointmentClient;
    private final PaymentPublisher paymentPublisher;

    @Override
    @Transactional
    public BillingRecordResponse createBilling(
            CreateBillingRequest request
    ) {
        AppointmentResponse appointment =
                appointmentClient.findById(request.appointmentId());

        BillingRecord billingRecord = new BillingRecord();

        billingRecord.setAppointmentId(request.appointmentId());
        billingRecord.setPatientId(appointment.patientId());
        billingRecord.setAmount(request.amount());
        billingRecord.setStatus(BillingStatus.PENDING);
        billingRecord.setIssuedAt(LocalDateTime.now());

        return toResponse(billingRepository.save(billingRecord));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillingRecordResponse> getBillingByPatient(
            Long patientId,
            Pageable pageable
    ) {
        return billingRepository
                .findByPatientId(patientId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillingRecordResponse> getBillings(
            Pageable pageable,
            BillingStatus status,
            String search,
            List<Long> patientIds
    ) {
        return billingRepository
                .findAll(
                        BillingRecordSpecifications.filter(status, search, patientIds),
                        pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BillingSummaryResponse getSummary() {

        Map<BillingStatus, BillingRepository.StatusTotal> totals =
                billingRepository.totalsByStatus()
                        .stream()
                        .collect(Collectors.toMap(
                                BillingRepository.StatusTotal::getStatus,
                                Function.identity()));

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        BigDecimal paidThisMonth =
                billingRepository.sumByStatusSince(BillingStatus.PAID, monthStart);

        long pendingCount = countOf(totals, BillingStatus.PENDING);
        long paidCount = countOf(totals, BillingStatus.PAID);
        long cancelledCount = countOf(totals, BillingStatus.CANCELLED);

        BigDecimal pendingAmount = amountOf(totals, BillingStatus.PENDING);
        BigDecimal paidAmount = amountOf(totals, BillingStatus.PAID);
        BigDecimal cancelledAmount = amountOf(totals, BillingStatus.CANCELLED);

        return new BillingSummaryResponse(
                pendingCount + paidCount + cancelledCount,
                pendingAmount.add(paidAmount).add(cancelledAmount),
                pendingCount,
                pendingAmount,
                paidCount,
                paidAmount,
                cancelledCount,
                cancelledAmount,
                paidThisMonth);
    }

    private long countOf(
            Map<BillingStatus, BillingRepository.StatusTotal> totals,
            BillingStatus status) {

        BillingRepository.StatusTotal total = totals.get(status);

        return total == null ? 0 : total.getCount();
    }

    private BigDecimal amountOf(
            Map<BillingStatus, BillingRepository.StatusTotal> totals,
            BillingStatus status) {

        BillingRepository.StatusTotal total = totals.get(status);

        return total == null ? BigDecimal.ZERO : total.getAmount();
    }

    @Override
    @Transactional
    public BillingRecordResponse payBilling(Long id) {

        BillingRecord billingRecord =
                billingRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                BillingErrorCode.BILLING_RECORD_NOT_FOUND,
                                "Registro de facturación no encontrado"
                        ));

        if (billingRecord.getStatus() == BillingStatus.PAID) {
            throw new BusinessException(
                    BillingErrorCode.BILLING_RECORD_ALREADY_PAID,
                    "El registro de facturación ya se encuentra pagado"
            );
        }

        if (billingRecord.getStatus() == BillingStatus.CANCELLED) {
            throw new BusinessException(
                    BillingErrorCode.BILLING_RECORD_ALREADY_CANCELLED,
                    "El registro de facturación ya se encuentra cancelado"
            );
        }

        billingRecord.setStatus(BillingStatus.PAID);
        billingRecord.setPaidAt(LocalDateTime.now());

        paymentPublisher.publishPaymentUpdateStatus(
                new PaymentUpdateStatus(
                        billingRecord.getId(),
                        billingRecord.getAppointmentId(),
                        billingRecord.getAmount(),
                        "PEN",
                        StatusPayment.PAID,
                        billingRecord.getIssuedAt(),
                        billingRecord.getPaidAt()
                )
        );

        return toResponse(billingRepository.save(billingRecord));
    }

    @Override
    @Transactional
    public BillingRecordResponse createBillingFromAppointment(
            AppointmentEventRequest event
    ) {
        BillingRecord billingRecord = new BillingRecord();

        billingRecord.setAppointmentId(event.appointmentId());
        billingRecord.setPatientId(event.patientId());
        billingRecord.setAmount(event.amount());
        billingRecord.setStatus(BillingStatus.PENDING);
        billingRecord.setIssuedAt(LocalDateTime.now());

        return toResponse(billingRepository.save(billingRecord));
    }

    @Override
    @Transactional
    public BillingRecordResponse cancelBillingRecord(Long appointmentId) {

        BillingRecord billingRecord =
                billingRepository.findById(appointmentId)
                        .orElseThrow(() -> new BusinessException(
                                BillingErrorCode.BILLING_RECORD_NOT_FOUND,
                                "Registro de facturación no encontrado"
                        ));

        if (billingRecord.getStatus() == BillingStatus.PAID) {
            throw new BusinessException(
                    BillingErrorCode.BILLING_RECORD_ALREADY_PAID,
                    "El registro de facturación ya se encuentra pagado"
            );
        }

        if (billingRecord.getStatus() == BillingStatus.CANCELLED) {
            throw new BusinessException(
                    BillingErrorCode.BILLING_RECORD_ALREADY_CANCELLED,
                    "El registro de facturación ya se encuentra cancelado"
            );
        }

        billingRecord.setStatus(BillingStatus.CANCELLED);

        paymentPublisher.publishPaymentUpdateStatus(
                new PaymentUpdateStatus(
                        billingRecord.getId(),
                        billingRecord.getAppointmentId(),
                        billingRecord.getAmount(),
                        "PEN",
                        StatusPayment.CANCELLED,
                        billingRecord.getIssuedAt(),
                        null
                )
        );

        return toResponse(billingRepository.save(billingRecord));
    }

    private BillingRecordResponse toResponse(
            BillingRecord billing
    ) {
        return new BillingRecordResponse(
                billing.getId(),
                billing.getAppointmentId(),
                billing.getPatientId(),
                billing.getAmount(),
                billing.getCurrency(),
                billing.getStatus(),
                billing.getIssuedAt(),
                billing.getPaidAt()
        );
    }
}