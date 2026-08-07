package com.personal.exceptions;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("Patient not found with id: " + id);
    }

    public PatientNotFoundException(String documentNumber) {
        super("Patient not found with document number: " + documentNumber);
    }
}
