# Multi-stage build for CMS Microservice
FROM maven:3.8.1 AS build

# Install OpenJDK 21
RUN microdnf update -y && microdnf install -y \
    java-21-openjdk-devel \
    && microdnf clean all

# Set JAVA_HOME to Java 21
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Set working directory
WORKDIR /app

# Copy parent pom first for better caching
COPY pom.xml ./pom.xml

# Copy the microservice pom
COPY cmsmicroservice/pom.xml ./cmsmicroservice/pom.xml

# Copy source code and swagger
COPY cmsmicroservice/src ./cmsmicroservice/src
COPY cmsmicroservice/swagger ./cmsmicroservice/swagger

# Change to microservice directory
WORKDIR /app/cmsmicroservice

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM maven:3.8.1

# Install OpenJDK 21
RUN microdnf update -y && microdnf install -y \
    java-21-openjdk-devel \
    && microdnf clean all

# Set JAVA_HOME to Java 21
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/cmsmicroservice/target/*.jar app.jar

# Change ownership to app user
RUN chown -R appuser:appuser /app
USER appuser

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=local"]
