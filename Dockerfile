# syntax=docker/dockerfile:1
FROM ubuntu:20.04
ENV DEBIAN_FRONTEND=noninteractive

# Set application environment variables
ENV CACHE_EVICT_SPAN_MILLISECONDS=3600000 \
    ADAPTERS_HOST=http://rnapdbee-adapters-container \
    RNAPDBEE_ADAPTERS_MONO_CACHE_DURATION=3600 \
    RNAPDBEE_ADAPTERS_MAX_CONNECTIONS=50 \
    RNAPDBEE_ADAPTERS_MAX_IDLE_TIME=60 \
    RNAPDBEE_ADAPTERS_MAX_LIFE_TIME=180 \
    RNAPDBEE_ADAPTERS_PENDING_ACQUIRE_TIMEOUT=60 \
    RNAPDBEE_ADAPTERS_EVICT_IN_BACKGROUND=120

# Install OpenJDK-11, Maven and curl
RUN apt-get update -y && \
    apt-get install -y openjdk-11-jre-headless \
            maven \
            curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Run tests build jar with dev Spring profile
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean test --no-transfer-progress && \
    mvn -f /home/app/pom.xml clean package -Pdev --no-transfer-progress -Dmaven.test.skip=true && \
    mv /home/app/target/rnapdbee-engine-0.0.1-SNAPSHOT.jar /app.jar && \
    rm -rf /home/app

# Copy & set entrypoint to jar file
EXPOSE 8081
ENTRYPOINT ["java", \
            "-XX:+ExitOnOutOfMemoryError", \
            "-jar", \
            "/app.jar"]
