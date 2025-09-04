# Multi-stage build for Fraud Microservice
FROM maven:3.8.1 AS build

# Install OpenJDK 21
RUN microdnf update -y && microdnf install -y \
    java-21-openjdk-devel \
    && microdnf clean all

# Set JAVA_HOME to Java 21
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

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
FROM maven:3.8.1

# Install OpenJDK 21
RUN microdnf update -y && microdnf install -y \
    java-21-openjdk-devel \
    && microdnf clean all

# Set JAVA_HOME to Java 21
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Create fraud user
RUN groupadd -r frauduser && useradd -r -g frauduser frauduser

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
ENTRYPOINT ["java", "-jar", "fraud.jar", "--spring.profiles.active=local"]
