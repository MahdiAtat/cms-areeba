# Multi-stage build for Fraud Microservice
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /fraud

# Copy parent pom first for better caching
COPY pom.xml ./pom.xml

# Copy the microservice pom
COPY fraudmicroservice/pom.xml ./fraudmicroservice/pom.xml

# Copy source code and swagger
COPY fraudmicroservice/src ./fraudmicroservice/src
COPY fraudmicroservice/swagger ./fraudmicroservice/swagger

# Change to microservice directory
WORKDIR /fraud/fraudmicroservice

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Create fraud user
RUN addgroup -S frauduser && adduser -S frauduser -G frauduser

# Set working directory
WORKDIR /fraud

# Copy the built JAR from build stage
COPY --from=build /fraud/fraudmicroservice/target/*.jar fraud.jar

# Change ownership to fraud user
RUN chown -R frauduser:frauduser /fraud
USER frauduser

# Expose port
EXPOSE 8090

# Run the application
ENTRYPOINT ["java", "-jar", "fraud.jar"]
