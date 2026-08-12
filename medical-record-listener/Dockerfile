FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
# Copiar hospital-shared
COPY hospital-shared/pom.xml hospital-shared/pom.xml
COPY hospital-shared/src hospital-shared/src
# Copiar medical-record-listener
COPY medical-record-listener/pom.xml medical-record-listener/pom.xml
COPY medical-record-listener/src medical-record-listener/src
# Instalar hospital-shared en el repositorio Maven
# del contenedor
RUN mvn -f /app/hospital-shared/pom.xml clean install -DskipTests
# Compilar medical-record-listener
WORKDIR /app/medical-record-listener
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/medical-record-listener/target/*.jar app.jar
COPY medical-record-listener/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV EUREKA_SERVER_URL=null
ENV CONFIG_SERVER_HOST=null
ENV KAFKA_BROKER=null
ENV OTEL_SERVICE_NAME=medical-record-listener
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
ENV OTEL_LOGS_EXPORTER=otlp
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-jar","/app/app.jar"]