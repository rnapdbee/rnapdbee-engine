# syntax=docker/dockerfile:1
FROM ubuntu:20.04
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
ENV ADAPTERS_HOST=http://rnapdbee-adapters-container
ENV RNAPDBEE_ADAPTERS_MONO_CACHE_DURATION=3600
ENV RNAPDBEE_ADAPTERS_MAX_CONNECTIONS=50
ENV RNAPDBEE_ADAPTERS_MAX_IDLE_TIME=60
ENV RNAPDBEE_ADAPTERS_MAX_LIFE_TIME=180
ENV RNAPDBEE_ADAPTERS_PENDING_ACQUIRE_TIMEOUT=60
ENV RNAPDBEE_ADAPTERS_EVICT_IN_BACKGROUND=120
# Install curl
RUN apt-get update -y && \
    apt-get install -y curl
# Install OpenJDK-11 and Maven
RUN apt-get update && \
    apt-get install -y openjdk-11-jre-headless && \
    apt-get install -y maven && \
    apt-get clean
# Install gurobi & add gurobi.jar to local maven repository
RUN curl -L https://packages.gurobi.com/10.0/gurobi10.0.0_linux64.tar.gz > gurobi10.0.0_linux64.tar.gz \
    && tar -xvf gurobi10.0.0_linux64.tar.gz --directory /opt
RUN mvn install:install-file -Dfile=$GUROBI_HOME/lib/gurobi.jar \
     -DgroupId=com -DartifactId=gurobi -Dversion=10.0.0 -Dpackaging=jar -DgeneratePom=true
# Set up Gurobi WLS license file
COPY $LICENSE_PATH /opt/gurobi1000/license
ENV GRB_LICENSE_FILE=/opt/gurobi1000/license
# Disable gurobi log messages
RUN echo "OutputFlag 0" > gurobi.env

# Run tests build jar with dev Spring profile
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean test --no-transfer-progress
RUN mvn -f /home/app/pom.xml clean package -Pdev --no-transfer-progress -Dmaven.test.skip=true
RUN mv /home/app/target/rnapdbee-engine-0.0.1-SNAPSHOT.jar /app.jar
RUN rm -rf /home/app

# Copy & set entrypoint to jar file
EXPOSE 8081
ENTRYPOINT ["java", \
            "-jar", \
            "/app.jar"]
