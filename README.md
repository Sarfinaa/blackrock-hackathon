# Retirement Savings API

BlackRock Coding Challenge — Retirement Savings API built with Spring Boot and Java 23.

## Configuration

- **Port:** 5477 (configured in `src/main/resources/application.properties`)
- **Java Version:** 23
- **Spring Boot:** 3.4.5
- **Build Tool:** Maven

## Run Locally

```bash
# Build
./mvnw clean package

# Run
java -jar target/retirement-savings-api-1.0.0.jar
```

The API will be available at `http://localhost:5477`.

## Run with Docker

```bash
# Build image
docker build -t blk-hacking-ind-karan-seth .

# Run container
docker run -p 5477:5477 blk-hacking-ind-karan-seth

# Docker Image Image
docker pull karanseth/blk-hacking-ind-karan-seth:latest

```
## Youtube video link
https://www.youtube.com/watch?v=k4lj4swGX98

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/blackrock/challenge/v1/transactions:parse` | Calculate ceiling and remanent |
| POST | `/blackrock/challenge/v1/transactions:validator` | Validate transactions |
| POST | `/blackrock/challenge/v1/transactions:filter` | Apply temporal constraints |
| POST | `/blackrock/challenge/v1/returns:nps` | Calculate NPS returns |
| POST | `/blackrock/challenge/v1/returns:index` | Calculate Index Fund returns |
| GET | `/blackrock/challenge/v1/performance` | System performance metrics |

## Test

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CeilingServiceTest
```

## Project Structure

```
src/main/java/com/blackrock/challenge/
├── Application.java          # Entry point
├── controller/               # REST controllers
├── service/                  # Business logic
├── model/                    # Domain models
├── dto/                      # Request/Response DTOs
└── util/                     # Shared utilities
```
