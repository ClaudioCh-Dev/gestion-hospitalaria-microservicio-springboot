package personal.billing_ms.service;

import java.util.List;

import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingRecord;
import personal.shared.event.AppointmentEvent;

public interface IBillingRecordService {

    BillingRecord createBilling(CreateBillingRequest request);

    List<BillingRecord> getBillingByPatient(Long patientId);

    BillingRecord payBilling(Long id);
    
    BillingRecord createBillingFromAppointment(AppointmentEvent event);
}