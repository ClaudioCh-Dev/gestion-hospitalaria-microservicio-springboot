package personal.billing_ms.controller;

import personal.billing_ms.docs.BillingRecordApiDocs;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

import personal.billing_ms.dto.BillingRecordResponse;
import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.service.IBillingRecordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class BillingRecordController implements BillingRecordApiDocs {

    private final IBillingRecordService billingRecordService;

    @Override
    @PostMapping
    @PreAuthorize("@auth.hasPermission('BILLING_CREATE')")
    public ResponseEntity<BillingRecordResponse> createBilling(
            @Valid @RequestBody CreateBillingRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(billingRecordService.createBilling(request));
    }

    @Override
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("@auth.hasPermission('BILLING_READ_BY_PATIENT')")
    public ResponseEntity<Page<BillingRecordResponse>> getBillingByPatient(
            @PathVariable Long patientId,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                billingRecordService.getBillingByPatient(
                        patientId,
                        pageable
                )
        );
    }

    @Override
    @GetMapping
    @PreAuthorize("@auth.hasPermission('BILLING_READ')")
    public ResponseEntity<Page<BillingRecordResponse>> getBillings(
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                billingRecordService.getBillings(pageable)
        );
    }

    @Override
    @PatchMapping("/{id}/pay")
    @PreAuthorize("@auth.hasPermission('BILLING_PAY')")
    public ResponseEntity<BillingRecordResponse> payBilling(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                billingRecordService.payBilling(id)
        );
    }
}