# ShareIt

ShareIt is a Java/Spring Boot backend service that lets people share the things they own with others. Users can list items they're willing to lend, book items owned by other users, and post requests for items that aren't available yet. While an item is booked, it's automatically hidden from other renters for the duration of the booking.

## Features

- **Items** — add, update, and search items available for sharing.
- **Bookings** — book an item for a date range; owners approve or reject booking requests; items are unavailable to others while booked.
- **Item requests** — ask for an item that isn't currently listed, and let other users respond by offering it.
- **Comments** — leave a comment on an item after having booked it.
- **Users** — basic user registration and management.

## Architecture

The project is a two-module Maven build:

| Module | Port | Responsibility |
|---|---|---|
| `gateway` | 8080 | Public-facing entry point. Validates incoming requests and forwards them to the server. |
| `server` | 9090 | Core business logic, persistence, and data access. |

Requests flow: `client → gateway (validation) → server (business logic) → PostgreSQL`.

## Tech stack

- Java 11
- Spring Boot 2.7
- Spring Data JPA / Hibernate
- PostgreSQL (H2 for tests)
- Maven
- Lombok
- Docker & Docker Compose
- JUnit 5, Mockito, MockMvc

## Getting started

### Prerequisites

- JDK 11+
- Apache Maven 3.6+
- Docker and Docker Compose

### Run with Docker Compose

```bash
git clone https://github.com/ku-alexej/java-shareit.git
cd java-shareit
docker-compose up --build
```

This starts three containers:
- `db` — PostgreSQL 13 (exposed on host port `6541`)
- `server` — the business-logic service (port `9090`)
- `gateway` — the public API (port `8080`)

Once running, the API is available at `http://localhost:8080`.

### Build locally

```bash
mvn clean install
```

## API overview

All endpoints are exposed through the gateway (`http://localhost:8080`).

| Resource | Endpoints |
|---|---|
| Users | `POST /users`, `GET /users`, `GET /users/{id}`, `PATCH /users/{id}`, `DELETE /users/{id}` |
| Items | `POST /items`, `GET /items`, `GET /items/{itemId}`, `PATCH /items/{itemId}`, `GET /items/search`, `POST /items/{itemId}/comment` |
| Bookings | `POST /bookings`, `PATCH /bookings/{bookingId}`, `GET /bookings/{bookingId}`, `GET /bookings`, `GET /bookings/owner` |
| Item requests | `POST /requests`, `GET /requests`, `GET /requests/all`, `GET /requests/{requestId}` |

Most endpoints expect an `X-Sharer-User-Id` header identifying the current user.

## Testing and code coverage

The `server` module is covered by unit tests (service and controller layers with Mockito/MockMvc) and integration/repository tests backed by an in-memory H2 database (JUnit 5, Spring Boot Test).

Code coverage is measured with the **JaCoCo** Maven plugin, which is wired into the build (`prepare-agent` → `test` → `report` → `check`). The `check` goal enforces minimum coverage thresholds on the codebase:

- **Line coverage:** ≥ 90%
- **Branch coverage:** ≥ 60%
- **Cyclomatic complexity coverage:** ≥ 60%

To run the tests and generate a coverage report locally:

```bash
mvn clean verify
```

The HTML report is generated at `server/target/site/jacoco/index.html` — open it in a browser to see line-by-line and package-by-package coverage. The build will fail if coverage drops below the configured thresholds.

## License

No license file is currently provided in this repository.
