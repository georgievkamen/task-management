# Task Management - REST Server

# Prerequisites

## Installing
- Simply clone this repo using: `git clone`

## Running
Ensure you have the following auxiliary services running before starting:
- PostgreSQL database. Run with `docker run -d \ --name task-management-postgres \ -e POSTGRES_USER=developer -e POSTGRES_PASSWORD=developer -e POSTGRES_DB=postgres \ -p 5432:5432/tcp postgres`

Run the service with one of the following commands:
- gradle bootRun
- ./gradlew bootRun
- or simply use your IDE to run it

## Technologies
- Kotlin 1.8.22
- JVM 17
- Spring Boot 3
- Postgres
- Hibernate ORM

## Example requests

```jsx
curl -X POST -H "Content-Type: application/json" -d '{' 
'"title": "Your Project Title", ' 
'"description": "Your Project Description",' 
' "companyId": 1,' 
' "clientId": null, ' 
'"taskIds": [1, 2]}'
http://localhost:8080/api/projects
```

```jsx
curl -v -X GET http://localhost:8080/api/projects
```

```jsx
curl -X POST -H "Content-Type: application/json" -d '{
  "name": "Your Task Name",
  "status": "NEW",
  "duration": "2h30m",
  "projectId": 1
}' http://localhost:8080/api/tasks
```

There are also front end pages for http://localhost:8080/api/tasks and http://localhost:8080/api/projects
