package personal.billing_ms.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import personal.billing_ms.dto.BillingTariffResponse;
import personal.billing_ms.dto.CreateBillingTariffRequest;
import personal.billing_ms.dto.UpdateBillingTariffRequest;
import personal.billing_ms.service.IBillingTariffService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tariffs")
public class BillingTariffController {

    private final IBillingTariffService billingTariffService;

    @PostMapping
    @PreAuthorize("@auth.hasPermission('BILLING_TARIFF_CREATE')")
    public ResponseEntity<BillingTariffResponse> createTariff(
            @Valid @RequestBody CreateBillingTariffRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(billingTariffService.createTariff(request));
    }

    @PutMapping("/{appointmentTypeId}")
    @PreAuthorize("@auth.hasPermission('BILLING_TARIFF_UPDATE')")
    public ResponseEntity<BillingTariffResponse> updateTariff(
            @PathVariable Long appointmentTypeId,
            @Valid @RequestBody UpdateBillingTariffRequest request) {

        return ResponseEntity.ok(
                billingTariffService.updateTariff(
                        appointmentTypeId,
                        request));
    }

    @GetMapping
    @PreAuthorize("@auth.hasPermission('BILLING_TARIFF_READ')")
    public ResponseEntity<List<BillingTariffResponse>> getTariffs() {

        return ResponseEntity.ok(
                billingTariffService.getTariffs());
    }

    @GetMapping("/{appointmentTypeId}")
    @PreAuthorize("@auth.hasPermission('BILLING_TARIFF_READ')")
    public ResponseEntity<BillingTariffResponse> getTariff(
            @PathVariable Long appointmentTypeId) {

        return ResponseEntity.ok(
                billingTariffService.getTariff(
                        appointmentTypeId));
    }

    @GetMapping("/{appointmentTypeId}/price")
    @PreAuthorize("@auth.hasPermission('BILLING_TARIFF_READ')")
    public ResponseEntity<BigDecimal> getPriceByAppointmentTypeId(
            @PathVariable Long appointmentTypeId) {

        return ResponseEntity.ok(
                billingTariffService.getPriceByAppointmentTypeId(
                        appointmentTypeId));
    }
}