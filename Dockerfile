FROM maven:3.9.11-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml /app/pom.xml

RUN mvn dependency:resolve -DskipTests -B

COPY src /app/src

RUN mvn clean test --no-transfer-progress -B \
 && mvn package -Pdev --no-transfer-progress -DskipTests -B

#######################################

FROM eclipse-temurin:17-alpine

ENV CACHE_EVICT_SPAN_MILLISECONDS=3600000 \
    ADAPTERS_HOST=http://rnapdbee-adapters-container \
    RNAPDBEE_ADAPTERS_MONO_CACHE_DURATION=3600 \
    RNAPDBEE_ADAPTERS_MAX_CONNECTIONS=50 \
    RNAPDBEE_ADAPTERS_MAX_IDLE_TIME=60 \
    RNAPDBEE_ADAPTERS_MAX_LIFE_TIME=180 \
    RNAPDBEE_ADAPTERS_PENDING_ACQUIRE_TIMEOUT=60 \
    RNAPDBEE_ADAPTERS_EVICT_IN_BACKGROUND=120

EXPOSE 8081

COPY --from=builder /app/target/rnapdbee-engine-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-XX:MaxRAMPercentage=75.0", "-XX:+UseZGC", "-jar", "/app.jar"]
