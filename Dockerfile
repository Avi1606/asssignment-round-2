# Use official OpenJDK 17 runtime as a parent image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy maven build artifacts
COPY target/leave-management-system-*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Add labels for better organization
LABEL maintainer="Avi Patel <avi.patel@company.com>"
LABEL description="Mini Leave Management System - A comprehensive leave management solution"
LABEL version="1.0.0"

# Set JVM options for better performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]