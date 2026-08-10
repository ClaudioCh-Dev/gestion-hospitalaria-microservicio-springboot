package personal.appointment_ms.exceptions;

public class AppointmentTypeAlreadyInactiveException extends RuntimeException {

    public AppointmentTypeAlreadyInactiveException(Long id) {
        super("Appointment type is already inactive with id: " + id);
    }
}