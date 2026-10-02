# ---- Build stage: compile the Spring Boot jar ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# ---- Run stage: small Java 21 runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render's free instance has 512 MB RAM; keep the JVM inside it.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k"
EXPOSE 8080

# Render provides PORT; application.properties already uses server.port=${PORT:8080}
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
