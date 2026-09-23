package personal.doctor_ms.docs;

/**
 * Ejemplos JSON usados en la documentación OpenAPI de doctor-ms.
 */
public final class DoctorExamples {

    private DoctorExamples() {
    }

    // =========================================================
    // REQUESTS
    // =========================================================

    public static final String CREATE_DOCTOR_REQUEST = """
            {
              "licenseNumber": "CMP-045872",
              "firstName": "Carlos",
              "lastName": "Ramírez",
              "email": "carlos.ramirez@hospital.com",
              "phone": "912345678",
              "specialtyId": 1,
              "scheduleStart": "08:00:00",
              "scheduleEnd": "14:00:00"
            }
            """;

    public static final String UPDATE_DOCTOR_REQUEST = """
            {
              "firstName": "Carlos",
              "lastName": "Ramírez",
              "email": "carlos.ramirez@hospital.com",
              "phone": "912345678",
              "specialtyId": 1,
              "scheduleStart": "09:00:00",
              "scheduleEnd": "15:00:00",
              "active": true
            }
            """;

    public static final String CREATE_SPECIALTY_REQUEST = """
            {
              "name": "Cardiología",
              "description": "Diagnóstico y tratamiento de enfermedades del corazón"
            }
            """;

    // =========================================================
    // RESPONSES
    // =========================================================

    public static final String DOCTOR_RESPONSE = """
            {
              "id": 3,
              "licenseNumber": "CMP-045872",
              "firstName": "Carlos",
              "lastName": "Ramírez",
              "email": "carlos.ramirez@hospital.com",
              "phone": "912345678",
              "userId": 12,
              "specialtyId": 1,
              "specialtyName": "Cardiología",
              "scheduleStart": "08:00:00",
              "scheduleEnd": "14:00:00",
              "active": true
            }
            """;

    public static final String DOCTOR_PAGE = """
            {
              "content": [
                {
                  "id": 3,
                  "licenseNumber": "CMP-045872",
                  "firstName": "Carlos",
                  "lastName": "Ramírez",
                  "email": "carlos.ramirez@hospital.com",
                  "phone": "912345678",
                  "userId": 12,
                  "specialtyId": 1,
                  "specialtyName": "Cardiología",
                  "scheduleStart": "08:00:00",
                  "scheduleEnd": "14:00:00",
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

    public static final String SPECIALTY_RESPONSE = """
            {
              "id": 1,
              "name": "Cardiología",
              "description": "Diagnóstico y tratamiento de enfermedades del corazón"
            }
            """;

    public static final String SPECIALTY_LIST = """
            [
              {
                "id": 1,
                "name": "Cardiología",
                "description": "Diagnóstico y tratamiento de enfermedades del corazón"
              },
              {
                "id": 2,
                "name": "Pediatría",
                "description": "Atención médica de niños y adolescentes"
              }
            ]
            """;

    // =========================================================
    // ERRORS
    // =========================================================

    public static final String DOCTOR_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Doctor no encontrado",
              "instance": "/crud/99",
              "code": "DOCTOR_NOT_FOUND"
            }
            """;

    public static final String SPECIALTY_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Especialidad no encontrada",
              "instance": "/crud",
              "code": "SPECIALTY_NOT_FOUND"
            }
            """;

    public static final String DOCTOR_ALREADY_EXISTS = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "El correo electrónico ya está registrado",
              "instance": "/crud",
              "code": "DOCTOR_ALREADY_EXISTS"
            }
            """;

    public static final String AUTH_SERVICE_UNAVAILABLE = """
            {
              "type": "about:blank",
              "title": "Service Unavailable",
              "status": 503,
              "detail": "Auth Server temporalmente no disponible",
              "instance": "/crud",
              "code": "AUTH_SERVICE_UNAVAILABLE"
            }
            """;
}
