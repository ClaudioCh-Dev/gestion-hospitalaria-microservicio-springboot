package personal.billing_ms.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import personal.billing_ms.client.AppointmentClient;
import personal.billing_ms.client.dto.AppointmentResponse;
import personal.billing_ms.dto.AppointmentEventRequest;
import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingRecord;
import personal.billing_ms.entities.BillingStatus;
import personal.billing_ms.exceptions.BillingErrorCode;
import personal.billing_ms.repositories.BillingRepository;
import personal.billing_ms.service.IBillingRecordService;
import personal.billing_ms.streams.PaymentPublisher;
import personal.shared.event.status.StatusPayment;
import personal.shared.event.PaymentUpdateStatus;
import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class BillingRecordServiceImpl implements IBillingRecordService {

    private final BillingRepository billingRepository;
    private final AppointmentClient appointmentClient;
    private final PaymentPublisher paymentPublisher;

    @Override
    @Transactional
    public BillingRecord createBilling(CreateBillingRequest request) {

        AppointmentResponse appointment =
                appointmentClient.findById(request.appointmentId());

        BillingRecord billingRecord = new BillingRecord();

        billingRecord.setAppointmentId(request.appointmentId());
        billingRecord.setPatientId(appointment.patientId());
        billingRecord.setAmount(request.amount());
        billingRecord.setStatus(BillingStatus.PENDING);
        billingRecord.setIssuedAt(LocalDateTime.now());

        return billingRepository.save(billingRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingRecord> getBillingByPatient(Long patientId) {

        return billingRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional
    public BillingRecord payBilling(Long id) {

        BillingRecord billingRecord = billingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        BillingErrorCode.BILLING_RECORD_NOT_FOUND,
                        "Registro de facturación no encontrado"
                ));
        
        if(billingRecord.getStatus() == BillingStatus.PAID) {
            throw new BusinessException(
                    BillingErrorCode.BILLING_RECORD_ALREADY_PAID,
                    "El registro de facturación ya se encuentra pagado"
            );
        }
        if(billingRecord.getStatus() == BillingStatus.CANCELLED) {
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
                        StatusPayment.valueOf(BillingStatus.PAID.name()),
                        billingRecord.getIssuedAt(),
                        billingRecord.getPaidAt()
                )
                
        );

        return billingRepository.save(billingRecord);
    }

    @Override
    @Transactional
    public BillingRecord createBillingFromAppointment(
            AppointmentEventRequest event) {

        BillingRecord billingRecord = new BillingRecord();

        billingRecord.setAppointmentId(event.appointmentId());
        billingRecord.setPatientId(event.patientId());
        billingRecord.setAmount(event.amount());
        billingRecord.setStatus(BillingStatus.PENDING);
        billingRecord.setIssuedAt(LocalDateTime.now());

        return billingRepository.save(billingRecord);
    }

    @Override
    public List<BillingRecord> getBillings() {
        return billingRepository.findAll();
    }

    @Override
    public BillingRecord cancelBillingRecord(Long appointmentId) {
        
        BillingRecord billingRecord = billingRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(
                        BillingErrorCode.BILLING_RECORD_NOT_FOUND,
                        "Registro de facturación no encontrado"
                ));

        if(billingRecord.getStatus() == BillingStatus.PAID) {
            throw new BusinessException(
                    BillingErrorCode.BILLING_RECORD_ALREADY_PAID,
                    "El registro de facturación ya se encuentra pagado"
            );
        }
        if(billingRecord.getStatus() == BillingStatus.CANCELLED) {
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
                        StatusPayment.valueOf(BillingStatus.CANCELLED.name()),
                        billingRecord.getIssuedAt(),
                        null
                )
        );
        
        return billingRepository.save(billingRecord);
    }
}