FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
# var opcional para ajustes de JVM
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# El SDK de Firebase usará GOOGLE_APPLICATION_CREDENTIALS (la define compose)
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
