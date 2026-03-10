# Build Stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Create the /data directory and give permissions to the spring user
RUN mkdir -p /data/comprobantes /data/contratos && \
    chown -R spring:spring /data && \
    chmod -R 755 /data

# Copy the jar
COPY --from=build /app/target/*.jar app.jar

# Give permissions to the app directory
RUN chown -R spring:spring /app

# Switch to non-root user
# USER spring

# Expose port
EXPOSE 8080

# Run the app
# Limit memory for Render free tier (512MB) and optimize for fast startup
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]
