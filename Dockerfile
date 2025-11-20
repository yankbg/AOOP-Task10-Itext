# Use OpenJDK 17 as base image
#FROM openjdk:17
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

# Set working directory
WORKDIR /app

# Copy the JAR file from Gradle build
COPY build/libs/*.jar app.jar

# Expose port (if your app uses one)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]