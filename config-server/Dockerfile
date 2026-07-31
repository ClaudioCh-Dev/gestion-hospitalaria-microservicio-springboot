FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENV EUREKA_SERVER_URL null
ENV CONFIG_GIT_USERNAME null
ENV CONFIG_GIT_TOKEN null
ENV CONFIG_GIT_URI null
ENTRYPOINT ["java","-jar","/app.jar"]