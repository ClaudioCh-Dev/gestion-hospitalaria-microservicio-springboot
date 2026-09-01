package personal.shared.event;

public record DoctorUpdateEvent(
    Long   doctorId,
    String licenseNumber,
    String firstName,
    String lastName,
    String email,
    String phone,
    Long   userId,
    String specialty
) {}