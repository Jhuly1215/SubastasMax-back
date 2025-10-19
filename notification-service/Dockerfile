# Multi-stage Dockerfile: build with Maven, then run a minimal JRE image
# Build stage
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Avoid copying unnecessary files to speed up builds; copy pom first to leverage cache
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Copy sources
COPY src ./src

# Run maven package (skip tests to speed up; override with -DskipTests=false if needed)
RUN mvn -B -DskipTests package

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy jar built in previous stage
ARG JAR_NAME=notification-service-0.0.1-SNAPSHOT.jar
# Use the produced jar name based on pom finalName: target/<JAR_NAME>
COPY --from=build /workspace/app/target/${JAR_NAME} app.jar

EXPOSE 8060

# Optional: run as non-root
# RUN addgroup -S appgroup && adduser -S appuser -G appgroup
# USER appuser

ENTRYPOINT ["java","-jar","/app/app.jar"]