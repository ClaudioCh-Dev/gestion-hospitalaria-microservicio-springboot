package personal.appointment_ms.exceptions;

public class InvalidAppointmentStatusException extends RuntimeException {

    public InvalidAppointmentStatusException(String status) {
        super("Invalid appointment status: " + status);
    }
}