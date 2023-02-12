# Quote Voter
Quote Voter is a Spring Boot application for managing quotes and voting on them.

## Docker Hub Repository
The Docker image for this application is available at `aydarsh/quotevoter` repository on Docker Hub.

## API Access
The API for this application is accessible via `http://localhost:8080/api`.

API Documentation
The API documentation can be accessed at http://localhost:8080/swagger-ui.html once the application is running.

## Functionality
- User account creation
- Quote creation, viewing, modification, and deletion
- Upvoting and downvoting quotes
- Viewing the top 10 quotes and details of each quote
- Viewing the evolution of votes over time with a graph

## GitHub Actions CI Pipeline
The repository has a GitHub Actions CI pipeline configured to build a Docker image and push it to Docker Hub. In order to build a Docker image with a SemVer or `latest` tag, the repository must be tagged and the tag must be pushed.

## Endpoints
- `/api/users`: Endpoint for creating and managing user accounts.
- `/api/quotes`: Endpoint for adding, viewing, modifying, and deleting quotes.
- `/api/votes`: Endpoint for voting on quotes.
- `/api/quotes/top`: Endpoint for viewing the top 10 quotes based on their vote count.
- `/api/quotes/{quoteId}`: Endpoint for viewing the details of a specific quote.
- `/api/quotes/{quoteId}/votes`: Endpoint for viewing the vote history of a specific quote.

## Requirements
The following tools are required to run this application:
- Java 17 or higher
- Docker and Docker Compose
- Maven

## Running the Application
You can run this application using Docker and Docker Compose. Simply run the following command in the root directory of the application:
```
docker-compose up --pull always
```
This will start the application and you can access the API at `http://localhost:8080/api`.
