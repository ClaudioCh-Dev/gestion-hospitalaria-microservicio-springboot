package personal.billing_ms.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import personal.billing_ms.client.AppointmentClient;
import personal.billing_ms.dto.AppointmentResponse;
import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingRecord;
import personal.billing_ms.entities.BillingStatus;
import personal.billing_ms.exceptions.BillingRecordNotFoundException;
import personal.billing_ms.repositories.BillingRepository;
import personal.shared.event.AppointmentEvent;

@Service
@RequiredArgsConstructor
public class BillingRecordServiceImpl implements IBillingRecordService {

    private final BillingRepository billingRepository;
    private final AppointmentClient appointmentClient;

    @Override
    @Transactional
    public BillingRecord createBilling(CreateBillingRequest request) {

        AppointmentResponse appointment = appointmentClient.findById(request.appointmentId());

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
                .orElseThrow(() -> new BillingRecordNotFoundException(id));

        billingRecord.setStatus(BillingStatus.PAID);
        billingRecord.setPaidAt(LocalDateTime.now());

        return billingRepository.save(billingRecord);
    }

    // TODO: Mejorar la lógica para crear factura desde evento de cita
    @Override
    @Transactional
    public BillingRecord createBillingFromAppointment(AppointmentEvent event) {

        BillingRecord billingRecord = new BillingRecord();

        billingRecord.setAppointmentId(event.appointmentId());
        billingRecord.setPatientId(event.patientId());
        billingRecord.setAmount(BigDecimal.valueOf(100.0));
        billingRecord.setStatus(BillingStatus.PENDING);
        billingRecord.setIssuedAt(LocalDateTime.now());

        return billingRepository.save(billingRecord);
    }
}