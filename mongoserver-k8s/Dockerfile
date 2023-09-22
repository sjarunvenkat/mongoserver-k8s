# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR (you should have built this already)
COPY target/mongoserver-0.0.1-SNAPSHOT.jar app.jar

# Specify the command to run your application
CMD ["java", "-jar", "app.jar"]
