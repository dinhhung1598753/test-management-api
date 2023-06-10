# Base image with Maven and JDK
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Compile the application
RUN mvn clean package

# Build the final image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file from the build stage
COPY --from=build /app/target/Project_Design_I-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port that the application listens on
EXPOSE 8000

# Set the command to run the application when the container starts
CMD ["java", "-jar", "/app/app.jar"]