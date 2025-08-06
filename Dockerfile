# Use Eclipse Temurin OpenJDK 17 as base image
FROM eclipse-temurin:17-jre-alpine

# Set environment variables
ENV JAVA_OPTS=""

# Set the working directory
WORKDIR /app

# Copy the jar file
COPY target/Task-Management-System-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
