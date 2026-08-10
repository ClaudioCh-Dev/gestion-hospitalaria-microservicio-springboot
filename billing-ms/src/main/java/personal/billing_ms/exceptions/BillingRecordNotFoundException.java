package personal.billing_ms.exceptions;

public class BillingRecordNotFoundException extends RuntimeException {

    public BillingRecordNotFoundException(Long id) {
        super("Billing record not found with id: " + id);
    }
}