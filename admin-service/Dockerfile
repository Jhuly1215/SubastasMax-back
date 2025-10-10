# ---------- STAGE 1: Build del proyecto ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar los archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Compilar sin ejecutar tests
RUN mvn dependency:purge-local-repository
RUN mvn -U -q -DskipTests package


# ---------- STAGE 2: Imagen final de ejecución ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Configuración de entorno Java
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

# Copiamos el .jar compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# 🔐 Directorio donde se montarán las credenciales Firebase
VOLUME /secrets

# 🔧 Variables de entorno (coinciden con las del .env)
ENV GOOGLE_APPLICATION_CREDENTIALS_CONTAINER=/secrets/admin-service-key.json
ENV FIREBASE_DATABASE_URL=https://admin-service-57abc.firebaseio.com

# Puerto del microservicio
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
