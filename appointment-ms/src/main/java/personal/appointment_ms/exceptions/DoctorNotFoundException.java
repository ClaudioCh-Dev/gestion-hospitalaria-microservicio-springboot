package personal.appointment_ms.exceptions;

public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException() {
        super("Doctor not found");
    }
}