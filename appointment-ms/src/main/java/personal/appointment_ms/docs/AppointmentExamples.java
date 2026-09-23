package personal.appointment_ms.docs;

/**
 * Ejemplos JSON usados en la documentación OpenAPI de appointment-ms.
 */
public final class AppointmentExamples {

    private AppointmentExamples() {
    }

    // =========================================================
    // REQUESTS
    // =========================================================

    public static final String CREATE_APPOINTMENT_REQUEST = """
            {
              "patientId": 1,
              "doctorId": 3,
              "appointmentTypeId": 2,
              "scheduledAt": "2026-10-05T10:30:00",
              "durationMinutes": 30,
              "reason": "Dolor de cabeza persistente",
              "notes": "Paciente con antecedentes de migraña"
            }
            """;

    public static final String UPDATE_STATUS_REQUEST = """
            {
              "status": "CONFIRMED"
            }
            """;

    public static final String CREATE_APPOINTMENT_TYPE_REQUEST = """
            {
              "title": "Consulta general",
              "description": "Atención médica general de 30 minutos",
              "price": 80.00
            }
            """;

    public static final String UPDATE_APPOINTMENT_TYPE_REQUEST = """
            {
              "title": "Consulta general",
              "description": "Atención médica general de 45 minutos",
              "active": true
            }
            """;

    // =========================================================
    // RESPONSES
    // =========================================================

    public static final String APPOINTMENT_RESPONSE = """
            {
              "id": 10,
              "patientId": 1,
              "doctorId": 3,
              "scheduledAt": "2026-10-05T10:30:00",
              "durationMinutes": 30,
              "reason": "Dolor de cabeza persistente",
              "status": "SCHEDULED",
              "notes": "Paciente con antecedentes de migraña",
              "createdAt": "2026-09-23T17:45:00"
            }
            """;

    public static final String APPOINTMENT_LIST = """
            [
              {
                "id": 10,
                "patientId": 1,
                "doctorId": 3,
                "scheduledAt": "2026-10-05T10:30:00",
                "durationMinutes": 30,
                "reason": "Dolor de cabeza persistente",
                "status": "SCHEDULED",
                "notes": "Paciente con antecedentes de migraña",
                "createdAt": "2026-09-23T17:45:00"
              }
            ]
            """;

    public static final String APPOINTMENT_PAGE = """
            {
              "content": [
                {
                  "id": 10,
                  "patientId": 1,
                  "doctorId": 3,
                  "scheduledAt": "2026-10-05T10:30:00",
                  "durationMinutes": 30,
                  "reason": "Dolor de cabeza persistente",
                  "status": "SCHEDULED",
                  "notes": "Paciente con antecedentes de migraña",
                  "createdAt": "2026-09-23T17:45:00"
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
                "pageSize": 10,
                "paged": true,
                "sort": { "empty": true, "sorted": false, "unsorted": true },
                "unpaged": false
              },
              "size": 10,
              "sort": { "empty": true, "sorted": false, "unsorted": true },
              "totalElements": 1,
              "totalPages": 1
            }
            """;

    public static final String APPOINTMENT_TYPE_RESPONSE = """
            {
              "id": 2,
              "title": "Consulta general",
              "description": "Atención médica general de 30 minutos",
              "active": true
            }
            """;

    public static final String APPOINTMENT_TYPE_CREATED = """
            {
              "id": 2,
              "title": "Consulta general",
              "description": "Atención médica general de 30 minutos",
              "active": false
            }
            """;

    public static final String APPOINTMENT_TYPE_LIST = """
            [
              {
                "id": 1,
                "title": "Control prenatal",
                "description": "Seguimiento del embarazo",
                "active": true
              },
              {
                "id": 2,
                "title": "Consulta general",
                "description": "Atención médica general de 30 minutos",
                "active": true
              }
            ]
            """;

    // =========================================================
    // ERRORS
    // =========================================================

    public static final String APPOINTMENT_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Cita no encontrada",
              "instance": "/crud/99",
              "code": "APPOINTMENT_NOT_FOUND"
            }
            """;

    public static final String PATIENT_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Paciente no encontrado",
              "instance": "/crud",
              "code": "PATIENT_NOT_FOUND"
            }
            """;

    public static final String DOCTOR_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Doctor no encontrado",
              "instance": "/crud",
              "code": "DOCTOR_NOT_FOUND"
            }
            """;

    public static final String APPOINTMENT_TYPE_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Tipo de cita no encontrado",
              "instance": "/appointment-types/99",
              "code": "APPOINTMENT_TYPE_NOT_FOUND"
            }
            """;

    public static final String DOCTOR_NOT_AVAILABLE = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "El doctor ya tiene una cita en ese horario",
              "instance": "/crud",
              "code": "DOCTOR_NOT_AVAILABLE"
            }
            """;

    public static final String APPOINTMENT_TYPE_NOT_ACTIVE = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "El tipo de cita no está activo",
              "instance": "/crud",
              "code": "APPOINTMENT_TYPE_NOT_ACTIVE"
            }
            """;

    public static final String APPOINTMENT_TARIFF_INVALID = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "Establece la tarifa para este tipo de cita en gestion Billing",
              "instance": "/crud",
              "code": "APPOINTMENT_TARIFF_INVALID"
            }
            """;

    public static final String APPOINTMENT_STATUS_ALREADY_SET = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "No se puede cambiar el estado de la cita a el mismo estado",
              "instance": "/crud/10/status",
              "code": "APPOINTMENT_STATUS_ALREADY_SET"
            }
            """;

    public static final String APPOINTMENT_STATUS_CANNOT_CHANGE = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "No se puede cambiar el estado de una cita completada o cancelada.",
              "instance": "/crud/10/status",
              "code": "APPOINTMENT_STATUS_CANNOT_CHANGE"
            }
            """;

    public static final String APPOINTMENT_TYPE_ALREADY_INACTIVE = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "El tipo de cita ya está inactivo",
              "instance": "/appointment-types/2",
              "code": "APPOINTMENT_TYPE_ALREADY_INACTIVE"
            }
            """;

    public static final String PATIENT_SERVICE_UNAVAILABLE = """
            {
              "type": "about:blank",
              "title": "Service Unavailable",
              "status": 503,
              "detail": "Patient MS temporalmente no disponible",
              "instance": "/crud",
              "code": "PATIENT_SERVICE_UNAVAILABLE"
            }
            """;

    public static final String DOCTOR_SERVICE_UNAVAILABLE = """
            {
              "type": "about:blank",
              "title": "Service Unavailable",
              "status": 503,
              "detail": "Doctor MS temporalmente no disponible",
              "instance": "/crud",
              "code": "DOCTOR_SERVICE_UNAVAILABLE"
            }
            """;

    public static final String BILLING_SERVICE_UNAVAILABLE = """
            {
              "type": "about:blank",
              "title": "Service Unavailable",
              "status": 503,
              "detail": "Billing MS temporalmente no disponible",
              "instance": "/crud",
              "code": "BILLING_SERVICE_UNAVAILABLE"
            }
            """;
}
