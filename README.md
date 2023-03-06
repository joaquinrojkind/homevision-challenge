# homevision-challenge

## Github project

Clone project locally

## Build and Run app from terminal

Install Maven build tool in MacOS

```brew install maven```

Build project

```mvn clean install```

Run app

```mvn spring-boot:run```

## Invoke app's API

Call the following endpoint using Postman or similar client in order to trigger the integration

```GET http://localhost:8080/homevision-challenge/houses```

## Photo downloads

After the request has been processed successfully you can find all the downloaded photo files in the following directory within the project

```src/main/resources/photos```

## API's response

The API will return all the requested houses and their details in json format

## Unit Tests

The main service class ```HouseServiceImpl``` has been unit tested through the following test class

```src/test/java/com/homevision/service/HouseServiceImplTest.java```

More unit test coverage should be added. Some good candidate classes are ```ResilientCallExecutor``` and ```ParallelTaskRunner```