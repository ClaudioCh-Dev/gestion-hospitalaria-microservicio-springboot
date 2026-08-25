package personal.billing_ms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import personal.billing_ms.client.AppointmentClient;
import personal.billing_ms.dto.AppointmentEventRequest;
import personal.billing_ms.dto.AppointmentResponse;
import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingRecord;
import personal.billing_ms.entities.BillingStatus;
import personal.billing_ms.exceptions.BillingErrorCode;
import personal.billing_ms.repositories.BillingRepository;
import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class BillingRecordServiceImpl implements IBillingRecordService {

    private final BillingRepository billingRepository;
    private final AppointmentClient appointmentClient;

    // TODO: Mejorar la lógica para crear factura desde request.
    // El monto debería venir del servicio de citas.
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

        billingRecord.setStatus(BillingStatus.PAID);
        billingRecord.setPaidAt(LocalDateTime.now());

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
}