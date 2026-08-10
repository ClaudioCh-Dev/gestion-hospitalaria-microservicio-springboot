FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY eureka-server/src eureka-server/src
RUN mvn -f /app/eureka-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/eureka-server/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]