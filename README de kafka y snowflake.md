Requirements

- Java 21+
- Maven 3.9+
- Kafka broker (local or remote)
- MongoDB (local or remote)
- Snowflake account + credentials

---

Snowflake Connection

Snowflake connectivity is done using JDBC.

---
Required Environment Variables

The service reads Snowflake configuration from environment variables:

- 'SNOWFLAKE_URL' (example: 'MDSTMWT-QF72263.snowflakecomputing.com')
- 'SNOWFLAKE_USER'
- 'SNOWFLAKE_PASSWORD'
- 'SNOWFLAKE_WAREHOUSE' (default: 'COMPUTE_WH')
- 'SNOWFLAKE_DATABASE' (default: 'SNOWFLAKE_SAMPLE_DATA')
- 'SNOWFLAKE_SCHEMA' (default: 'TPCDS_SF100TCL')
- 'SNOWFLAKE_ROLE' (default: 'ACCOUNTADMIN')

---

MongoDB Connection

MongoDB is used to read customer documents already consumed from Kafka.

Environment Variable

- 'MONGODB_URI' (default: 'mongodb://localhost:27017/customerdb')

Kafka Configuration

Kafka is used to publish messages to be consumed and persisted into MongoDB.

Environment Variable

- 'KAFKA_BOOTSTRAP_SERVERS' (default: 'localhost:9092')

Kafka Topic

It is on 'application.properties':

- 'kafka.topic=customer-topic'

---

How to Run

1. Export Environment Variables

Windows PowerShell
powershell
$env:SNOWFLAKE_URL="MDSTMWT-QF72263.snowflakecomputing.com"
$env:SNOWFLAKE_USER="YOUR_USER"
$env:SNOWFLAKE_PASSWORD="YOUR_PASSWORD"
$env:SNOWFLAKE_WAREHOUSE="COMPUTE_WH"
$env:SNOWFLAKE_DATABASE="SNOWFLAKE_SAMPLE_DATA"
$env:SNOWFLAKE_SCHEMA="TPCDS_SF100TCL"
$env:SNOWFLAKE_ROLE="ACCOUNTADMIN"

$env:KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
$env:MONGODB_URI="mongodb://localhost:27017/customerdb"

2. Start Dependencies (Kafka + MongoDB)

If you are using Docker Compose:

bash
docker compose up -d

Note: i ran it on the same directory i had docker-compose.yml and if it shows errors run first 'docker compose down -v'

3. Start the Microservice

bash:
mvn clean spring-boot:run


The API will start on:

* 'http://localhost:8080'

---

## API Endpoints

### List customers (from Snowflake)

'GET /api/customers?page=0&size=10' or 'POST /api/customers'

### Fetch one customer and send to Kafka

'GET /api/customers/fetch/{id}' or 'POST /api/customers/fetch'

### Read customers from MongoDB (consumed data)

'GET /api/customers/mongo'

---

## Actuator

* 'GET /actuator/health'
* 'GET /actuator/info'

---------------------
'kafka-consumer/README.md'

md
Kafka Consumer (Kafka -> MongoDB)

This service consumes 'CustomerMessageDto' events from a Kafka topic and persists them into MongoDB.

Features

- Listens to Kafka topic 'customer-topic'
- Deserializes JSON messages into 'CustomerMessageDto'
- Saves the consumed message into MongoDB collection 'customer_data'
- Adds 'receivedAt' timestamp on insertion

---

Requirements

- Java 21+
- Maven 3.9+
- Kafka broker
- MongoDB

---

Kafka Configuration

Kafka configuration is defined in 'application.properties'.

Environment Variable

- 'KAFKA_BOOTSTRAP_SERVERS' (default: 'localhost:9092')

Consumer Group

- 'spring.kafka.consumer.group-id=customer-data-pipeline'

Topic

- 'kafka.topic=customer-topic'

---

MongoDB Configuration

Environment Variable

- 'MONGODB_URI' (default: 'mongodb://localhost:27017/customerdb')

MongoDB Details

- Database: 'customerdb'
- Collection: 'customer_data'

Each consumed message is stored as a document including:

- 'customerId'
- 'firstName'
- 'lastName'
- 'birthDay', 'birthMonth', 'birthYear'
- 'email'
- 'address' (embedded object)
- 'receivedAt' (timestamp inserted by the consumer)

---

How to Run

1. Start Dependencies (Kafka + MongoDB)

Using Docker Compose:
bash
docker compose up -d

2. Export Environment Variables

**Windows PowerShell**

powershell
$env:KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
$env:MONGODB_URI="mongodb://localhost:27017/customerdb"

3. Start the Consumer Service

bash
mvn clean spring-boot:run

---

Verifying MongoDB Inserts

You can verify inserts in MongoDB (example using mongosh):

bash

mongosh
use customerdb
db.customer_data.find().limit(5).pretty()


---

Notes

* If MongoDB is unavailable, the consumer will throw an exception and log the failure.
* Make sure the topic exists and Kafka is reachable at localhost:9092 (or your configured bootstrap servers).
