# Log Analyser

`Log Analyser` is a Spring Boot service designed to automate the triage of application crash logs and stack traces using an LLM. It provides an asynchronous API to submit logs, which are then analyzed by an AI model to determine root causes, assess impact, and suggest recovery actions.

## Features

- **Asynchronous Log Processing**: Submit logs via a REST API and let the service process them in the background.
- **AI-Powered Triage**: Leverages `Spring AI` and `Ollama` (defaulting to `llama3`) to perform expert-level SRE analysis.
- **Structured Reports**: Generates JSON reports including:
    - Incident Categorization (Database, Network, Auth, etc.)
    - Root Cause Analysis
    - Impact Summary
    - Recommended Recovery Action
- **Persistent Storage**: Stores incidents and triage reports in a database (H2 for development).
- **Interactive Documentation**: Integrated Swagger UI for API exploration.

## Tech Stack

- **Java 21**
- **Spring Boot 4.x** (with Web, Data JPA, Validation)
- **Spring AI**: Integration with AI models.
- **Ollama**: Local LLM runner.
- **Lombok**: Reduced boilerplate.
- **H2 Database**: In-memory storage (configurable).
- **SpringDoc OpenAPI**: Swagger UI documentation.

## Architecture

The code is split into domain and infrastructure packages to keep the core business logic separate from 
the details of persistence and AI integration. This separation enhances maintainability and testability.

The use of Spring AI means that, although development has been done against a local LLM running in Ollama,
modifications to use other LLM providers or cloud services can be made with minimal impact on the core business logic.

Similarly, development and testing has used the in memory H2 database, but conversion to other databases is straightforward.


## Prerequisites

1.  **Java 21** or higher.
2.  **Maven 3.x**.
3.  **Ollama**: Install and run Ollama locally.
    - Pull the default model: `ollama pull llama3`

## Quick Start

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd logAnalyser
    ```

2.  **Run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```

3.  **Access the API Documentation**:
    Open your browser and navigate to: `http://localhost:8080/swagger-ui.html`
    Note that the stack trace must be escaped for inclusion in a json object before pasting into the Swagger 
    interface. 

## API Usage

### 1. Submit an Incident for Analysis

**Endpoint**: `POST /logs/api/v1/analyser/submit`

**Request Body**:
```json
{
  "serviceName": "payment-service",
  "environment": "production",
  "timestamp": "2026-05-21T13:17:00Z",
  "rawLogDump": "java.net.ConnectException: Connection refused (Connection refused)\n    at java.base/jdk.internal.reflect.DirectConstructorHandleAccessor.newInstance(DirectConstructorHandleAccessor.java:62)..."
}
```

**Response** (202 Accepted):
```json
{
  "incidentId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 2. Retrieve Triage Report

**Endpoint**: `GET /logs/api/v1/analyser/get/{id}`

**Response** (200 OK):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "category": "NETWORK",
  "rootCause": "Connection refused to downstream service.",
  "impactSummary": "Payments failing for 100% of users.",
  "recommendedAction": "Check network connectivity and downstream service health.",
  "telemetry": {
    "tokenCount": 12,
    "inferenceDuration": "PT1.2S",
    "tokensPerSecond": 10.0
  }
}
```

## Design Trade offs
In a production environment, consider the following trade-offs:

I have not considered security in this test/demonstration application, in a production setting one would want to 
implement OAuth2 or similar to secure the endpoints. 

The simple in-memory processing used here may not be sufficient at scale, where a Kafka or similar queue might be 
more appropriate (depending on load). 

No consideration is given to log retention timescales, the database will simply grow forever. As the in memory 
H2 database is blanked at every re-start, this is not a problem for the test application. However, a production deployment
would need to take retention policies into account to manage database size and performance as well as regulatory concerns.


## Configuration

Settings can be found in `src/main/resources/application.properties`:

- `spring.ai.ollama.chat.options.model`: The LLM model to use (default: `llama3`).
- `spring.datasource.*`: Database connection settings.

## License

This project is licensed under the terms of the [LICENSE](LICENSE) file.
