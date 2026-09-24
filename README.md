# 🏥 Sistema de Gestión Hospitalaria — Microservicios

<p align="center">
  <img src="https://github.com/user-attachments/assets/0a92bd98-71d1-4093-84d4-e45a4616e505" width="800"/>
</p>

## 🔗 Frontend

Este backend consume el frontend de Gestión Hospitalaria (Angular + signals).

👉 https://github.com/ClaudioCh-Dev/gestion-hospitalaria-web-angular

---

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
| `config-repo-example/` | No es un servicio: plantilla del repositorio Git que lee el `config-server` (ver paso 2 de "Cómo levantar"). |

---

# 🎨 Frontend (planeado)

La solución contará con un dashboard administrativo para el personal del hospital, con vistas para registrar pacientes, gestionar médicos, programar citas, ver agendas y gestionar pagos.

<p align="center">
  <img src="https://github.com/user-attachments/assets/59c30420-a57e-4918-a090-b8dcdbbba7da" width="800"/>
</p>

---

# ▶️ Cómo levantar el proyecto

Todo corre en Docker: bases de datos, Kafka, observabilidad y los servicios Spring. **No necesitas Java ni Maven instalados** — cada imagen compila su servicio (y la librería `hospital-shared`) dentro del contenedor.

## 0. Requisitos

- **Docker Desktop** (con Docker Compose v2 → comando `docker compose`).
- **Git**.
- **OpenSSL** para generar la llave RSA (viene con Git for Windows: úsalo desde *Git Bash*).
- ~8 GB de RAM libres para Docker (son ~20 contenedores).
- Puertos libres: `2181 3000 3001 3100 3200 3307 4040 4317 4318 5434 6379 7777 8081-8087 8761 9090 9092 27017`.

## 1. Clonar el repositorio

```bash
git clone https://github.com/ClaudioCh-Dev/gestion-hospitalaria-microservicio-springboot.git
cd gestion-hospitalaria-microservicio-springboot
```

## 2. Preparar el repositorio de configuración (Config Server)

El `config-server` lee la configuración de cada microservicio desde **otro repositorio Git** (por defecto [`config-gestion-hospitalaria`](https://github.com/ClaudioCh-Dev/config-gestion-hospitalaria)).

Si no tienes acceso a ese repo, crea el tuyo a partir de la carpeta de ejemplo [`config-repo-example/`](config-repo-example/):

```bash
# fuera de este proyecto
mkdir config-gestion-hospitalaria && cd config-gestion-hospitalaria
cp ../gestion-hospitalaria-microservicio-springboot/config-repo-example/* .
git init -b main
git add . && git commit -m "config inicial"
git remote add origin https://github.com/<tu-usuario>/config-gestion-hospitalaria.git
git push -u origin main
```

> La rama **debe llamarse `main`**. Si el repo es privado necesitas un *Personal Access Token* de GitHub con permiso de lectura de contenido.
> Qué va en cada archivo y cómo se combinan los perfiles: ver [`config-repo-example/README.md`](config-repo-example/README.md).

## 3. Crear el archivo `.env`

En la raíz del proyecto (al lado de `docker-compose.yml`):

```bash
cp .env.example .env
```

y rellénalo:

```env
# Config Server
CONFIG_GIT_URI=https://github.com/<tu-usuario>/config-gestion-hospitalaria.git
CONFIG_GIT_USERNAME=<tu-usuario-github>
CONFIG_GIT_TOKEN=<tu-token>            # vacío si el repo es público

# Correo del auth-server (https://mailtrap.io → Email Testing → Inbox → SMTP)
MAILTRAP_HOST=sandbox.smtp.mailtrap.io
MAILTRAP_PORT=2525
MAILTRAP_USERNAME=<usuario-mailtrap>
MAILTRAP_PASSWORD=<password-mailtrap>
MAILTRAP_FROM=no-reply@demomailtrap.co

FRONTEND_URL=https://localhost:4200

# Cookie del refresh token: false en desarrollo (http), true en producción (https)
REFRESH_COOKIE_SECURE=false
REFRESH_COOKIE_SAME_SITE=Lax
```

| Variable | La usa | Para qué |
|---|---|---|
| `CONFIG_GIT_URI` / `CONFIG_GIT_USERNAME` / `CONFIG_GIT_TOKEN` | `ms-config-server` | Clonar el repo de configuración |
| `MAILTRAP_*` | `auth-server` | Enviar correos (recuperar contraseña, etc.) |
| `FRONTEND_URL` | `auth-server` | Links que van dentro de los correos |
| `REFRESH_COOKIE_SECURE` / `REFRESH_COOKIE_SAME_SITE` | `auth-server` | Atributos de la cookie `refresh_token`. Sin HTTPS usa `false` / `Lax`; en producción `true` / `Strict` (valor por defecto si no se define) |

> El `.env` está en `.gitignore`: **nunca lo subas**. El resto de variables (URLs de Eureka, Kafka, OTEL…) ya vienen fijadas en `docker-compose.yml`.

## 4. Generar la llave privada RSA del `auth-server`

El `auth-server` firma los JWT con `auth-server/src/main/resources/keys/private-key-pkcs8.pem`. Ese archivo **no está en Git** (`*.pem` está ignorado), así que hay que generarlo **antes del build**, porque se copia dentro de la imagen:

```bash
mkdir -p auth-server/src/main/resources/keys
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 \
  -out auth-server/src/main/resources/keys/private-key-pkcs8.pem
```

Debe empezar con `-----BEGIN PRIVATE KEY-----` (formato PKCS#8). Si falta, el `auth-server` se cae al arrancar con `No se encontró private-key-pkcs8.pem`.

## 5. Construir las imágenes

```bash
docker compose build
```

La primera vez tarda (descarga las dependencias Maven de cada servicio). Para reconstruir uno solo tras un cambio de código:

```bash
docker compose build ms-patient
```

## 6. Levantar los contenedores

Se recomienda arrancar por etapas para que cada capa esté lista antes de la siguiente (el `docker-compose.yml` solo tiene `depends_on`, sin *healthchecks*):

```bash
# 6.1 Infraestructura: bases de datos, Kafka y observabilidad
docker compose up -d db db-notification mongo-db redis zookeeper kafka kafka-ui \
  otel-collector prometheus tempo loki grafana

# 6.2 Registro y configuración
docker compose up -d ms-registry-server ms-config-server
docker compose logs -f ms-config-server   # Ctrl+C cuando aparezca "Started ConfigServerApplication"

# 6.3 Auth, microservicios de dominio y gateway
docker compose up -d
```

Atajo (todo de una vez; los servicios tienen `restart: always` y se reintentan solos hasta que sus dependencias estén arriba):

```bash
docker compose up -d --build
```

> Los scripts de `sql/` (esquema + datos de ejemplo) se ejecutan **solo la primera vez** que se crean los contenedores de Postgres y MySQL.

## 7. Verificar que todo está arriba

```bash
docker compose ps
```

1. **Config Server** sirve la configuración:
   ```bash
   curl http://localhost:7777/patient-ms/default
   ```
   Debe devolver un JSON con la URL de tu repo de config y las claves `db.*`.
2. **Eureka** → <http://localhost:8761>: deben aparecer `AUTH-SERVER`, `PATIENT-MS`, `DOCTOR-MS`, `APPOINTMENT-MS`, `BILLING-MS`, `NOTIFICATION-MS`, `MEDICAL-RECORD-LISTENER`, `GATEWAY` y `CONFIG-SERVER` (puede tardar ~1 min).
3. **Login** a través del gateway (usuarios creados automáticamente por el `DataSeeder` del auth-server):
   ```bash
   curl -X POST http://localhost:4040/auth-server/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@example.com","password":"123456"}'
   ```

   | Usuario | Password | Rol |
   |---|---|---|
   | `admin@example.com` | `123456` | ADMIN |
   | `doctor@example.com` | `123456` | DOCTOR |

4. Con el token, llamar a los servicios por el gateway (`/patients/**`, `/doctors/**`, `/appointments/**`, `/billings/**`, `/notifications/**`, `/medical-records/**`):
   ```bash
   curl http://localhost:4040/patients/<endpoint> -H "Authorization: Bearer <access_token>"
   ```
   Endpoints de cada servicio en [`docs/arquitectura-tecnica.md`](docs/arquitectura-tecnica.md).
5. **Postman**: importa la colección de [`postman/`](postman/) — trae todos los endpoints con ejemplos, guarda el token al hacer Login y tiene una carpeta *Flujo completo* que recorre cita → pago → historial clínico. Ver [`postman/README.md`](postman/README.md).

## 8. URLs y puertos

| Qué | URL / puerto (host) |
|---|---|
| **API Gateway** (entrada única) | <http://localhost:4040> |
| Auth Server | <http://localhost:3000/auth-server> |
| Eureka | <http://localhost:8761> |
| Config Server | <http://localhost:7777> |
| patient-ms / doctor-ms / appointment-ms | `8081` / `8082` / `8083` |
| billing-ms / notification-ms / medical-record-listener | `8084` / `8085` / `8086` |
| Kafka UI | <http://localhost:8087> |
| Grafana | <http://localhost:3001> (admin / admin) |
| Prometheus | <http://localhost:9090> |
| PostgreSQL `hospital_db` | `localhost:5434` — `postgres` / `123456` |
| MySQL `notification_db` | `localhost:3307` — `root` / `123456` |
| MongoDB `medical_records` | `localhost:27017` — `sa` / `sa` |
| Redis | `localhost:6379` |

## 9. Comandos útiles

```bash
docker compose logs -f ms-appointment     # ver logs de un servicio
docker compose restart ms-patient         # reiniciar (p. ej. tras cambiar el repo de config)
docker compose up -d --build ms-billing   # recompilar y reiniciar un servicio
docker compose down                       # apagar todo
docker compose down -v                    # apagar y borrar volúmenes
```

## 10. Problemas comunes

| Síntoma | Causa / solución |
|---|---|
| `ms-config-server` falla con `not authorized` / `Authentication is required` | `CONFIG_GIT_URI`, usuario o token incorrectos en `.env`, o el token no tiene acceso al repo. |
| `ms-config-server` falla con `No ref ... main` | El repo de config no tiene rama `main`. |
| Un microservicio intenta conectarse a `localhost:5434` y se reinicia en bucle | No recibió su config (config-server caído o falta `<servicio>.yml` en el repo). Revisa el paso 7.1. |
| `auth-server` → `No se encontró private-key-pkcs8.pem` | Falta el paso 4: genera la llave y ejecuta `docker compose build auth-server`. |
| El refresh token no llega (`Missing cookie 'refresh_token'`) | Sin HTTPS pon `REFRESH_COOKIE_SECURE=false` en el `.env` y `docker compose up -d auth-server`. Desde el front, las llamadas a login/refresh/logout deben ir con `withCredentials: true`. |
| El gateway responde `401` | Falta el header `Authorization: Bearer <token>` o el token expiró (dura 15 min). |
| El gateway responde con el *fallback* | El servicio aún no se registró en Eureka; espera ~1 min y reintenta. |
| Puerto ya en uso | Otro Postgres/MySQL/Redis local ocupa el puerto; detenlo o cambia el puerto de host en `docker-compose.yml`. |

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
