package personal.appointment_ms.exceptions;

public class AppointmentTypeNotFoundException extends RuntimeException {

    public AppointmentTypeNotFoundException(Long id) {
        super("Appointment type not found with id: " + id);
    }
}