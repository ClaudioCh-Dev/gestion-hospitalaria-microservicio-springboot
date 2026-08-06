```markdown
# 🏥 Sistema de Gestión Hospitalaria — Microservicios

<p align="center">
  <img src="https://github.com/user-attachments/assets/0a92bd98-71d1-4093-84d4-e45a4616e505" width="800"/>
</p>

# 🚧 Proyecto en construcción

Sistema de gestión hospitalaria desarrollado con una arquitectura basada en microservicios utilizando **Spring Boot** y tecnologías del ecosistema **Spring Cloud**.

El objetivo del proyecto es construir una solución completa para la administración de un hospital, permitiendo gestionar:

- 👥 Pacientes
- 👨‍⚕️ Médicos y especialidades
- 📅 Citas médicas
- 📋 Historial clínico
- 💰 Facturación y pagos

---

# 🎯 Objetivo del proyecto

Implementar una arquitectura moderna orientada a microservicios aplicando buenas prácticas de desarrollo backend:

- Separación de responsabilidades.
- Comunicación síncrona y asíncrona.
- Descubrimiento de servicios.
- Configuración centralizada.
- Seguridad basada en JWT.
- Observabilidad y monitoreo.

---

# 🛠️ Stack tecnológico

### Backend
- Java
- Spring Boot
- Spring Cloud
- Spring Security
- OAuth2 / JWT
- Spring Data JPA

### Arquitectura
- Eureka Service Discovery
- Config Server
- API Gateway
- OpenFeign
- Circuit Breaker (Resilience4j)

### Datos
- PostgreSQL
- MongoDB

### Mensajería
- Apache Kafka

### Infraestructura y observabilidad
- Docker
- OpenTelemetry
- Prometheus
- Grafana
- Loki
- Tempo

---

# 🎨 Diseño Frontend

La solución contará con un dashboard administrativo para el personal del hospital.

Los usuarios podrán:

- Registrar y administrar pacientes.
- Gestionar médicos.
- Programar citas.
- Visualizar agendas.
- Gestionar pagos.
- Consultar información del sistema.

<p align="center">
  <img src="https://github.com/user-attachments/assets/59c30420-a57e-4918-a090-b8dcdbbba7da" width="800"/>
</p>

---

# 🏗️ Flujo general del sistema

```

Usuario
|
v
Dashboard Web
|
v
API Gateway
|
+----------------+
|                |
Patient MS       Doctor MS
|
Appointment MS
|
Kafka Events
|
+----------------------+
| Billing MS           |
| Medical Record MS    |
| Notification MS      |
+----------------------+

```

---

# 📌 Estado del proyecto

🚧 Actualmente en fase de construcción.

Próximas etapas:

- [x] Diseño de arquitectura
- [x] Definición de microservicios
- [ ] Implementación de microservicios
- [ ] Seguridad JWT
- [ ] Comunicación con Kafka
- [ ] Persistencia PostgreSQL / MongoDB
- [ ] Observabilidad completa
- [ ] Dockerización
- [ ] Despliegue en Kubernetes

```
