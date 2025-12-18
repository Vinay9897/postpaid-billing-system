Backend Overview — Postpaid Billing System

Purpose
- Backend services for a postpaid billing platform: user management, customers, services, billing (invoices, payments, usage).

Key Components
- Authentication & Security: JWT-based authentication with `JwtAuthenticationFilter`, `JwtProvider`, configured in SecurityConfig.
- Controllers: REST controllers expose API endpoints for users, customers, billing (invoices/payments/usage).
- Services: Business logic lives under `com.abc.postpaid.*.service.impl` (AdminUserServiceImpl, InvoiceServiceImpl, PaymentServiceImpl, UsageRecordServiceImpl, etc.).
- Repositories: Spring Data JPA repositories for persistence (CustomerRepository, InvoiceRepository, PaymentRepository, UsageRecordRepository, ServiceRepository, UserRepository).
- DTOs & Entities: DTOs under `billing.dto`, `user.dto`, `customer.dto`; entities under `billing.entity`, `customer.entity`, `user.entity`.
- Build & Tooling: Maven-based project with JaCoCo coverage, unit tests (JUnit 5 + Mockito), Docker Compose for local services.

Project Layout (important folders)
- `backend/src/main/java` — application source
- `backend/src/test/java` — unit and integration tests
- `backend/pom.xml` — Maven build and plugins (JaCoCo config)
- `backend/target/site/jacoco` — generated coverage HTML report
- `frontend/` — separate SPA frontend (Vite + React)

Architecture Notes
- Typical Spring Boot layered architecture: controllers -> services -> repositories -> database.
- Security: stateless JWT; requests are authorized in controllers by checking authenticated user id against resource owner id where applicable.
- Persistence: JPA entities and repositories; recommended to review query performance and N+1 risks in service methods.

Where to look first (developer onboarding)
- `backend/src/main/java/com/abc/postpaid/customer/controller` — customer-related endpoints
- `backend/src/main/java/com/abc/postpaid/user/controller` — admin/user endpoints
- `backend/src/main/java/com/abc/postpaid/billing/controller` — invoice/payment/usage controllers
- `backend/src/main/java/com/abc/postpaid/*/service/impl` — business logic implementations

Next steps for maintainers
- Add service-layer unit tests for low-coverage packages.
- Add metrics and tracing for observability.
- Move secrets out of repository and into environment/secret manager.
