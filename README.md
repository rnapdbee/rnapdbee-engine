# RNApdbee engine

Project generated with [Spring](https://start.spring.io/) version 2.6.6.

## Requirements

- Maven
- Java 11 (SDK 11)
- Docker

## Build

The server starts on the localhost `8081` port -> http://localhost:8081/

### Building on the environment

The project is prepared to be built as a docker image on the environment. After downloading the repository, you may
build the image using:
`docker build -t <image_name> .`

For automatic build and deployment suggest using the `deployment.sh` script.
Usage: `deployment.sh`

## Documentation

When the project is working auto generated swagger endpoint documentation is available at the
following [LINK](http://localhost:8081/swagger-ui.html).

## Funding

This work was supported by the National Science Centre, Poland (Narodowe Centrum Nauki) under grant No. 2023/51/D/ST6/01207, "Exploring Multiple Loop Motifs in RNA Structures".
