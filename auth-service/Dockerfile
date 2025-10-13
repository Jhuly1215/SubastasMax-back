# ===== Build =====
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests=true dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests=true clean package

# ===== Run =====
FROM eclipse-temurin:17-jre
ENV JAVA_OPTS=""
ENV PORT=8080
WORKDIR /opt/app
COPY --from=build /app/target/*.jar app.jar

# Si usas archivo de credenciales:
# COPY firebase-service-account.json /secrets/firebase.json
# ENV FIREBASE_CREDENTIALS_FILE=/secrets/firebase.json

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
