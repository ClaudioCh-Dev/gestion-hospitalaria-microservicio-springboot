package personal.medical_record_listener.docs;

/**
 * Ejemplos JSON usados en la documentación OpenAPI de medical-record-listener.
 */
public final class MedicalRecordExamples {

    private MedicalRecordExamples() {
    }

    // =========================================================
    // RESPONSES
    // =========================================================

    public static final String MEDICAL_RECORD_PAGE = """
            {
              "content": [
                {
                  "id": "66f1c2a9e4b0a12b3c4d5e6f",
                  "appointmentId": 10,
                  "patientId": 1,
                  "patientName": "María Gonzales",
                  "doctorId": 3,
                  "doctorName": "Carlos Ramírez",
                  "specialty": "Cardiología",
                  "scheduledAt": "2026-10-05T10:30:00",
                  "reason": "Dolor de cabeza persistente",
                  "status": "COMPLETED",
                  "amount": 80.00
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

    public static final String MEDICAL_RECORD_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Historial médico no encontrado para el paciente",
              "instance": "/crud/patient/99",
              "code": "MEDICAL_RECORD_NOT_FOUND"
            }
            """;
}
