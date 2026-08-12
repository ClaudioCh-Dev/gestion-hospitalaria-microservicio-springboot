package personal.medical_record_listener.exceptions;

public class MedicalRecordNotFoundException extends RuntimeException {

    public MedicalRecordNotFoundException(Long patientId) {
        super("Medical records not found for patient with id: " + patientId);
    }
}
