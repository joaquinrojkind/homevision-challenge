# homevision-challenge

## Github project

Clone project locally

## Build and Run app from terminal

Install Maven build tool in MacOS if not already installed. 

```brew install maven```

Build project from project's root directory.
This will also run the unit tests.


```mvn clean install```

Run app from project's root directory

```mvn spring-boot:run```

## Build and Run app from IDE

You can open project in an IDE such as IntelliJ:
* Update maven project (right-hand maven bar menu, click on circular arrows)
* Clean and install project (right-hand maven bar menu, expand project name and expand Lifecycle item, click on ```clean``` first then on ```install```) 
* Run following class (right click on class -> Run/Debug)

```src/main/java/com/homevision/HomevisionChallengeApplication.java```

## Invoke app's API

Call the following endpoint using Postman or similar client in order to trigger the integration

```GET http://localhost:8080/homevision-challenge/houses```

## API's response

The API will return all the requested houses and their details in json format

## Photo downloads

After the request has been processed successfully you can find all the downloaded photo files in the following directory within the project.
Remove the ```photos``` directory before calling the API again.

```src/main/resources/photos```

## Unit Tests

The main service class ```HouseServiceImpl``` has been unit tested through the following test class

```src/test/java/com/homevision/service/HouseServiceImplTest.java```

More unit test coverage should be added. Some good candidate classes are ```ResilientCallExecutor``` and ```ParallelTaskRunner```