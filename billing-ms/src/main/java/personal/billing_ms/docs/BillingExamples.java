package personal.billing_ms.docs;

/**
 * Ejemplos JSON usados en la documentación OpenAPI de billing-ms.
 */
public final class BillingExamples {

    private BillingExamples() {
    }

    // =========================================================
    // REQUESTS
    // =========================================================

    public static final String CREATE_BILLING_REQUEST = """
            {
              "appointmentId": 10,
              "amount": 80.00
            }
            """;

    public static final String CREATE_TARIFF_REQUEST = """
            {
              "appointmentTypeId": 2,
              "price": 80.00,
              "currency": "PEN"
            }
            """;

    public static final String UPDATE_TARIFF_REQUEST = """
            {
              "price": 95.00,
              "currency": "PEN"
            }
            """;

    // =========================================================
    // RESPONSES
    // =========================================================

    public static final String BILLING_RECORD_RESPONSE = """
            {
              "id": 5,
              "appointmentId": 10,
              "patientId": 1,
              "amount": 80.00,
              "currency": "PEN",
              "status": "PENDING",
              "issuedAt": "2026-09-23T17:45:00",
              "paidAt": null
            }
            """;

    public static final String BILLING_RECORD_PAID = """
            {
              "id": 5,
              "appointmentId": 10,
              "patientId": 1,
              "amount": 80.00,
              "currency": "PEN",
              "status": "PAID",
              "issuedAt": "2026-09-23T17:45:00",
              "paidAt": "2026-09-24T09:10:00"
            }
            """;

    public static final String BILLING_RECORD_PAGE = """
            {
              "content": [
                {
                  "id": 5,
                  "appointmentId": 10,
                  "patientId": 1,
                  "amount": 80.00,
                  "currency": "PEN",
                  "status": "PENDING",
                  "issuedAt": "2026-09-23T17:45:00",
                  "paidAt": null
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

    public static final String TARIFF_RESPONSE = """
            {
              "appointmentTypeId": 2,
              "price": 80.00,
              "currency": "PEN"
            }
            """;

    public static final String TARIFF_LIST = """
            [
              {
                "appointmentTypeId": 1,
                "price": 120.00,
                "currency": "PEN"
              },
              {
                "appointmentTypeId": 2,
                "price": 80.00,
                "currency": "PEN"
              }
            ]
            """;

    public static final String PRICE_RESPONSE = "80.00";

    // =========================================================
    // ERRORS
    // =========================================================

    public static final String BILLING_RECORD_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Registro de facturación no encontrado",
              "instance": "/crud/99/pay",
              "code": "BILLING_RECORD_NOT_FOUND"
            }
            """;

    public static final String APPOINTMENT_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Cita no encontrada",
              "instance": "/crud",
              "code": "APPOINTMENT_NOT_FOUND"
            }
            """;

    public static final String BILLING_RECORD_ALREADY_PAID = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "El registro de facturación ya se encuentra pagado",
              "instance": "/crud/5/pay",
              "code": "BILLING_RECORD_ALREADY_PAID"
            }
            """;

    public static final String BILLING_RECORD_ALREADY_CANCELLED = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "El registro de facturación ya se encuentra cancelado",
              "instance": "/crud/5/pay",
              "code": "BILLING_RECORD_ALREADY_CANCELLED"
            }
            """;

    public static final String BILLING_TARIFF_NOT_FOUND = """
            {
              "type": "about:blank",
              "title": "Not Found",
              "status": 404,
              "detail": "Tarifa no encontrada para el tipo de cita: 99",
              "instance": "/tariffs/99",
              "code": "BILLING_TARIFF_NOT_FOUND"
            }
            """;

    public static final String BILLING_TARIFF_ALREADY_EXISTS = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "Ya existe una tarifa para el tipo de cita: 2",
              "instance": "/tariffs",
              "code": "BILLING_TARIFF_ALREADY_EXISTS"
            }
            """;

    public static final String APPOINTMENT_SERVICE_UNAVAILABLE = """
            {
              "type": "about:blank",
              "title": "Service Unavailable",
              "status": 503,
              "detail": "Appointment MS temporalmente no disponible",
              "instance": "/crud",
              "code": "APPOINTMENT_SERVICE_UNAVAILABLE"
            }
            """;
}
