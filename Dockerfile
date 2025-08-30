FROM openjdk:17-jdk-slim

LABEL maintainer="Avi Patel <avi.patel@example.com>"
LABEL description="Leave Management System - A comprehensive employee leave tracking application"

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:resolve

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Create non-root user for security
RUN addgroup --system lmsgroup && adduser --system lmsuser --ingroup lmsgroup

# Copy the built jar file
COPY target/leave-management-system-*.jar app.jar

# Change ownership of the app.jar file
RUN chown lmsuser:lmsgroup app.jar

# Switch to non-root user
USER lmsuser:lmsgroup

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]