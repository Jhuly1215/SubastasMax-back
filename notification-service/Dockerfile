# Multi-stage Dockerfile: build with Maven, then run a minimal JRE image
# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Copiar los archivos de configuración de Maven (igual que el otro servicio)
COPY pom.xml .
COPY src ./src

# Run maven package (skip tests to speed up)
RUN mvn -B -DskipTests package

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy jar built in previous stage
ARG JAR_NAME=notification-service-0.0.1-SNAPSHOT.jar
COPY --from=build /workspace/app/target/${JAR_NAME} app.jar

EXPOSE 8060

ENTRYPOINT ["java","-jar","/app/app.jar"]