package personal.doctor_ms.exceptions;

public class SpecialtyNotFoundException extends RuntimeException {

    public SpecialtyNotFoundException(Long id) {
        super("Specialty not found with id: " + id);
    }

    public SpecialtyNotFoundException(String name) {
        super("Specialty not found with name: " + name);
    }
}