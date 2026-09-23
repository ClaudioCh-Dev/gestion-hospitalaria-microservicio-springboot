package personal.shared.docs;

/**
 * Ejemplos JSON (ProblemDetail) de los errores genéricos que devuelven
 * los GlobalExceptionHandler de todos los microservicios.
 *
 * Son constantes String para poder usarlas dentro de anotaciones
 * de OpenAPI (@ExampleObject) sin que este módulo dependa de Swagger.
 */
public final class ErrorExamples {

    private ErrorExamples() {
    }

    /** Content-Type con el que Spring serializa un ProblemDetail. */
    public static final String PROBLEM_JSON = "application/problem+json";

    // =========================================================
    // 400
    // =========================================================

    public static final String VALIDATION_ERROR = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "firstName: El nombre es obligatorio",
              "instance": "/crud",
              "code": "VALIDATION_ERROR"
            }
            """;

    public static final String INVALID_REQUEST_BODY = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "El cuerpo de la petición no tiene un formato válido",
              "instance": "/crud",
              "code": "INVALID_REQUEST_BODY"
            }
            """;

    public static final String INVALID_PARAMETER = """
            {
              "type": "about:blank",
              "title": "Bad Request",
              "status": 400,
              "detail": "El parámetro 'id' tiene un formato inválido",
              "instance": "/crud/abc",
              "code": "INVALID_PARAMETER"
            }
            """;

    // =========================================================
    // 403
    // =========================================================

    public static final String ACCESS_DENIED = """
            {
              "type": "about:blank",
              "title": "Forbidden",
              "status": 403,
              "detail": "No tienes permisos para realizar esta operación",
              "instance": "/crud",
              "code": "ACCESS_DENIED"
            }
            """;

    // =========================================================
    // 409
    // =========================================================

    public static final String DATA_INTEGRITY_ERROR = """
            {
              "type": "about:blank",
              "title": "Conflict",
              "status": 409,
              "detail": "No se pudo completar la operación debido a una restricción de datos",
              "instance": "/crud",
              "code": "DATA_INTEGRITY_ERROR"
            }
            """;

    // =========================================================
    // 500
    // =========================================================

    public static final String INTERNAL_ERROR = """
            {
              "type": "about:blank",
              "title": "Internal Server Error",
              "status": 500,
              "detail": "Ocurrió un error interno en el servidor",
              "instance": "/crud",
              "code": "INTERNAL_ERROR"
            }
            """;
}
