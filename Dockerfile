# syntax=docker/dockerfile:1
FROM ubuntu:20.04
ENV DEBIAN_FRONTEND=noninteractive

# Gurobi license path from which it is copied to the image
ARG LICENSE_PATH
ARG GUROBI_LICENSE_LOCATION_IN_IMAGE=/opt/gurobi1000/license
# Set gurobi env variables
ENV GUROBI_HOME=/opt/gurobi1000/linux64
ENV PATH=$PATH:$GUROBI_HOME/bin \
    LD_LIBRARY_PATH=$GUROBI_HOME/lib \
    GRB_LICENSE_FILE=$GUROBI_LICENSE_LOCATION_IN_IMAGE
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
# Install gurobi & add gurobi.jar to local maven repository
RUN curl -L https://packages.gurobi.com/10.0/gurobi10.0.0_linux64.tar.gz > gurobi10.0.0_linux64.tar.gz \
    && tar -xvf gurobi10.0.0_linux64.tar.gz --directory /opt && \
    mvn install:install-file -Dfile=$GUROBI_HOME/lib/gurobi.jar \
        -DgroupId=com -DartifactId=gurobi -Dversion=10.0.0 -Dpackaging=jar -DgeneratePom=true
# Set up Gurobi WLS license file
COPY $LICENSE_PATH $GUROBI_LICENSE_LOCATION_IN_IMAGE
# Disable gurobi log messages
RUN echo "OutputFlag 0" > gurobi.env

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
            "-jar", \
            "/app.jar"]
