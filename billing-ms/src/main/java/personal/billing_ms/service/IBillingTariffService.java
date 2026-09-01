package personal.billing_ms.service;

import java.math.BigDecimal;
import java.util.List;

import personal.billing_ms.dto.BillingTariffResponse;
import personal.billing_ms.dto.CreateBillingTariffRequest;
import personal.billing_ms.dto.UpdateBillingTariffRequest;

public interface IBillingTariffService {

    BillingTariffResponse createTariff(
            CreateBillingTariffRequest request);

    BillingTariffResponse updateTariff(
            Long appointmentTypeId,
            UpdateBillingTariffRequest request);

    List<BillingTariffResponse> getTariffs();

    BillingTariffResponse getTariff(
            Long appointmentTypeId);

    BigDecimal getPriceByAppointmentTypeId(
            Long appointmentTypeId);
}