package personal.appointment_ms.exceptions;

public class DoctorServiceUnavailableException
        extends RuntimeException {

    public DoctorServiceUnavailableException(String message) {
        super(message);
    }
}