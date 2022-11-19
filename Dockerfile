# syntax=docker/dockerfile:1
FROM ubuntu:20.04
ARG LICENSE_PATH
ENV DEBIAN_FRONTEND=noninteractive

# Set gurobi env variables
ENV GUROBI_HOME=/opt/gurobi1000/linux64
ENV PATH=$PATH:$GUROBI_HOME/bin
ENV LD_LIBRARY_PATH=$GUROBI_HOME/lib

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
COPY $LICENSE_PATH /opt/gurobi100/license
ENV GRB_LICENSE_FILE=/opt/gurobi100/license
# Disable gurobi log messages
RUN echo "OutputFlag 0" > gurobi.env

# Copy & set entrypoint to jar file
ARG JAR_FILE=target/*.jar
COPY $JAR_FILE app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]
