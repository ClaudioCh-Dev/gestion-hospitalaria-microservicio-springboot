# ⚙️ Repositorio de configuración — Config Server

Este es el repositorio Git que lee el **`config-server`** (Spring Cloud Config) del proyecto
[gestion-hospitalaria-microservicio](https://github.com/ClaudioCh-Dev/gestion-hospitalaria-microservicio-springboot).
Cada microservicio, al arrancar, le pide su configuración al Config Server y este la saca de aquí.

> Si clonaste el proyecto de microservicios y **no tienes acceso** al repo de config original, copia
> el contenido de la carpeta `config-repo-example/` a un repositorio Git tuyo (público o privado)
> y apunta `CONFIG_GIT_URI` a ese repo en el `.env`.

---

## 📁 Convención de nombres

Spring Cloud Config resuelve los archivos por `{application}-{profile}.yml`:

| Archivo | Cuándo se usa |
|---|---|
| `patient-ms.yml` | Siempre (base) para `patient-ms`, sea cual sea el perfil |
| `patient-ms-dev.yml` | Solo con `SPRING_PROFILES_ACTIVE=dev` (sobrescribe al base) |
| `patient-ms-prod.yml` | Solo con `SPRING_PROFILES_ACTIVE=prod` (sobrescribe al base) |

- `{application}` = `spring.application.name` del microservicio.
- Rama leída: **`main`** (`default-label: main` en el config-server).
- En `docker-compose.yml` todos los servicios corren con `SPRING_PROFILES_ACTIVE=default`, así que en Docker se usa **el archivo base** (`<servicio>.yml`).
- Los `-dev` activan la documentación **Scalar** (`scalar.enabled: true`); base y `-prod` la dejan apagada.

### Archivos incluidos

```
appointment-ms.yml             appointment-ms-dev.yml             appointment-ms-prod.yml
auth-server.yml                                                   auth-server-prod.yml
billing-ms.yml                 billing-ms-dev.yml                 billing-ms-prod.yml
doctor-ms.yml                  doctor-ms-dev.yml                  doctor-ms-prod.yml
medical-record-listener.yml    medical-record-listener-dev.yml    medical-record-listener-prod.yml
notification-ms.yml            notification-ms-dev.yml            notification-ms-prod.yml
patient-ms.yml                 patient-ms-dev.yml                 patient-ms-prod.yml
```

`gateway`, `eureka-server` y `config-server` **no** leen configuración de aquí.

---

## 🧩 Cómo encaja con los `application.yml`

Los microservicios dejan placeholders con valor por defecto en su `application.yml`:

```yaml
# patient-ms/src/main/resources/application.yml
spring:
  datasource:
    username: ${db.username:postgres}
    password: ${db.password:123456}
    url: ${db.url:jdbc:postgresql://localhost:5434/hospital_db}
    hikari:
      connection-timeout: ${db.connection-timeout:30000}
      maximum-pool-size: ${db.maximum-pool-size:10}
```

Y este repo rellena esas claves `db.*`:

```yaml
# patient-ms.yml
db:
  url: jdbc:postgresql://db-hospital:5432/hospital_db   # host = container_name de Postgres en Docker
  username: postgres
  password: 123456
  connection-timeout: 20000
  maximum-pool-size: 5

scalar:
  enabled: false
```

Si el Config Server no responde, el servicio arranca igual (`optional:configserver:`) y usa los valores por defecto
del `application.yml` (`localhost:5434`), que solo sirven cuando corres el servicio fuera de Docker.

---

## 📝 Ejemplos por servicio

### PostgreSQL — `patient-ms`, `doctor-ms`, `appointment-ms`, `billing-ms`

```yaml
# <servicio>.yml y <servicio>-prod.yml
db:
  url: jdbc:postgresql://db-hospital:5432/hospital_db
  username: postgres
  password: 123456
  connection-timeout: 20000
  maximum-pool-size: 5

scalar:
  enabled: false
```

```yaml
# <servicio>-dev.yml
db:
  url: jdbc:postgresql://db-hospital:5432/hospital_db
  username: postgres
  password: 123456
  connection-timeout: 20000
  maximum-pool-size: 5

scalar:
  enabled: true
```

### MySQL — `notification-ms`

```yaml
# notification-ms.yml
db:
  url: jdbc:mysql://db-notification:3306/notification_db
  username: root
  password: 123456
  connection-timeout: 20000
  maximum-pool-size: 5

scalar:
  enabled: false
```

### MongoDB — `medical-record-listener`

```yaml
# medical-record-listener.yml
db:
  url: mongodb://sa:sa@mongo-hospital:27017/medical_records?authSource=admin

scalar:
  enabled: false
```

> La conexión que usa realmente el servicio es `MONGO_URI` / su `application.yaml`
> (`mongodb://sa:sa@mongo-db:27017/...`); este `db.url` queda como referencia.

### Auth — `auth-server`

```yaml
# auth-server.yml
mail:
  port: 587
  host: smtp.gmail.com
  username: your-email@gmail.com
  password: your-password
```

> El envío de correos del `auth-server` se configura realmente con las variables `MAILTRAP_*`
> del `.env` del proyecto de microservicios. **No subas credenciales reales** a este repo.

---

## ✅ Probar que el Config Server sirve la config

Con el `config-server` levantado (puerto `7777`):

```bash
curl http://localhost:7777/patient-ms/default     # patient-ms.yml
curl http://localhost:7777/patient-ms/dev         # patient-ms.yml + patient-ms-dev.yml
curl http://localhost:7777/notification-ms/prod   # notification-ms.yml + notification-ms-prod.yml
```

La respuesta es un JSON con `propertySources`; ahí debe aparecer la URL de este repo y las claves `db.*`.

Después de hacer `git push` a este repo, **reinicia el microservicio** (o el config-server si cambiaste la URL/rama)
para que tome los cambios:

```bash
docker compose restart ms-patient
```

---

## 🔐 Repo privado

Si este repo es privado, el config-server se autentica con un **Personal Access Token** de GitHub
(permiso de solo lectura de contenido):

```env
# .env del proyecto de microservicios
CONFIG_GIT_URI=https://github.com/<tu-usuario>/<tu-repo-config>.git
CONFIG_GIT_USERNAME=<tu-usuario>
CONFIG_GIT_TOKEN=<tu-token>
```

Si el repo es público, `CONFIG_GIT_TOKEN` puede quedar vacío.
