FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

COPY target/*.jar app.jar

# OpenTelemetry Java Agent
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar

ENV EUREKA_SERVER_URL=null
ENV CONFIG_SERVER_HOST=null

# OpenTelemetry
ENV OTEL_SERVICE_NAME=auth-server
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
ENV OTEL_LOGS_EXPORTER=otlp
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp

ENTRYPOINT ["java","-javaagent:opentelemetry-javaagent.jar","-jar","/app.jar"]