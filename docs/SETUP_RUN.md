Setup & Run — Backend (Postpaid Billing System)

Prerequisites
- Java 21 (installed and on PATH)
- Maven 3.8+
- Docker & Docker Compose (for local DB or running services)

Build
```powershell
cd C:\workspace\postpaid-billing-system\backend
mvn -DskipTests=false clean package
```

Run tests and generate coverage
```powershell
cd C:\workspace\postpaid-billing-system\backend
mvn -DskipTests=false test jacoco:report
# open report:
start target\site\jacoco\index.html
```

Run locally (application)
- From IDE: run the Spring Boot `main` class in `backend/src/main/java`.
- Using Maven:
```powershell
mvn spring-boot:run
```

Docker Compose
- A `docker-compose.yml` exists at `backend/docker-compose.yml`. Use it to start dependencies (e.g., a DB) before running the app.

Environment
- Control sensitive settings via environment variables or application profile `application.yml`.
- Keystore files exist under `src/main/resources/keystore` in the project — do NOT commit production keystores.

Useful commands
- Compile only:
```powershell
mvn -DskipTests=true test-compile
```
- Run a single test class:
```powershell
mvn -DskipTests=false -Dtest=InvoiceServiceImplTest test
```

CI recommendations
- Run `mvn test jacoco:report` in CI and publish `backend/target/site/jacoco` as an artifact.
- Fail builds on critical test failures; optionally fail on minimum coverage thresholds.
