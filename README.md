Customer Data Pipeline – Technical Assessment

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Overview

This project implements a customer data pipeline with Spring Boot, using:

--Snowflake as the source of customer data

--Apache Kafka as the messaging layer

--MongoDB as the database

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

The application exposes REST endpoints to:

--Retrieve customers from Snowflake

--Fetch a customer by ID and publish it to Kafka

--Consume customer messages from Kafka and persist them into MongoDB

The project includes unit tests with JUnit and Mockito covering controllers, services, Kafka consumers, and Snowflake client logic.

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Technology Stack:

--Java 21

--Spring Boot 3.5.x

--Apache Kafka

--MongoDB

--Snowflake (JDBC)

--Maven

--JUnit + Mockito

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Application Architecture:

This represents the workflow of the application:
Java
  |
  |
  ▼
Snowflake
  │
  │ 
  ▼
CustomerService
  │
  │ (Kafka Producer)
  ▼
Kafka Topic
  │
  │ (Kafka Consumer)
  ▼
MongoDB

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Project Structure:

This is the resulting project tree:

com.zaid.examen
├── controller       
├── service          
├── dto              
├── enums            
├── exception        
├── logs             
├── util
│   ├── kafka
│   │   ├── producer
│   │   └── consumer
│   ├── mongo
│   │   ├── model
│   │   └── repository
│   └── snowflake
└── ExamenApplication

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

REST Endpoints:
1️. Get customers (Using Snowflake and giving the results paginated)

GET /api/customers?page=0&size=10

Response

{
    "codigo": "200.EXAMEN.CUSTOMERS.LIST.1",
    "id": "e459fa6a-00e1-4da4-823d-e44359d8282a",
    "mensaje": "La petición se ha procesado exitosamente.",
    "respuesta": [
        {
            "id": 1,
            "firstName": "Javier",
            "lastName": "Lewis",
            "email": "Javier.Lewis@VFAxlnZEvOx.org"
        },
        {
            "id": 2,
            "firstName": "Amy",
            "lastName": "Moses",
            "email": "Amy.Moses@Ovk9KjHH.com"
        },
        {
            "id": 3,
            "firstName": "Latisha",
            "lastName": "Hamilton",
            "email": "Latisha.Hamilton@V.com"
        },
        {
            "id": 4,
            "firstName": "Michael",
            "lastName": "White",
            "email": "Michael.White@i.org"
        },
        {
            "id": 5,
            "firstName": "Robert",
            "lastName": "Moran",
            "email": "Robert.Moran@Hh.edu"
        }
    ]
}

2️. Fetch customer and send to Kafka

GET /api/customers/fetch/{id}

Reads customer + address from Snowflake

Publishes the message to Kafka

Kafka consumer stores it in MongoDB

Response:

{
    "codigo": "200.EXAMEN.CUSTOMERS.FETCH.1",
    "id": "d7b731f4-7234-4e84-99b2-4917dc49a141",
    "mensaje": "Customer sent to Kafka."
}

3️. Get customers stored in MongoDB

GET /api/customers/mongo

Response:

{
    "codigo": "200.EXAMEN.CUSTOMERS.MONGO.1",
    "id": "3bd8caa4-d07d-43d4-a601-65ab0151c695",
    "mensaje": "La petición se ha procesado exitosamente.",
    "respuesta": [
        {
            "id": "69705c7e9fca2ab962937418",
            "customerId": 28437730,
            "firstName": "Cheryl",
            "lastName": "Ramsey",
            "birthDay": 16,
            "birthMonth": 1,
            "birthYear": 1965,
            "email": "Cheryl.Ramsey@uaq4mXuuV3.org",
            "address": {
                "street": "Spring ",
                "city": "Oak Grove",
                "state": "ND",
                "country": "United States",
                "street_number": null
            },
            "receivedAt": "2026-01-21T04:56:30.689Z"
        },
        {
            "id": "6970601edf2ed770243fd4ba",
            "customerId": 28437730,
            "firstName": "Cheryl",
            "lastName": "Ramsey",
            "birthDay": 16,
            "birthMonth": 1,
            "birthYear": 1965,
            "email": "Cheryl.Ramsey@uaq4mXuuV3.org",
            "address": {
                "street": "Spring ",
                "city": "Oak Grove",
                "state": "ND",
                "country": "United States",
                "street_number": "300"
            },
            "receivedAt": "2026-01-21T05:11:58.388Z"
        },
        {
            "id": "6970742d0419af4f2d033e28",
            "customerId": 28437730,
            "firstName": "Cheryl",
            "lastName": "Ramsey",
            "birthDay": 16,
            "birthMonth": 1,
            "birthYear": 1965,
            "email": "Cheryl.Ramsey@uaq4mXuuV3.org",
            "address": {
                "street": "Spring ",
                "city": "Oak Grove",
                "state": "ND",
                "country": "United States",
                "street_number": "300"
            },
            "receivedAt": "2026-01-21T06:37:33.632Z"
        }
    ]
}

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Configurations:

--------------------------------------------------------------------
application.properties:

spring.application.name=Examen
server.port=8080

# Kafka
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
spring.kafka.consumer.group-id=customer-data-pipeline
kafka.topic=customer-topic

# MongoDB
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/customerdb}

# Snowflake
snowflake.url=${SNOWFLAKE_URL}
snowflake.user=${SNOWFLAKE_USER}
snowflake.password=${SNOWFLAKE_PASSWORD}
snowflake.warehouse=${SNOWFLAKE_WAREHOUSE:COMPUTE_WH}
snowflake.database=${SNOWFLAKE_DATABASE:SNOWFLAKE_SAMPLE_DATA}
snowflake.schema=${SNOWFLAKE_SCHEMA:TPCDS_SF100TCL}
snowflake.role=${SNOWFLAKE_ROLE:ACCOUNTADMIN}

--------------------------------------------------------------------
Environment Variables:

SNOWFLAKE_URL=xxxx.snowflakecomputing.com
SNOWFLAKE_USER=your_user
SNOWFLAKE_PASSWORD=your_password
SNOWFLAKE_WAREHOUSE=COMPUTE_WH
SNOWFLAKE_DATABASE=SNOWFLAKE_SAMPLE_DATA
SNOWFLAKE_SCHEMA=TPCDS_SF100TCL
SNOWFLAKE_ROLE=ACCOUNTADMIN

KAFKA_BOOTSTRAP_SERVERS=localhost:9092
MONGODB_URI=mongodb://localhost:27017/customerdb

--------------------------------------------------------------------
JVM Options:

Due to Java 21 and Kafka serialization internals:

--add-opens=java.base/java.nio=ALL-UNNAMED

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Docker Compose – Problems

Why was docker-compose.yml modified?

The Docker Compose configuration was unintentionally modified because the provided docker-compose.yml at the moment of the docker #docker compose up -d it showed two errors, one on the zookeper and one on the kafka part,
so i had to do reasearch and asking with Isabel i was given the green light to modify this part only to continue the test and also to use docker because with the provided one it showed error on the :latest part.
On my research i found that this versions worked with the application i was trying to use.

Pages used:
https://dev.to/gilson_oliveira/dont-let-bitnami-changes-break-your-kafka-pipelines-migrate-to-kraft-mode-now-4ol4
https://stackoverflow.com/questions/73382919/what-is-the-difference-between-bitnami-kafka-and-confluentinc-cp-kafka

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Unit testing:

I just made some unit tests for what i considered was the important parts, i know it requires a lot more tests.

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Running the Application:

1️. Start infrastructure services:

docker-compose up -d

Note: You have to open the terminal on the folder that you have the docker-compose.yml as it is organized on this repo.

2️. Run the application:

mvn spring-boot:run

Note: You have to run this line on the root folder of the application, in this case being the folder that contains the ExamenApplication (I used an IDE but i think it is important to mention)

3. Running Tests

All unit tests are isolated using Mockito. Ypu just have to run the next command

mvn test

Tests are for these components:

- Controllers
- Services
- Kafka Consumer
- Snowflake Client

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Error Handling & Logging

I implemented a centralized format of response for a better display, also i added some logs that reflect the requestId of the response just for tracking and debugging purposes, added a catalog of some of the 
response messages in case that later these are required to be modified and also made an ControllerAdvice in case that in the future it is required to use a personalized exception.

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Notes:

No integration tests were required for this assessment

Snowflake access is abstracted behind a client class

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Author:

Abraham Zaid Aguilar Reyes
Backend Developer
