FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY config-server/pom.xml config-server/pom.xml
COPY config-server/src config-server/src
RUN mvn -f /app/config-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/config-server/target/*.jar app.jar
ENV EUREKA_SERVER_URL null
ENV CONFIG_GIT_USERNAME null
ENV CONFIG_GIT_TOKEN null
ENV CONFIG_GIT_URI null
ENTRYPOINT ["java","-jar","/app/app.jar"]