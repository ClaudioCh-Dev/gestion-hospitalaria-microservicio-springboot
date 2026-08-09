FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copiar hospital-shared
COPY hospital-shared/pom.xml hospital-shared/pom.xml
COPY hospital-shared/src hospital-shared/src
# Copiar appointment-ms
COPY appointment-ms/pom.xml appointment-ms/pom.xml
COPY appointment-ms/src appointment-ms/src
# Instalar hospital-shared en el repositorio Maven
# del contenedor
RUN mvn -f /app/hospital-shared/pom.xml clean install -DskipTests
# Compilar appointment-ms
WORKDIR /app/appointment-ms
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/appointment-ms/target/*.jar app.jar
COPY appointment-ms/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV EUREKA_SERVER_URL=null
ENV CONFIG_SERVER_HOST=null
ENV KAFKA_BROKER=null
ENV OTEL_SERVICE_NAME=appointment-ms
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
ENV OTEL_LOGS_EXPORTER=otlp
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-jar","/app/app.jar"]