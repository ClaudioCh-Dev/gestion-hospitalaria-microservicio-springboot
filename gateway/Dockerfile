FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY gateway/pom.xml gateway/pom.xml
COPY gateway/src gateway/src
RUN mvn -f /app/gateway/pom.xml clean package -DskipTests


FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/gateway/target/*.jar app.jar
COPY gateway/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV EUREKA_SERVER_URL=null
ENV AUTH_VALIDATE_URI=null
ENV OTEL_SERVICE_NAME=gateway
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
ENV OTEL_LOGS_EXPORTER=otlp
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-jar","/app/app.jar"]