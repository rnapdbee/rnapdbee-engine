# syntax=docker/dockerfile:1
FROM ubuntu:20.04
ARG CONFIG_DIRECTORY=config/application-dev.properties
ENV DEBIAN_FRONTEND=noninteractive
# Gurobi license path from which it is copied to the container
ARG LICENSE_PATH
# Set gurobi env variables
ENV GUROBI_HOME=/opt/gurobi1000/linux64
ENV PATH=$PATH:$GUROBI_HOME/bin
ENV LD_LIBRARY_PATH=$GUROBI_HOME/lib
### Application environment variables
ENV CACHE_EVICT_SPAN_MILLISECONDS=3600000
# Rnapdbee-adapters related environment variables
ENV ADAPTERS_HOST=http://localhost:8000
ENV RNAPDBEE_ADAPTERS_MONO_CACHE_DURATION=3600
ENV RNAPDBEE_ADAPTERS_MAX_CONNECTIONS=50
ENV RNAPDBEE_ADAPTERS_MAX_IDLE_TIME=60
ENV RNAPDBEE_ADAPTERS_MAX_LIFE_TIME=180
ENV RNAPDBEE_ADAPTERS_PENDING_ACQUIRE_TIMEOUT=60
ENV RNAPDBEE_ADAPTERS_EVICT_IN_BACKGROUND=120

# Copy application configuration to docker container
COPY $CONFIG_DIRECTORY /config/app.properties
# Install wget
RUN apt-get update -y && \
    apt-get install -y curl
# Install gurobi
RUN curl -L https://packages.gurobi.com/10.0/gurobi10.0.0_linux64.tar.gz > gurobi10.0.0_linux64.tar.gz \
    && tar -xvf gurobi10.0.0_linux64.tar.gz --directory /opt
# Install OpenJDK-11
RUN apt-get update && \
    apt-get install -y openjdk-11-jre-headless && \
    apt-get clean;
# Set up Gurobi WLS license file
COPY $LICENSE_PATH /opt/gurobi1000/license
ENV GRB_LICENSE_FILE=/opt/gurobi1000/license
# Disable gurobi log messages
RUN echo "OutputFlag 0" > gurobi.env

# Copy & set entrypoint to jar file
ARG JAR_FILE=target/*.jar
COPY $JAR_FILE app.jar
EXPOSE 8081
ENTRYPOINT ["java", \
            "-jar", \
            "/app.jar", \
            "--spring.config.location=file:/config/app.properties"]
