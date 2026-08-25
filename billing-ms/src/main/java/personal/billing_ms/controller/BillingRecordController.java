package personal.billing_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import personal.billing_ms.dto.CreateBillingRequest;
import personal.billing_ms.entities.BillingRecord;
import personal.billing_ms.service.IBillingRecordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class BillingRecordController {

    private final IBillingRecordService billingRecordService;

    @PostMapping
    public ResponseEntity<BillingRecord> createBilling(
            @Valid @RequestBody CreateBillingRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(billingRecordService.createBilling(request));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillingRecord>> getBillingByPatient(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                billingRecordService.getBillingByPatient(patientId)
        );
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<BillingRecord> payBilling(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                billingRecordService.payBilling(id)
        );
    }
    
    @GetMapping
    public ResponseEntity<List<BillingRecord>> getBillings() {
        return ResponseEntity.ok(billingRecordService.getBillings());
    }
}
