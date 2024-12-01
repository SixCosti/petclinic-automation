# Use of a Maven image for building
FROM maven:3.8.8-eclipse-temurin-17 AS builder
COPY . /app
WORKDIR /app
RUN mvn clean package

# Use of a lightweight OpenJDK image for running
FROM openjdk:17-jdk-alpine
WORKDIR /opt/petclinic
COPY --from=builder /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
