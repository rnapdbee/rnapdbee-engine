# RNApdbee engine
Project generated with [Spring](https://start.spring.io/) version 2.6.6.

## Requirements
- Maven
- Java 11 (SDK 11)
- Docker

## Build
The server starts on the localhost `8081` port -> http://localhost:8081/

To run project in command line type:
```
mvn clean install
docker build . -t rnapdbee-engine
docker run -i -t --rm -p 8081:8081 --name rnapdbee-engine rnapdbee-engine
```




