FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copiar hospital-shared 
COPY hospital-shared/pom.xml hospital-shared/pom.xml 
COPY hospital-shared/src hospital-shared/src 
# Copiar auth-server 
COPY auth-server/pom.xml auth-server/pom.xml
COPY auth-server/src auth-server/src
# Instalar hospital-shared en el repositorio Maven 
# del contenedor 
RUN mvn -f /app/hospital-shared/pom.xml clean install -DskipTests 
# Compilar auth-server 
WORKDIR /app/auth-server 
RUN mvn clean package -DskipTests 


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
ENV REDIS_HOST=redis-hospital
ENV REDIS_PORT=6379
ENV MAILTRAP_HOST=sandbox.smtp.mailtrap.io
ENV MAILTRAP_PORT=2525
ENV MAILTRAP_USERNAME=${MAILTRAP_HOST}
ENV MAILTRAP_PASSWORD=${MAILTRAP_PASSWORD}

ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-jar","/app/app.jar"]
