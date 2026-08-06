# Observabilidad con OpenTelemetry

## Arquitectura

La plataforma utiliza un stack de observabilidad basado en **OpenTelemetry**, **Prometheus**, **Tempo**, **Loki** y **Grafana** para recopilar y visualizar:

- **Traces** (trazas distribuidas)
- **Metrics** (métricas de aplicación)
- **Logs** (registros de aplicación)

```text
                         ┌─────────────────┐
                         │   auth-server   │
                         │   Spring Boot   │
                         └─────────────────┘
                                  |
              ┌───────────────────┼───────────────────┐
              |                   |                   |
              ↓                   ↓                   ↓

        ┌────────────┐      ┌────────────┐     ┌────────────────┐
        │ Micrometer │      │  Actuator  │     │ OTEL Appender  │
        │  Tracing   │      │  Metrics   │     │     Logs       │
        └────────────┘      └────────────┘     └────────────────┘
              |                   |                   |
              ↓                   ↓                   ↓

        ┌────────────┐      ┌────────────┐     ┌────────────┐
        │   OTEL     │      │ Prometheus │     │    OTEL    │
        │ Collector  │      │            │     │ Collector  │
        └────────────┘      └────────────┘     └────────────┘
              |                                      |
              ↓                                      ↓

        ┌────────────┐                      ┌────────────┐
        │   Tempo    │                      │    Loki    │
        │  Traces    │                      │   Logs     │
        └────────────┘                      └────────────┘


                         ┌────────────┐
                         │  Grafana   │
                         │            │
                         │ Dashboard  │
                         └────────────┘
```

---

# Componentes

## OpenTelemetry

OpenTelemetry es el estándar utilizado para recopilar datos de observabilidad.

Permite capturar:

- Trazas distribuidas
- Métricas
- Logs

y enviarlos hacia diferentes herramientas de monitoreo.

---

# Traces

## Micrometer Tracing

Spring Boot utiliza Micrometer Tracing para generar trazas automáticamente.

Ejemplo:

```text
Request /auth/login

auth-server
    |
    ├── Controller
    |
    ├── Service
    |
    └── Database
```

El flujo es:

```text
Spring Boot
      |
Micrometer Tracing
      |
OpenTelemetry
      |
OTEL Collector
      |
Tempo
```

Tempo almacena las trazas y permite analizar el recorrido de una petición.

---

# Metrics

## Spring Boot Actuator + Prometheus

Actuator expone métricas internas mediante:

```
/actuator/prometheus
```

Ejemplos:

- Uso de memoria JVM
- Cantidad de requests
- Tiempo de respuesta
- Errores HTTP
- Estado de conexiones

El flujo es:

```text
Spring Boot Actuator
          |
          |
     /actuator/prometheus
          |
          ↓
     Prometheus
          |
          ↓
       Grafana
```

Prometheus obtiene las métricas realizando un proceso de **scraping** periódico.

---

# Logs

## OpenTelemetry Logback Appender

Los logs generados por Spring Boot son enviados mediante OpenTelemetry.

Ejemplo:

```java
log.info("Usuario autenticado");
```

Flujo:

```text
Spring Boot
      |
    Logback
      |
OTEL Appender
      |
OTEL Collector
      |
Loki
      |
Grafana
```

Loki permite buscar y analizar los logs de todos los microservicios.

---

# Grafana

Grafana funciona como capa de visualización.

Se conecta a:

| Fuente | Información |
|---|---|
| Prometheus | Métricas |
| Tempo | Traces |
| Loki | Logs |

Permite relacionar información:

Ejemplo:

```
Trace ID: abc123

Tempo:
    Login tardó 500ms

Loki:
    Error encontrado en auth-server

Prometheus:
    Incremento de errores HTTP 500
```

---

# Flujo completo

```text
                     Microservicios
                          |
        ┌─────────────────┼─────────────────┐
        |                 |                 |
     Traces            Metrics            Logs
        |                 |                 |
        ↓                 ↓                 ↓

 OTEL Collector     Prometheus       OTEL Collector
        |                                   |
        ↓                                   ↓

      Tempo                              Loki

        └─────────────────┬─────────────────┘
                          |
                          ↓

                       Grafana
```

---

# Beneficios

- Detectar dónde ocurre un error.
- Medir rendimiento de microservicios.
- Analizar tiempos de respuesta.
- Buscar errores mediante logs.
- Tener visibilidad completa del sistema distribuido.