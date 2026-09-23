package personal.billing_ms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import personal.billing_ms.dto.AppointmentEventRequest;
import personal.billing_ms.dto.BillingRecordResponse;
import personal.billing_ms.dto.CreateBillingRequest;

public interface IBillingRecordService {

    BillingRecordResponse createBilling(CreateBillingRequest request);

    Page<BillingRecordResponse> getBillings(Pageable pageable);

    Page<BillingRecordResponse> getBillingByPatient(
            Long patientId,
            Pageable pageable);

    BillingRecordResponse payBilling(Long id);

    BillingRecordResponse cancelBillingRecord(Long appointmentId);

    BillingRecordResponse createBillingFromAppointment(AppointmentEventRequest event);
}