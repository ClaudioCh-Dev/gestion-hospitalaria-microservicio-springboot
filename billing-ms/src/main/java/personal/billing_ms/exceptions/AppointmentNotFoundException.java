package personal.billing_ms.exceptions;

public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException() {
        super("Appointment not found");
    }
}