# Test Coverage Guide

This document explains how to generate test coverage for the project, where gaps currently exist, and a prioritized plan to add tests to reach reliable coverage for the critical service methods. This file does not include any generated test code — it provides commands, analysis, and a concrete testing plan you or your team can implement.

----

## Current coverage summary (from user's report)
- Total coverage: 85%
- Missed elements: 285 of 311
- Missed branches: 15 of 16 (9%)
- Missed methods: 3
- Missed lines: 25

Most important missed methods reported for `CustomerServiceImpl` and related mapping lambdas:
- `getCustomer(Long)`
- `getCustomerByUserId(Long)`
- `updateCustomer(Long, CustomerRequest)`
- `deleteCustomer(Long)`
- Several lambda mapping functions used in list/map streams

These are high-value items to add tests for because they exercise service-layer logic and mapping that affect API behavior.

----

## How to generate a coverage report (local)

1. Open a PowerShell terminal in the repository root.
2. Run backend tests and generate Jacoco report (recommended):

```powershell
cd backend
mvn test jacoco:report
```

3. When the build completes, open the coverage report in your browser:

```powershell
start target\site\jacoco\index.html
```

Alternatively, run a single test class and then generate the report:

```powershell
cd backend
mvn -Dtest=CustomerServiceImplTest test
mvn jacoco:report
start target\site\jacoco\index.html
```

Notes:
- If your Maven lifecycle is customized or the project uses multi-module structure, run `mvn -f backend/pom.xml test jacoco:report` from the repo root.
- CI: configure the CI pipeline to run `mvn test jacoco:report` and publish the `target/site/jacoco` artifacts or use a coverage service (Codecov, Coveralls).

----

## Testing strategy and priorities

Goal: get service-level coverage to a stable baseline (80%+ for critical services), and ensure controller behavior is validated by MockMvc tests.

Priority 1 — Critical service methods (high impact):
- `CustomerServiceImpl.getCustomer(Long)`
  - Tests: success path (existing customer returned), not-found path (repository empty -> expect IllegalArgumentException or service-specific exception handling).
- `CustomerServiceImpl.getCustomerByUserId(Long)`
  - Tests: success and not-found paths.
- `CustomerServiceImpl.updateCustomer(Long, CustomerRequest)`
  - Tests: partial updates (only non-null fields change), full updates, and not-found case.
- `CustomerServiceImpl.deleteCustomer(Long)`
  - Tests: deletion success and repository behavior when id not found.

Priority 2 — Mapping lambdas and list methods:
- `listCustomers()` mapping that converts `Customer` -> `CustomerResponse` (verify null user handling, fields mapping).
- `listServicesForCustomer(Long)` mapping of `ServiceEntity` to `ServiceResponse` (verify fields and `customerId` mapping).

Priority 3 — Controller authorization and `/me` endpoint (already partially covered):
- Add MockMvc tests or integration tests that exercise `GET /api/customers/me` when the authenticated principal is a typed principal or raw `sub` string, to validate `SecurityContext` behavior under `JwtAuthenticationFilter`.

Priority 4 — Integration tests (end-to-end):
- Register -> verify `User` and `Customer` created in DB (transactional test with @SpringBootTest). Use Testcontainers or an in-memory database for CI reliability.

----

## Where to add tests (file paths suggestions)
- Unit tests for service layer: `backend/src/test/java/com/abc/postpaid/customer/service/impl/CustomerServiceImplTest.java`
- Controller MockMvc tests (already present): `backend/src/test/java/com/abc/postpaid/customer/controller/CustomerControllerTest.java` — expand as needed for negative cases and principal variants.
- Integration tests: `backend/src/test/java/com/abc/postpaid/integration/` with `@SpringBootTest` classes that use a test DB.

----

## Test design (described, no code included)

- For each target method, write unit tests that mock repositories with Mockito. Arrange the repository behavior for success and failure cases, call the service method, and assert returned DTO fields or exceptions.

- For `updateCustomer`, three tests are recommended:
  1. Update only `fullName` — assert `address` and `phoneNumber` unchanged.
  2. Update `address` and `phoneNumber` — assert `fullName` unchanged.
  3. Call with non-existing `customerId` — assert service throws `IllegalArgumentException` (or the service's chosen exception).

- For mapping lambdas, create repository-returned entities with null and non-null nested `User` values to ensure `CustomerResponse.userId` resolves correctly.

- For controller `/me` endpoint, use MockMvc and set `SecurityContextHolder` with an authentication token whose `principal` equals the test `userId` string; verify successful 200 and 404 cases.

----

## CI integration and coverage gating

- Add Jacoco plugin to CI build steps (if not present) and fail builds when overall coverage drops below a set threshold, e.g., 70% for the whole project and 80% for critical packages like `com.abc.postpaid.customer.service`.
- Example pipeline step (PowerShell / Bash snippet):

```powershell
cd backend
mvn test jacoco:report
# publish target/site/jacoco to CI artifacts or upload to coverage service
```

Notes:
- Be pragmatic: start with method-level tests for critical services before enforcing strict global thresholds.

----

## Suggested next steps (I can help with these)
1. I can draft specific unit test descriptions for each missed method (I already included above). If you approve, I can then generate test files for you — say **"Approve test generation"** to proceed. (Per your repo policy files, I will not generate code without explicit approval.)
2. I can run the Jacoco report locally in this environment and return the HTML summary if you want me to run `mvn test jacoco:report` — say **"Run coverage"**.
3. I can add CI steps to the repo (e.g., GitHub Actions workflow) to run tests and publish Jacoco coverage — say **"Add CI coverage"**.

----

Generated on: December 9, 2025
