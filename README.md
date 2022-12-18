# RNApdbee engine

Project generated with [Spring](https://start.spring.io/) version 2.6.6.

## Requirements

- Maven
- Java 11 (SDK 11)
- Docker
- Gurobi 10.0.0 set up and with proper license

## Prerequisites

After installing Gurobi, it has to be added to local mvn repository. You can achieve it by running in the console:
mvn install:install-file "-Dfile=<path_to_gurobi_jar>" "-DgroupId=com" "-DartifactId=gurobi" "-Dversion=10.0.0" "
-Dpackaging=jar" "-DgeneratePom=true"
exemplary path to gurobi file would be:

- C:\gurobi1000\win64\lib\gurobi.jar for Windows environment
- /usr/share/java/gurobi/gurobi.jar for Linux environment

## Build

The server starts on the localhost `8081` port -> http://localhost:8081/

### With docker

There is currently no possibility to run this project properly in dockerized, local environment, as it needs Gurobi
license to run.

### Building on the environment

The project is prepared to be built as a docker image on the environment. After downloading the repository, you may
build the image using:
`docker build --build-arg LICENSE_PATH='<path_to_gurobi_wls_license>' -t <image_name> .`

For automatic build and deployment suggest using the `deployment.sh` script.
Usage: `deployment.sh <PATH_TO_GUROBI_LICENSE_FILE>`

## Documentation

When the project is working auto generated swagger endpoint documentation is available at the
following [LINK](http://localhost:8081/swagger-ui.html).
