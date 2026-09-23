# Arquitectura técnica

Documento de referencia técnica del sistema, basado en el código real del repositorio (no en documentos de diseño previos). Para una descripción general no técnica, ver el [`README.md`](../README.md).

> Nota: existen en esta misma carpeta `hospital-ms-concepto.md` y `hospital-ms-guide.md`, documentos de **planificación previos a la implementación**. Varias cosas que describen (servicio `patient-ms-fallback` separado, puertos 7080/7090, Jaeger, roles RECEPTIONIST/NURSE) ya no reflejan el código actual y no deben tomarse como referencia — este documento sí.

---

## 1. Módulos y puertos

Todos los microservicios de dominio corren internamente en el puerto **8081** (Spring Boot) y se diferencian por `context-path`; en Docker Compose cada uno se expone en un puerto de host distinto.

| Módulo | Puerto interno | Puerto host (compose) | Context-path |
|---|---|---|---|
| `eureka-server` | 8761 | 8761 | — |
| `config-server` | 7777 | 7777 | — |
| `auth-server` | 3000 | 3000 | `/auth-server` |
| `gateway` | 4040 | 4040 | — |
| `patient-ms` | 8081 | 8081 | `/patients` |
| `doctor-ms` | 8081 | 8082 | `/doctors` |
| `appointment-ms` | 8081 | 8083 | `/appointments` |
| `billing-ms` | 8081 | 8084 | `/billings` |
| `notification-ms` | 8081 | 8085 | `/notifications` |
| `medical-record-listener` | 8081 | 8086 | `/medical-records` |
| `hospital-shared` | — | — | librería Maven local, sin puerto (eventos de Kafka + manejo de errores compartido) |

Stack común a todos: **Spring Boot 4.1.0**, **Spring Cloud 2025.1.2**, **Java 17**.

---

## 2. Endpoints REST por servicio

Rutas relativas a cada `context-path` (ver tabla anterior). Todas pasan por el gateway usando el path público correspondiente (sección 10).

**appointment-ms**
- `AppointmentController` (`/crud`): `POST /crud`, `GET /crud`, `GET /crud/{id}`, `GET /crud/patient/{patientId}`, `GET /crud/doctor/{doctorId}`, `GET /crud/date/{date}`, `PATCH /crud/{id}/status`, `DELETE /crud/{id}`
- `AppointmentTypeController` (`/appointment-types`): `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`

**billing-ms**
- `BillingRecordController` (`/crud`): `POST /crud`, `GET /crud/patient/{patientId}`, `GET /crud`, `PATCH /crud/{id}/pay`
- `BillingTariffController` (`/tariffs`): `POST`, `PUT /{appointmentTypeId}`, `GET`, `GET /{appointmentTypeId}`, `GET /{appointmentTypeId}/price`

**doctor-ms**
- `DoctorController` (`/crud`): `GET`, `GET /{id}`, `GET /specialty/{specialtyId}`, `POST`, `PUT /{id}`
- `SpecialtyController` (`/specialties`): `GET`, `POST`

**patient-ms**
- `PatientController` (`/crud`): `GET`, `GET /{id}`, `GET /document/{documentNumber}`, `POST`, `PUT /{id}`, `DELETE /{id}`

**notification-ms**
- `NotificationController` (`/crud`): `POST`, `GET /doctor/{doctorId}`, `GET /admin`, `PATCH /{notificationId}/read`
- `NotificationSseController`: `GET /stream` (Server-Sent Events, `text/event-stream` — notificaciones en tiempo real)

**medical-record-listener**
- `MedicalRecordController` (`/crud`): `GET /patient/{patientId}`, `GET`
- No expone `POST`: los registros se crean solo a partir de eventos de Kafka (ver sección 3).

**auth-server**
- `AuthController` (`/auth`): `POST /login`, `POST /validate-jwt`, `POST /refresh-token`, `POST /logout`
- `UserController` (`/users`): `GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`, `POST /doctor`, `POST /activate`, `POST /resend-activation`, `POST /change-password-me`
- `JwksController`: `GET /.well-known/jwks.json` (clave pública para validar JWT)

**gateway**
- `FallbackController`: `/fallback` (todos los métodos) — respuesta 503 con `code=SERVICE_UNAVAILABLE`, usada por el circuit breaker del gateway cuando un microservicio no responde.

---

## 3. Kafka — tópicos y flujo de eventos

Todos los servicios usan **Spring Cloud Stream** (binder de Kafka), no `spring-kafka` directo. Los eventos son `record` de Java definidos en `hospital-shared` (`AppointmentCreatedEvent`, `AppointmentCreatedTypeEvent`, `AppointmentUpdateStatusEvent`, `DoctorCreatedEvent`, `DoctorUpdateEvent`, `PatientCreateEvent`, `PatientUpdateEvent`, `PaymentUpdateStatus`, `MedicalRecordReadyEvent`), junto con los enums `StatusAppointment` (`SCHEDULED`/`CONFIRMED`/`COMPLETED`/`CANCELLED`) y `StatusPayment` (`PENDING`/`PAID`/`CANCELLED`).

| Tópico | Productor | Consumidor(es) |
|---|---|---|
| `patient-created` | patient-ms | appointment-ms, notification-ms |
| `patient-updated` | patient-ms | appointment-ms |
| `doctor-created` | doctor-ms | appointment-ms |
| `doctor-updated` | doctor-ms | appointment-ms |
| `appointment-created` | appointment-ms | billing-ms, notification-ms, medical-record-listener |
| `appointment-updated-status` | appointment-ms | billing-ms, notification-ms, medical-record-listener |
| `appointment-created-type` | appointment-ms | billing-ms |
| `payment-update-status` | billing-ms | medical-record-listener |
| `appointment-state` | medical-record-listener (interno, Kafka Streams) | medical-record-listener (mismo servicio) |
| `medical-record-ready` | medical-record-listener (Kafka Streams) | ⚠️ ver nota |

> **Corregido:** `appointmentStateProcessor` y `medicalRecordReadyConsumer` no estaban en `spring.cloud.function.definition` de `application.yaml` (solo `medicalRecordProcessor` lo estaba), y a `medicalRecordReadyConsumer` le faltaba su binding (`destination`/`group`). Esto dejaba inactivas 2 de las 3 etapas del pipeline: nunca se generaba `appointment-state` y, aunque se generara, nada guardaba el resultado en MongoDB. El servicio arrancaba sin errores pero no procesaba nada. Ya está arreglado — las 3 funciones están registradas y con su binding correspondiente.

### Por qué appointment-ms guarda copias de médicos y pacientes

`appointment-ms` mantiene tablas locales de solo lectura (`doctors_appointment`, `patients_appointment`) que se sincronizan escuchando `patient-created/updated` y `doctor-created/updated`. Así, para mostrar el nombre de un médico o paciente en una cita no necesita llamar por Feign cada vez — solo usa Feign para las validaciones que sí requieren el dato más fresco posible (ej. al crear la cita).

### Kafka Streams en medical-record-listener

El historial clínico no lo crea nadie manualmente: se arma solo, combinando dos flujos de eventos con Kafka Streams (`BiFunction` con topología KStream/KTable):

1. **`appointmentStateProcessor`** — une `appointment-created` con `appointment-updated-status` para mantener el estado más reciente de cada cita → publica a `appointment-state`.
2. **`medicalRecordProcessor`** — cruza citas en estado `COMPLETED` (de `appointment-state`) con pagos en estado `PAID` (de `payment-update-status`). Cuando ambas condiciones se cumplen para la misma cita, publica un `MedicalRecordReadyEvent` a `medical-record-ready`, que dispara la creación del documento en MongoDB.

### Contexto de usuario en los eventos

Cada mensaje de Kafka lleva los headers `X-User-Id`, `X-Role`, `X-Permissions` — el mismo contexto de autorización que viaja en las peticiones HTTP (ver sección 5) se propaga también a los eventos asíncronos, y se reconstruye en el consumidor.

---

## 4. Bases de datos

### PostgreSQL `hospital_db` (host `5434`, `sql/create-schema.sql`)
Compartida por `patient-ms`, `doctor-ms`, `appointment-ms`, `billing-ms` (cada uno dueño de sus tablas, sin acceso cruzado).

- Enums: `gender` (MALE/FEMALE), `blood_type` (8 valores), `appointment_status`, `billing_status`
- `patients` — id, document_number (único), first_name, last_name, birth_date, gender, phone, email (único), address, blood_type, allergies, active, created_at, updated_at
- `specialties` — id, name (único), description
- `doctors` — id, user_id, license_number (único), first_name, last_name, email (único), phone, specialty_id → specialties, schedule_start, schedule_end, active, created_at
- `appointment_types` — id, title (único), description, active
- `doctors_appointment` / `patients_appointment` — réplicas locales de solo lectura en appointment-ms (id, full_name[, specialty]), alimentadas por Kafka (ver sección 3)
- `appointments` — id, patient_id, doctor_id, scheduled_at, duration_minutes, reason, status, notes, appointment_type_id → appointment_types, created_at (índices por patient_id, doctor_id, appointment_type_id)
- `billing_tariffs` — billing_appointment_type_id (PK), price, currency
- `billing_records` — id, appointment_id (único), patient_id, amount, currency, status, issued_at, paid_at (índices por patient_id, appointment_id)

### MySQL `notification_db` (host `3307`, `sql/notification-schema.sql`)
Solo `notification-ms`.

- `notifications` — id, type, title, message, reference_type, reference_id, created_at
- `notification_recipients` — id, notification_id → notifications (cascade), user_id, `read`, read_at (índices por reference, user_id, notification_id, user_id+read)

### MongoDB `medical_records` (puerto `27017`)
Solo `medical-record-listener`. Colección `medical_records`, documento `MedicalRecord`: `_id`, `appointmentId` (único, indexado), `patientId`, `patientName`, `doctorId`, `doctorName`, `specialty`, `scheduledAt`, `reason`, `status`, `amount`.

### H2 en memoria — `auth-server`
Base `jdbc:h2:mem:users`, **no persistente entre reinicios** (con consola habilitada para desarrollo). Entidades: `UserEntity`, `RoleEntity`, `PermissionEntity` (modelo roles↔permisos).

### Redis
Solo `auth-server` — almacena los refresh tokens.

---

## 5. Seguridad

**Modelo:** JWT firmado con clave **RSA** propia (par de claves generado por `RsaKeyConfig` en auth-server) + autorización **granular por permisos** (no por rol directamente), evaluada con `@PreAuthorize("@auth.hasPermission('X')")`. El bean `AuthorizationService` (expuesto como `@Component("auth")`) está **duplicado de forma idéntica en cada microservicio** — no es una librería compartida, cada servicio evalúa sus propios permisos localmente.

**Flujo de una petición autenticada:**

1. El cliente hace login contra `POST /auth-server/auth/login` → recibe un JWT.
2. El cliente llama a cualquier otro endpoint a través del gateway, con el JWT en el header `Authorization`.
3. El **gateway** (perfil `oauth2`) valida el JWT contra las claves públicas de `auth-server` (`GET /auth-server/.well-known/jwks.json`), usando el mecanismo estándar de OAuth2 Resource Server de Spring.
4. El `AuthFilter` del gateway extrae del JWT los claims `userId`, `role` y `permissions`, y los reinyecta como headers `X-User-Id`, `X-Role`, `X-Permissions` en la petición que reenvía al microservicio — **eliminando primero cualquier valor que el cliente haya intentado enviar en esos headers**, para que no se puedan falsificar.
5. Cada microservicio tiene un `UserContextFilter` que lee esos headers y arma un `UserContext` (en un `ThreadLocal`, vía `UserContextHolder`), que es lo que consulta `AuthorizationService` en cada `@PreAuthorize`.
6. Ese mismo contexto se propaga también a los eventos de Kafka que dispara la petición (ver sección 3), así que un consumidor de un evento sabe "en nombre de quién" se originó.

**Rutas públicas en el gateway** (no requieren JWT): `/auth-server/**` y `/fallback/**`. Todo lo demás exige un JWT válido.

**Tokens:** access token con expiración de **900s (15 min)**, refresh token de **604800s (7 días)**. El refresh token viaja en la cookie `refresh_token` (HttpOnly, `path=/auth-server/auth/refresh-token`); `Secure` y `SameSite` se configuran con `auth.refresh-token.cookie.*` (por defecto `true` / `Strict`; en desarrollo `REFRESH_COOKIE_SECURE=false` o perfil `dev` del Config Server).

**Roles sembrados actualmente** (`auth-server/.../config/DataSeeder.java`): solo **ADMIN** y **DOCTOR** tienen datos de ejemplo cargados. Hay decenas de permisos individuales definidos (`PATIENT_*`, `APPOINTMENT_*`, `APPOINTMENT_TYPE_*`, `BILLING_*`, `BILLING_TARIFF_*`, `DOCTOR_*`, `SPECIALTY_*`, `MEDICAL_RECORD_*`, `NOTIFICATION_*`, `USER_*`) que se pueden asignar a nuevos roles según se necesite.

**Correo:** `auth-server` usa Mailtrap (SMTP de pruebas) para el flujo de activación de cuenta.

---

## 6. Comunicación síncrona (Feign) — mapa completo

Solo 3 microservicios llaman a otros directamente; el resto únicamente responde:

| Servicio origen | Llama a (Feign) | Para qué |
|---|---|---|
| `appointment-ms` | `billing-ms` | Consultar tarifa de un tipo de cita |
| `appointment-ms` | `doctor-ms` | Validar que el médico existe/está disponible |
| `appointment-ms` | `patient-ms` | Validar que el paciente existe |
| `billing-ms` | `appointment-ms` | Verificar datos de la cita al facturar |
| `doctor-ms` | `auth-server` | Crear/consultar el usuario asociado a un médico |

`patient-ms`, `notification-ms`, `medical-record-listener` y `auth-server` no tienen Feign clients propios — solo son consumidos por otros.

---

## 7. Resiliencia (circuit breaker / timeout)

Los 3 servicios que usan Feign (`appointment-ms`, `billing-ms`, `doctor-ms`) siguen el mismo patrón, cada uno con Resilience4j:

- `spring.cloud.openfeign.circuitbreaker.enabled: true`
- **Timeout** por llamada: `resilience4j.timelimiter` → **4 segundos**
- **Circuit breaker**: ventana de 5 llamadas, mínimo 5 llamadas para evaluar, 50% de fallos abre el circuito, 10 segundos en estado abierto, 2 llamadas de prueba en estado semiabierto
- Los errores de negocio (`BusinessException`, y los `FeignException` 400/404/422) están excluidos del cálculo de fallos — un "no encontrado" no cuenta como caída del servicio

| Servicio | A qué protege |
|---|---|
| `appointment-ms` | `patient-ms`, `doctor-ms`, `billing-ms` |
| `billing-ms` | `appointment-ms` |
| `doctor-ms` | `auth-server` |

El **gateway** tiene su propio circuit breaker, independiente del de Feign (es el filtro `CircuitBreaker` de Spring Cloud Gateway, una instancia por ruta: `patient-circuitbreaker`, `doctor-circuitbreaker`, `appointment-circuitbreaker`, `billing-circuitbreaker`, `notification-circuitbreaker`, `medical-record-listener-circuitbreaker`, timeout 4s cada una). Solo reacciona ante fallos reales de conexión o timeout — no ante respuestas de error válidas del microservicio (ver el informe de la sesión anterior sobre manejo de excepciones, `docs/informe-manejo-excepciones.md`).

---

## 8. Stack tecnológico por categoría

- **Base:** Spring Boot 4.1.0, Spring Cloud 2025.1.2, Java 17
- **Persistencia:** Spring Data JPA (PostgreSQL, MySQL), Spring Data MongoDB, Spring Data Redis, H2 (solo auth-server, en memoria)
- **Mensajería:** Spring Cloud Stream + binder de Kafka; `medical-record-listener` además usa **Kafka Streams** (KStream/KTable)
- **Comunicación síncrona:** OpenFeign + Resilience4j (circuit breaker + timeout)
- **Seguridad:** Spring Security, OAuth2 Resource Server, JWT firmado con RSA, JWKS propio
- **Descubrimiento y configuración:** Eureka (client/server), Spring Cloud Config (respaldado por Git)
- **Documentación de API:** Scalar (springdoc-openapi-starter-webmvc-scalar) en appointment-ms, billing-ms, doctor-ms, patient-ms, notification-ms — desactivado por defecto (`scalar.enabled: false`)
- **Observabilidad:** OpenTelemetry (traces/metrics/logs vía OTLP), Spring Boot Actuator
- **Utilidades:** Lombok, MapStruct (doctor-ms, mapeo DTO↔entidad), Mailtrap (correo de pruebas en auth-server)
- **Validación:** Bean Validation (`spring-boot-starter-validation`)

---

## 9. Infraestructura (Docker Compose)

**Infraestructura de soporte:**

| Servicio | Imagen | Puerto host |
|---|---|---|
| PostgreSQL | `postgres:16.1` | 5434 |
| Redis | `redis:7.4` | 6379 |
| MySQL (notificaciones) | `mysql:8.4` | 3307 |
| MongoDB | `mongo:7.0.5-rc0` | 27017 |
| Zookeeper | `confluentinc/cp-zookeeper:7.4.3` | 2181 |
| Kafka | `confluentinc/cp-kafka:7.4.3` | 9092 (externo) / 29092 (interno) |
| Kafka UI | `kafbat/kafka-ui` | 8087 |
| OTEL Collector | `otel/opentelemetry-collector-contrib:0.82.0` | 4317 / 4318 |
| Prometheus | `prom/prometheus:v3.13.2` | 9090 |
| Tempo | `grafana/tempo:2.5.0` | 3200 |
| Loki | `grafana/loki:3.0.0` | 3100 |
| Grafana | `grafana/grafana:13.1.3` | 3001 |

**Microservicios:** ver tabla de la sección 1 para el mapeo de puertos. Todos dependen (`depends_on`) de Eureka, Config Server, Kafka, su base de datos y el OTEL Collector según corresponda, y reciben sus variables de conexión y de OpenTelemetry por entorno.

**Levantar todo:**
```bash
docker compose up -d
```

---

## 10. Gateway — perfil activo y rutas

Perfil activo por defecto: **`oauth2`** (`spring.profiles.default` en `gateway/application.yml`). Existen otros perfiles definidos en `GatewayBeans.java` (`eureka-off`, `eureka-on`, `eureka-on-cb`) pero no están activos por defecto — sirven para pruebas locales sin OAuth2.

Rutas del perfil `oauth2` (todas resueltas vía Eureka con `lb://`):

| Path público | Va a | Circuit breaker propio |
|---|---|---|
| `/patients/**` | `patient-ms` | sí |
| `/doctors/**` | `doctor-ms` | sí |
| `/appointments/**` | `appointment-ms` | sí |
| `/billings/**` | `billing-ms` | sí |
| `/notifications/**` | `notification-ms` | sí |
| `/medical-records/**` | `medical-record-listener` | sí |
| `/auth-server/**` | `auth-server` | no (ruta pública, login/JWKS) |

---

## 11. Config Server y Eureka

- **`eureka-server`** — registro central de servicios. Standalone: no se registra a sí mismo ni descarga el registro de otros (`register-with-eureka: false`, `fetch-registry: false`). Puerto 8761.
- **`config-server`** — configuración centralizada, respaldada por un repositorio Git externo (rama `main`, clonado al arrancar, con usuario/token configurables por variable de entorno). Puerto 7777. Cada microservicio importa su configuración con `spring.config.import: optional:configserver:...` — el `optional:` es clave: si el Config Server no está disponible, el microservicio arranca igual usando solo su `application.yml`/`application.yaml` local.

---

## Ver también

- [`README.md`](../README.md) — visión general del proyecto, no técnica.
- [`informe-manejo-excepciones.md`](informe-manejo-excepciones.md) — informe de la revisión y corrección del manejo de errores entre microservicios y el gateway (sesión previa).
