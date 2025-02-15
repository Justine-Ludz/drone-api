# drone-api
# Spring Boot Drone Management System

This is a Spring Boot application for managing a fleet of 10 drones and their assigned medicines.

## Features
- Manage drones with weight and battery capacity constraints.
- Assign medicines to drones.
- RESTful API for interacting with the system.

### additional details
- Scheduler works in a 15 second intervals. 
- drone will enter loading state if load is near max capacity or full
- LIGHTWEIGHT(200g), MIDDLEWEIGHT(400g), CRUISERWEIGHT(600g), HEAVYWEIGHT(1000g)

==========

## Requirements
- Java 17 (JDK 17)
- Gradle
- H2 Database (Embedded)
--- IntelliJ

## Build the Application
- ./gradlew build


## Run the application
java -jar build/libs/drone-0.0.1-SNAPSHOT.jar


## Run application in IntelliJ terminal
./gradlew bootrun


## default port= 8085
## run with a custom port
java -jar build/libs/drone-0.0.1-SNAPSHOT.jar --server.port=9090


## Test the application
-- use postman collection included in this zip file.
-- the program does not have preloaded data. 
-- use postman to interact with the api


## junit tests are included in the project


## to access H2 database
http://localhost:8085/h2-console/

Driver Class : org.h2.Driver
JDBC URL: jdbc:h2:mem:testdb
username : user
password :
	(leave as blank)

