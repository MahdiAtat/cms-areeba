# Multi-stage build for CMS Microservice
#FROM maven:3.8.1 AS build
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Install OpenJDK 21
#RUN microdnf update -y && microdnf install -y \
#    java-21-openjdk-devel \
#    && microdnf clean all

# Set JAVA_HOME to Java 21
#ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
#ENV PATH=$JAVA_HOME/bin:$PATH

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

# Install OpenJDK 21
#RUN microdnf update -y && microdnf install -y \
#    java-21-openjdk-devel \
#    && microdnf clean all

# Set JAVA_HOME to Java 21
#ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
#ENV PATH=$JAVA_HOME/bin:$PATH

# Create cms user
RUN groupadd -r cmsuser && useradd -r -g cmsuser cmsuser

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
ENTRYPOINT ["java", "-jar", "cms.jar", "--spring.profiles.active=local"]
