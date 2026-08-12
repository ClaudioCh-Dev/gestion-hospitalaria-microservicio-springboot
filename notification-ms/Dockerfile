FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copiar hospital-shared
COPY hospital-shared/pom.xml hospital-shared/pom.xml
COPY hospital-shared/src hospital-shared/src
# Copiar notification-ms
COPY notification-ms/pom.xml notification-ms/pom.xml
COPY notification-ms/src notification-ms/src
# Instalar hospital-shared en el repositorio Maven
# del contenedor
RUN mvn -f /app/hospital-shared/pom.xml clean install -DskipTests
# Compilar notification-ms
WORKDIR /app/notification-ms
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/notification-ms/target/*.jar app.jar
COPY notification-ms/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV EUREKA_SERVER_URL=null
ENV KAFKA_BROKER=null
ENV CONFIG_SERVER_HOST=null
ENV OTEL_SERVICE_NAME=notification-ms
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
ENV OTEL_LOGS_EXPORTER=otlp
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-jar","/app/app.jar"]