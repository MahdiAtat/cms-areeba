# Multi-stage build for CMS Microservice
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /cms

# Copy parent pom first for better caching
COPY pom.xml ./pom.xml

# Copy the microservice pom
COPY cmsmicroservice/pom.xml ./cmsmicroservice/pom.xml

# Copy source code and swagger
COPY cmsmicroservice/src ./cmsmicroservice/src
COPY cmsmicroservice/swagger ./cmsmicroservice/swagger

# Change to microservice directory
WORKDIR /cms/cmsmicroservice

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Create cms user
RUN addgroup -S cmsuser && adduser -S cmsuser -G cmsuser

# Set working directory
WORKDIR /cms

# Copy the built JAR from build stage
COPY --from=build /cms/cmsmicroservice/target/*.jar cms.jar

# Change ownership to cms user
RUN chown -R cmsuser:cmsuser /cms
USER cmsuser

# Expose port
EXPOSE 8090

# Run the application
ENTRYPOINT ["java", "-jar", "cms.jar"]
