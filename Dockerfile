# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Construir el JAR saltando tests para agilizar el build en este paso (los tests deberían correrse antes en CI)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Crear usuario no root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR desde el stage de build
COPY --from=build /app/target/*.jar app.jar

# Configurar variables de entorno por defecto (pueden sobreescribirse)
ENV PORT=8080
EXPOSE 8080

# Usar perfil 'prod' por defecto
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
