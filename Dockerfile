# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built artifact and resources
COPY --from=builder /app/target/quarkus-app/lib/ /app/lib/
COPY --from=builder /app/target/quarkus-app/*.jar /app/
COPY --from=builder /app/target/quarkus-app/app/ /app/app/
COPY --from=builder /app/target/quarkus-app/quarkus/ /app/quarkus/
COPY telemetry.csv /app/

# Configure the application
ENV QUARKUS_HTTP_PORT=8080
ENV org.acme.telemetry.csv.path=telemetry.csv

# Set user for better security
RUN useradd -r -u 1001 -g root telemetry
RUN chown -R telemetry:root /app
USER 1001

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]