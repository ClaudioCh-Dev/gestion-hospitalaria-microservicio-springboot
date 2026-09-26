package personal.notification_ms.docs;

/**
 * Ejemplos JSON usados en la documentación OpenAPI de notification-ms.
 */
public final class NotificationExamples {

    private NotificationExamples() {
    }

    // =========================================================
    // REQUESTS
    // =========================================================

    public static final String NOTIFICATION_REQUEST = """
            {
              "type": "APPOINTMENT_SCHEDULED",
              "title": "Nueva cita programada",
              "message": "María Gonzales tiene una cita con Carlos Ramírez el 2026-10-05 a las 10:30",
              "referenceType": "APPOINTMENT",
              "referenceId": 10,
              "metadata": {
                "patientName": "María Gonzales",
                "doctorName": "Carlos Ramírez",
                "specialty": "Cardiología"
              },
              "createdAt": "2026-09-23T17:45:00"
            }
            """;

    // =========================================================
    // RESPONSES
    // =========================================================

    public static final String NOTIFICATION_RESPONSE = """
            {
              "id": 25,
              "type": "APPOINTMENT_SCHEDULED",
              "title": "Cita programada",
              "message": "Se ha creado una nueva cita para María Gonzales con el doctor Carlos Ramírez",
              "referenceType": "APPOINTMENT",
              "referenceId": 10,
              "doctorId": 3,
              "patientName": "María Gonzales",
              "doctorName": "Carlos Ramírez",
              "specialty": "Cardiología",
              "appointmentStatus": "SCHEDULED",
              "reason": "Dolor de cabeza persistente",
              "scheduledAt": "2026-10-05T10:30:00",
              "read": false,
              "createdAt": "2026-09-23T17:45:00"
            }
            """;

    public static final String NOTIFICATION_LIST = """
            [
              {
                "id": 25,
                "type": "APPOINTMENT_SCHEDULED",
                "title": "Cita programada",
                "message": "Se ha creado una nueva cita para María Gonzales con el doctor Carlos Ramírez",
                "referenceType": "APPOINTMENT",
                "referenceId": 10,
                "doctorId": 3,
                "patientName": "María Gonzales",
                "doctorName": "Carlos Ramírez",
                "specialty": "Cardiología",
                "appointmentStatus": "SCHEDULED",
                "reason": "Dolor de cabeza persistente",
                "scheduledAt": "2026-10-05T10:30:00",
                "read": false,
                "createdAt": "2026-09-23T17:45:00"
              }
            ]
            """;

    /** Formato text/event-stream: cada evento se llama "notification". */
    public static final String SSE_EVENT = """
            event:notification
            data:{"id":25,"type":"APPOINTMENT_SCHEDULED","title":"Cita programada","message":"Se ha creado una nueva cita para María Gonzales con el doctor Carlos Ramírez","referenceType":"APPOINTMENT","referenceId":10,"doctorId":3,"patientName":"María Gonzales","doctorName":"Carlos Ramírez","specialty":"Cardiología","appointmentStatus":"SCHEDULED","reason":"Dolor de cabeza persistente","scheduledAt":"2026-10-05T10:30:00","read":false,"createdAt":"2026-09-23T17:45:00"}

            """;
}
