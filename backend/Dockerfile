# 1) Build stage: compile with Maven (using a published tag)
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy just the POM, go offline (caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy sources and build
COPY src ./src
RUN mvn package -DskipTests -B

# 2) Run stage: slim Alpine JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Pull in the “fat” JAR from builder
COPY --from=build /app/target/nto-backend-1.0-SNAPSHOT.jar ./app.jar
RUN apk add --no-cache curl
EXPOSE 8080

# Defaults (override in docker-compose)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/musicshop \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=postgres

ENTRYPOINT ["java","-jar","app.jar"]
