package personal.billing_ms.service;

import java.util.List;

import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingRecord;
import personal.shared.event.AppointmentEventRequest;

public interface IBillingRecordService {

    BillingRecord createBilling(CreateBillingRequest request);

    List<BillingRecord> getBillingByPatient(Long patientId);

    List<BillingRecord> getBillings();

    BillingRecord payBilling(Long id);
    
    BillingRecord cancelBillingRecord(Long appointmentId);
    
    BillingRecord createBillingFromAppointment(AppointmentEventRequest event);
}