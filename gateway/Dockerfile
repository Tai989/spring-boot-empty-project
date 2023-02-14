# Use the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-slim

# Set the timezone to China Standard Time
ENV TZ=Asia/Shanghai

# Update the timezone data
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set the working directory to /app
WORKDIR /app

# Copy the jar file into the container at /app
COPY build/libs/spring-boot-gateway-1.0.jar /app/spring-boot-gateway-1.0.jar

# Expose port 8877
EXPOSE 8877

# Run the command to start the Kotlin Spring Boot application
CMD ["java", "-Xmx300m", "-jar", "spring-boot-gateway-1.0.jar"]
