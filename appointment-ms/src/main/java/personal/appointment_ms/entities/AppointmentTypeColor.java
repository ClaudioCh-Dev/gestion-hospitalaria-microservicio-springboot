package personal.appointment_ms.entities;

/**
 * Gamas de color disponibles para los tipos de cita.
 * El frontend traduce cada clave a su paleta (fondo de la cita en la agenda).
 */
public final class AppointmentTypeColor {

    public static final String DEFAULT = "blue";

    public static final String PATTERN =
            "^(blue|emerald|violet|amber|rose|cyan|indigo|orange|teal|pink)$";

    public static final String MESSAGE =
            "Color no válido. Valores permitidos: blue, emerald, violet, amber, rose, cyan, indigo, orange, teal, pink";

    private AppointmentTypeColor() {
    }
}
