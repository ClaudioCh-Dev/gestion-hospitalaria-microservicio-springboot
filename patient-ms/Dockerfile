FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV EUREKA_SERVER_URL=null
ENV CONFIG_SERVER_HOST=null
ENV KAFKA_BROKER=null
ENV OTLP_TRACING_URL=null
ENV OTLP_LOGGING_URL=null
ENV OTLP_METRICS_URL=null
ENTRYPOINT ["java","-jar","/app/app.jar"]