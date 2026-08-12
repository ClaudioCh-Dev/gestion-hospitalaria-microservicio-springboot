package personal.appointment_ms.exceptions;

public class PatientServiceUnavailableException extends RuntimeException {

    public PatientServiceUnavailableException(String message) {
        super(message);
    }
}