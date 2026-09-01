package personal.appointment_ms.entities;

public enum AppointmentStatus {

    SCHEDULED(false),
    CONFIRMED(false),
    COMPLETED(true),
    CANCELLED(true);

    private final boolean finalStatus;

    AppointmentStatus(boolean finalStatus) {
        this.finalStatus = finalStatus;
    }

    public boolean isFinal() {
        return finalStatus;
    }
}