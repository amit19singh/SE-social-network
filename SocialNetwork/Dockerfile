# Start with a base image that has JDK 21
FROM eclipse-temurin:21
# openjdk:21-slim AS build
# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory in the Docker image
WORKDIR /app

# Copy the pom.xml and source code
COPY . .

# Build the application
RUN mvn clean package

# Use the JDK 21 image for running the application
FROM openjdk:21-slim
COPY ./target/SocialNetwork-0.0.1-SNAPSHOT.jar social-network.jar
#--from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
