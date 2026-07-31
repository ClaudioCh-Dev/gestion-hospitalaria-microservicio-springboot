FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENV EUREKA_SERVER_URL null
ENV CONFIG_SERVER_HOST null
ENV KAFKA_BROKER null
ENV OTLP_TRACING_URL null
ENV OTLP_LOGGING_URL null
ENV OTLP_METRICS_URL null
ENTRYPOINT ["java","-jar","/app.jar"]