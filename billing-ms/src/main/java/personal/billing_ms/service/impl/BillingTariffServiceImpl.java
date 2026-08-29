package personal.billing_ms.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import personal.billing_ms.dto.BillingTariffResponse;
import personal.billing_ms.dto.CreateBillingTariffRequest;
import personal.billing_ms.dto.UpdateBillingTariffRequest;
import personal.billing_ms.entities.BillingTariff;
import personal.billing_ms.repositories.BillingTariffRepository;
import personal.billing_ms.service.IBillingTariffService;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingTariffServiceImpl implements IBillingTariffService {

    private final BillingTariffRepository billingTariffRepository;

    @Override
    public BillingTariffResponse createTariff(
            CreateBillingTariffRequest request) {

        if (billingTariffRepository.existsById(
                request.appointmentTypeId())) {

            throw new IllegalArgumentException(
                    "A tariff already exists for appointment type: "
                            + request.appointmentTypeId());
        }

        BillingTariff tariff = BillingTariff.builder()
                .billingAppointmentTypeId(request.appointmentTypeId())
                .price(request.price())
                .currency(request.currency())
                .build();

        return toResponse(
                billingTariffRepository.save(tariff));
    }

    @Override
    public BillingTariffResponse updateTariff(
            Long appointmentTypeId,
            UpdateBillingTariffRequest request) {

        BillingTariff tariff = billingTariffRepository
                .findById(appointmentTypeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tariff not found for appointment type: "
                                + appointmentTypeId));

        tariff.setPrice(request.price());
        tariff.setCurrency(request.currency());

        return toResponse(
                billingTariffRepository.save(tariff));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingTariffResponse> getTariffs() {

        return billingTariffRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BillingTariffResponse getTariff(
            Long appointmentTypeId) {

        BillingTariff tariff = billingTariffRepository
                .findById(appointmentTypeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tariff not found for appointment type: "
                                + appointmentTypeId));

        return toResponse(tariff);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getPriceByAppointmentTypeId(
            Long appointmentTypeId) {

        return billingTariffRepository
                .findById(appointmentTypeId)
                .map(tariff -> tariff.getPrice())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tariff not found for appointment type: "
                                + appointmentTypeId));
    }

    private BillingTariffResponse toResponse(
            BillingTariff tariff) {

        return new BillingTariffResponse(
                tariff.getBillingAppointmentTypeId(),
                tariff.getPrice(),
                tariff.getCurrency());
    }
}