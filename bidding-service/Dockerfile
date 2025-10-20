FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY src/main/resources/firebase ./src/main/resources/firebase
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
COPY --from=build /app/target/*.jar app.jar
# No es necesario exponer puerto: el gateway enruta a través de Eureka
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
