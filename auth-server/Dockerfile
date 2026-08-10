FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY auth-server/pom.xml auth-server/pom.xml
COPY auth-server/src auth-server/src
RUN mvn -f /app/auth-server/pom.xml clean package -DskipTests


FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/auth-server/target/*.jar app.jar
COPY auth-server/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV EUREKA_SERVER_URL=null
ENV CONFIG_SERVER_HOST=null
ENV OTEL_SERVICE_NAME=auth-server
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
ENV OTEL_LOGS_EXPORTER=otlp
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-jar","/app/app.jar"]