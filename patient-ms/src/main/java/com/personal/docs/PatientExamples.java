package com.personal.docs;

/**
 * Ejemplos JSON usados en la documentación OpenAPI de patient-ms.
 */
public final class PatientExamples {

    private PatientExamples() {
    }

    // =========================================================
    // REQUESTS
    // =========================================================

    public static final String PATIENT_REQUEST = """
            {
              "documentNumber": "74859612",
              "firstName": "María",
              "lastName": "Gonzales",
              "birthDate": "1990-05-14",
              "gender": "FEMALE",
              "phone": "987654321",
              "email": "maria.gonzales@mail.com",
              "address": "Av. Arequipa 123, Lima",
              "bloodType": "O_POSITIVE",
              "allergies": "Penicilina"
            }
            """;

    // =========================================================
    // RESPONSES
    // =========================================================

    public static final String PATIENT_RESPONSE = """
            {
              "id": 1,
              "documentNumber": "74859612",
              "firstName": "María",
              "lastName": "Gonzales",
              "gender": "FEMALE",
              "birthDate": "1990-05-14",
              "phone": "987654321",
              "email": "maria.gonzales@mail.com",
              "active": true
            }
            """;

    public static final String PATIENT_DETAIL_RESPONSE = """
            {
              "id": 1,
              "documentNumber": "74859612",
              "firstName": "María",
              "lastName": "Gonzales",
              "birthDate": "1990-05-14",
              "gender": "FEMALE",
              "phone": "987654321",
              "email": "maria.gonzales@mail.com",
              "address": "Av. Arequipa 123, Lima",
              "bloodType": "O_POSITIVE",
              "allergies": "Penicilina",
              "active": true,
              "createdAt": "2026-01-10T09:15:00",
              "updatedAt": "2026-02-01T16:40:00"
            }
            """;

    public static final String PATIENT_PAGE = """
            {
              "content": [
                {
                  "id": 1,
                  "documentNumber": "74859612",
                  "firstName": "María",
                  "lastName": "Gonzales",
                  "gender": "FEMALE",
                  "birthDate": "1990-05-14",
                  "phone": "987654321",
                  "email": "maria.gonzales@mail.com",
                  "active": true
                }
              ],
              "empty": false,
              "first": true,
              "last": true,
              "number": 0,
              "numberOfElements": 1,
              "pageable": {
                "offset": 0,
                "pageNumber": 0,
                "pageSize": 20,
                "paged": true,
                "sort": { "empty": true, "sorted": false, "unsorted": true },
                "unpaged": false
              },
              "size": 20,
              "sort": { "empty": true, "sorted": false, "unsorted": true },
              "totalElements": 1,
              "totalPages": 1
            }
            """;

    // =========================================================
    // ERRORS
    // =========================================================

    public static final String PATIENT_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Paciente no encontrado",
              "instance": "/crud/99",
              "code": "PATIENT_NOT_FOUND"
            }
            """;

    public static final String PATIENT_DOCUMENT_ALREADY_EXISTS = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "El número de documento ya está registrado",
              "instance": "/crud",
              "code": "PATIENT_DOCUMENT_ALREADY_EXISTS"
            }
            """;

    public static final String PATIENT_EMAIL_ALREADY_EXISTS = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "El correo electrónico ya está registrado",
              "instance": "/crud",
              "code": "PATIENT_EMAIL_ALREADY_EXISTS"
            }
            """;
}
