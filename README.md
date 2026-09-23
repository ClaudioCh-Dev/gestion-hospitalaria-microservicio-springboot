# 🏥 Sistema de Gestión Hospitalaria — Microservicios

<p align="center">
  <img src="https://github.com/user-attachments/assets/0a92bd98-71d1-4093-84d4-e45a4616e505" width="800"/>
</p>

# 🚧 Proyecto en construcción

Sistema backend para la gestión de un hospital, construido con **Spring Boot** y **Spring Cloud** bajo una arquitectura de microservicios. Permite administrar:

- 👥 Pacientes
- 👨‍⚕️ Médicos y especialidades
- 📅 Citas médicas
- 💰 Facturación y tarifas
- 📋 Historial clínico (generado automáticamente a partir de citas completadas y pagadas)
- 🔔 Notificaciones en tiempo real

> 📄 Para el detalle técnico completo (endpoints, tópicos de Kafka, esquema de base de datos, seguridad, resiliencia, etc.) ver **[`docs/arquitectura-tecnica.md`](docs/arquitectura-tecnica.md)**.

---

# 🎯 Idea general

Cada proceso del hospital (pacientes, médicos, citas, facturación, historial, notificaciones) vive en su propio microservicio, con su propia base de datos. Si un servicio falla, los demás siguen funcionando — por ejemplo, si el servicio de facturación cae, se puede seguir agendando citas con normalidad.

Los servicios se comunican de dos formas:

- **Directa (síncrona)** — cuando un servicio necesita una respuesta inmediata de otro (ej: al crear una cita, se valida en el momento que el paciente y el médico existan).
- **Por eventos (asíncrona, vía Kafka)** — cuando el proceso puede seguir sin esperar respuesta (ej: al completarse una cita y pagarse, se genera el historial clínico automáticamente, sin que nadie tenga que pedirlo).

Todas las peticiones del cliente pasan por una única puerta de entrada (**API Gateway**), que valida que el usuario esté autenticado antes de dejarlo pasar a cualquier microservicio.

---

# 🛠️ Stack tecnológico (resumen)

- **Backend:** Java 17, Spring Boot, Spring Cloud
- **Seguridad:** JWT (firmado con clave RSA propia) + permisos por rol
- **Comunicación:** OpenFeign (directa) + Resilience4j (tolerancia a fallos) + Kafka (eventos, incluye Kafka Streams)
- **Descubrimiento y configuración:** Eureka + Config Server (config centralizada en Git)
- **Datos:** PostgreSQL, MySQL, MongoDB, Redis
- **Observabilidad:** OpenTelemetry, Prometheus, Grafana, Loki, Tempo
- **Infraestructura:** Docker / Docker Compose

Detalle de versiones y de qué usa cada servicio en [`docs/arquitectura-tecnica.md`](docs/arquitectura-tecnica.md).

---

# 🧩 Microservicios

| Servicio | Qué hace |
|---|---|
| `gateway` | Puerta de entrada única. Valida el token JWT de cada petición y la reenvía al microservicio correspondiente. |
| `auth-server` | Login, emisión y validación de tokens JWT, gestión de usuarios, roles y permisos. |
| `patient-ms` | Registro y administración de pacientes. |
| `doctor-ms` | Registro de médicos y sus especialidades. |
| `appointment-ms` | Agenda de citas médicas. Valida que el paciente y el médico existan antes de confirmar. |
| `billing-ms` | Tarifas y facturación de citas. |
| `notification-ms` | Notificaciones (incluye notificaciones en tiempo real). |
| `medical-record-listener` | Genera el historial clínico automáticamente cuando una cita se completa y se paga. |
| `eureka-server` | Directorio donde cada microservicio se registra para que los demás lo puedan encontrar. |
| `config-server` | Configuración centralizada compartida por todos los microservicios. |
| `hospital-shared` | Librería común (eventos de Kafka, manejo de errores) reutilizada por todos los servicios. |

---

# 🎨 Frontend (planeado)

La solución contará con un dashboard administrativo para el personal del hospital, con vistas para registrar pacientes, gestionar médicos, programar citas, ver agendas y gestionar pagos.

<p align="center">
  <img src="https://github.com/user-attachments/assets/59c30420-a57e-4918-a090-b8dcdbbba7da" width="800"/>
</p>

---

# ▶️ Cómo levantar el proyecto

Todo el entorno (bases de datos, Kafka, observabilidad y los microservicios) se levanta con Docker Compose:

```bash
docker compose up -d
```

El punto de entrada queda en `http://localhost:4040` (gateway). El detalle de cada puerto, variables de entorno y el orden recomendado de arranque están en [`docs/arquitectura-tecnica.md`](docs/arquitectura-tecnica.md).

---

# 📌 Estado del proyecto

🚧 En construcción.

- [x] Diseño de arquitectura
- [x] Microservicios de dominio (pacientes, médicos, citas, facturación, notificaciones, historial clínico)
- [x] Seguridad con JWT y permisos
- [x] Comunicación con Kafka (incluye Kafka Streams para el historial clínico)
- [x] Persistencia (PostgreSQL, MySQL, MongoDB, Redis)
- [x] Observabilidad (OpenTelemetry, Prometheus, Grafana, Loki, Tempo)
- [x] Dockerización
- [ ] Frontend (dashboard administrativo)
- [ ] Despliegue en Kubernetes
