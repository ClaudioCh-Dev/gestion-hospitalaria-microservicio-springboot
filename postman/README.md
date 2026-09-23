# 📬 Postman

| Archivo | Qué es |
|---|---|
| `gestion-hospitalaria.postman_collection.json` | Colección con todos los endpoints (68 peticiones), ejemplos de respuesta y scripts |
| `gestion-hospitalaria-local.postman_environment.json` | Entorno con las URLs para Docker local (opcional: la colección ya trae esas URLs como variables) |

## Importar

Postman → **Import** → arrastra los dos archivos → selecciona el entorno *Gestión Hospitalaria - Local (Docker)* (arriba a la derecha).

## Uso

1. Levanta el proyecto (ver sección "Cómo levantar el proyecto" del [README principal](../README.md)).
2. Ejecuta **00 · Auth → Login (admin)**. Guarda `access_token` y `refresh_token` en las variables de la colección; el resto de peticiones envían `Bearer {{access_token}}` automáticamente.
3. Los *Create* guardan el id generado (`patient_id`, `doctor_id`, `appointment_id`, `billing_id`, …) y las peticiones siguientes lo reutilizan.
4. Cada petición tiene ejemplos de respuesta (éxito y errores `400/403/404/409/503`) en la pestaña **Examples**.

## Carpetas

| Carpeta | Servicio | Ruta en el gateway |
|---|---|---|
| 00 · Auth | auth-server | `/auth-server/auth/**` |
| 01 · Users | auth-server | `/auth-server/users/**` |
| 02 · Doctors & Specialties | doctor-ms | `/doctors/**` |
| 03 · Patients | patient-ms | `/patients/**` |
| 04 · Appointment Types | appointment-ms | `/appointments/appointment-types/**` |
| 05 · Billing Tariffs | billing-ms | `/billings/tariffs/**` |
| 06 · Appointments | appointment-ms | `/appointments/crud/**` |
| 07 · Billing | billing-ms | `/billings/crud/**` |
| 08 · Notifications | notification-ms | `/notifications/**` (incluye SSE) |
| 09 · Medical Records | medical-record-listener | `/medical-records/**` |
| 10 · Flujo completo | todos | login → paciente → cita → confirmar → completar → pagar → historial clínico |

La carpeta **10 · Flujo completo** está pensada para el **Collection Runner** (clic derecho → *Run folder*). También se puede ejecutar por consola con [Newman](https://www.npmjs.com/package/newman):

```bash
npx newman run postman/gestion-hospitalaria.postman_collection.json --folder "10 · Flujo completo (Runner)"
```

## Variables principales

| Variable | Por defecto | Nota |
|---|---|---|
| `url_gateway` | `http://localhost:4040` | |
| `url_auth_server` | `http://localhost:4040` | Usa `http://localhost:3000` para ir directo al auth-server |
| `admin_email` / `admin_password` | `admin@example.com` / `123456` | Usuario del `DataSeeder` |
| `access_token` / `refresh_token` | — | Los llena Login |
| `doctor_id`, `specialty_id`, `appointment_type_id` | `1` | Existen desde `sql/data.sql` |
