# SE Authentication Service
FH Campus Wien - SDE - Service Engineering - Final Project Part 2

The SE Authentication Service is a Spring Boot application designed to handle user authentication processes. This service includes functionalities for user registration, login, and secure access token generation using JWT (JSON Web Tokens).

## Key Features
- **MongoDB Integration:** Utilizes MongoDB for storing and retrieving user data.
- **JWT-Based Authentication:** Implements JWT to ensure secure transmission of authentication credentials.
- **Exception Handling:** Custom exception handling for scenarios like "Email Already in Use."

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to install the software and how to install them:

- Java JDK 17 or later
- Maven 4.0.0
- MongoDB

### Installing

A step-by-step series of examples that tell you how to get a development environment running:

1. Ensure that MongoDB is running and accessible.
2. Clone the repository:
```bash
git clone https://github.com/JasmineYil/se-authentication-service.git
```
2. Navigate to the project directory:
```bash
cd se-authentication-service
```
3. Build the project with Maven:
```bash
mvn clean install
```
4. Run the application:
```bash
mvn spring-boot:run
```
The service will start on port 9092 as specified in the application.properties file.

### Usage
Once the application is running, you can access the Authentication Service at:
```bash
http://localhost:9092/
```
## Environmental Variables
To run this application, you will need to add the following environment variables to your .env file:

`MONGO_DB_USERNAME`, `MONGO_DB_PASSWORD`, `MONGO_DB_NAME`, `MONGO_DB_PORT`, `MONGO_DB_HOST`, `JWT_SECRET_KEY`, `JWT_EXPIRATION_TIME`, `SERVER_PORT`