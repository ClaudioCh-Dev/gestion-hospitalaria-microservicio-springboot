FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENV EUREKA_SERVER_URL null
ENV AUTH_VALIDATE_URI null
ENTRYPOINT ["java","-jar","/app.jar"]
