# Use Java 25 (early access)
FROM openjdk:25-ea-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the built jar file
COPY target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8761

# Run the jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
