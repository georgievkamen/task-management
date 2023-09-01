# Task Management - REST Server
The Ethereum Fetcher REST Server acts as a layer between users and the Ethereum Node, providing an API for fetching Ethereum transactions.

# Prerequisites

## Installing
- Simply clone this repo using: `git clone`

## Configuration
Ensure you have all the following env variables provided in an `.env file`:
- `DB_CONNECTION_URL` - url for connecting with your database

## Running
Ensure you have the following auxiliary services running before starting:
- PostgreSQL database. Run with: `docker-compose up -d database`, or `docker run -d \ --name task-management-postgres \ -e POSTGRES_USER=developer -e POSTGRES_PASSWORD=developer -e POSTGRES_DB=postgres \ -p 5432:5432/tcp postgres`

Run the service with one of the following commands:
- gradle run
- ./gradlew run
- or simply use your IDE to run it

## Technologies
- Spring Boot 3
- Postgres
- Hibernate ORM
