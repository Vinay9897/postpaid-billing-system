# Functional Specification Document (FSD) — backend

Last updated: 2025-12-22

Purpose
-------
This Functional Specification Document describes the backend application implemented in `backend/`. It documents scope, actors, major components, API endpoints (mapped from controller code), authorization rules, data flows, validation and error-handling behaviors that can be inferred from the codebase, and assumptions where the implementation is not explicit.

Scope
-----
- Covers only backend functional behavior: User registration, authentication, role-based authorization, customer profile and services, usage tracking, invoice generation and viewing, payment processing, and admin user management.
- Excludes frontend behavior, infrastructure provisioning, or external payment gateway internal behaviors beyond the abstracted PaymentService.

Actors and Roles
----------------
- ADMIN: Full administrative privileges. Many administrative controllers are annotated with `@PreAuthorize("hasRole('ADMIN')")`.
- CUSTOMER: Regular customers who can view their own profile, services, usage and invoices and perform payments.
- GUEST: Unauthenticated user (can register and login).

High-level Architecture
-----------------------
- Spring Boot REST API application (Java) under `backend/src/main/java`.
- Controllers expose REST endpoints; services implement business logic. Security is enforced via Spring Security (annotations and runtime authority checks).
- Main controller packages observed:
  - `com.abc.postpaid.user.controller` — authentication and admin user management
  - `com.abc.postpaid.customer.controller` — customer and admin-customer endpoints
  - `com.abc.postpaid.billing.controller` — usage, invoices, payments
- Data transfer objects (DTOs) are used for request/response payloads (see `dto` packages).

Key Components (discovered controllers)
--------------------------------------
- Authentication
  - `AuthController` (mapped to `/api`)
    - POST `/api/register` — register new user
    - POST `/api/login` — login and receive `AuthResponse`

- Admin / User management
  - `AdminUserController` (mapped to `/api/users`, secured with `hasRole('ADMIN')`)
    - POST `/api/users` — create user (admin)
    - GET `/api/users` — list users (admin)
    - GET `/api/users/{id}` — get user by id (admin)
    - PUT `/api/users/{id}` — update user (admin)
    - POST `/api/users/{id}/password` — set user password (admin)
    - DELETE `/api/users/{id}` — delete user (admin)

- Customer endpoints
  - `CustomerController` (mapped to `/api/customers`)
    - GET `/api/customers/{id}` — get customer profile (owner-only; returns 403 if authenticated user id != owner)
    - GET `/api/customers/{id}/services` — list services for a customer (owner-only)
  - `AdminCustomerController` (mapped to `/api/admin/customers`, `hasRole('ADMIN')`)
    - POST `/api/admin/customers` — create customer (admin)
    - GET `/api/admin/customers` — list customers
    - GET `/api/admin/customers/{id}` — get customer (admin)
    - PUT `/api/admin/customers/{id}` — update customer (admin)
    - DELETE `/api/admin/customers/{id}` — delete customer (admin)
    - POST `/api/admin/customers/{customerId}/services` — create service for customer (admin)
    - GET `/api/admin/customers/{customerId}/services` — list services for customer (admin)

- Billing / Usage / Invoices / Payments
  - `UsageController` (mapped to `/api/services`)
    - GET `/api/services/{serviceId}/usage` — list usage records for a given service
    - POST `/api/services/{serviceId}/usage` — create usage record for a service (admin runtime-checked)
  - `InvoiceController` (mapped under `/api/customers`)
    - GET `/api/customers/{id}/invoices` — list invoices for a customer (owner or admin)
    - POST `/api/customers/{id}/invoices` — create invoice for customer (admin-only)
  - `PaymentController` (mapped to `/api/invoices`)
    - POST `/api/invoices/{id}/payments` — record payment for invoice (authenticated users)
    - GET `/api/invoices/{id}/payments` — list payments for invoice (authenticated users)

API Behavior and Authorization Rules
-----------------------------------
- Authentication:
  - Registration: `POST /api/register` returns 201 and a new `user_id` in body.
  - Login: `POST /api/login` returns an `AuthResponse` (token/session info). Tests and integrations should use `Authorization: Bearer <token>` unless the project defines another header; this is an assumption (see Assumptions).

- Authorization patterns seen in code:
  - Controller-level `@PreAuthorize("hasRole('ADMIN')")` enforces ADMIN-only controllers (`AdminUserController`, `AdminCustomerController`).
  - In controllers without `@PreAuthorize`, runtime authority checks exist (e.g., `isAdmin(auth)` helper used in `InvoiceController`, `PaymentController`, `UsageController`).
  - Owner checks: `CustomerController` compares the authenticated principal id to the resource owner id and returns 401 if unauthenticated or 403 if authenticated but not owner.

- Error handling patterns (inferred):
  - Missing resources typically lead to 404 (e.g., `CustomerController` returns 404 if customer missing).
  - Unauthorized access returns 401 (when auth missing) or 403 (when authenticated but unauthorized).
  - Controller methods catching `IllegalArgumentException` return 404 in some controllers (e.g., invoice and payment creation) — this maps some validation errors to 404.
  - Successful creation uses 201 with a JSON body containing created entity id in several controllers.

Data Models and DTO mapping (inferred)
------------------------------------
The codebase uses DTO classes for requests and responses. Representative DTOs (from imports) include:
- `LoginRequest`, `RegisterRequest`, `AuthResponse` — used by `AuthController`.
- `AdminCreateUserRequest`, `AdminUpdateUserRequest`, `SetPasswordRequest`, `UserResponse` — used by `AdminUserController`.
- `CustomerRequest`, `CustomerResponse`, `ServiceRequest`, `ServiceResponse`, `OwnerCustomerUpdateRequest` — used by customer controllers.
- `UsageRecordRequest`, `UsageRecordResponse` — used by `UsageController`.
- `InvoiceRequest`, `InvoiceResponse` — used by `InvoiceController`.
- `PaymentRequest`, `PaymentResponse` — used by `PaymentController`.

Note: This document intentionally does not invent field lists. If you want field-level FSD sections, I can extract DTO field names from their class files and add them into this FSD (recommended for test automation and client SDKs).

Important Implementation Details and Observations
-----------------------------------------------
- Owner update endpoint: `CustomerController` does not expose an owner-edit endpoint; only `PUT /api/admin/customers/{id}` (admin) exists. If owner-self profile updates are required, implementers should add an endpoint or adjust tests accordingly.
- Invoice retrieval: `InvoiceController` exposes listing of invoices by customer (`GET /api/customers/{id}/invoices`) but not a dedicated `GET /api/invoices/{invoiceId}`; clients should filter the list or request a new endpoint if single-invoice fetch is required.
- Usage endpoints are service-centric (`/api/services/{serviceId}/usage`). If customer-level aggregation of usage is needed, the service layer likely supports it but the controller-level API for it is not present.
- PaymentController requires authentication but does not explicitly enforce admin-only restrictions; it relies on service-layer checks for ownership/authorization.

Validation and Input Constraints (inferred)
-----------------------------------------
- Controllers expect valid DTOs and use `@Valid` in admin customer endpoints (e.g., `@Valid @RequestBody CustomerRequest` in `AdminCustomerController`), indicating Bean Validation annotations are used on DTOs.
- Missing required fields should return 4xx validation errors; tests should assert field-level validation messages where possible.

Non-functional / Operational Notes
---------------------------------
- Error mapping: controllers return 401, 403, 404 and 201/200 accordingly. Integration tests should assert these status codes explicitly.
- Logging and debug: `CustomerController` contains a debug System.out print; additional application logging likely exists in services (not enumerated here).

Assumptions and Open Questions
------------------------------
These items are not explicitly defined in code and are recorded as assumptions — please confirm to produce a fully detailed FSD:
1. Authorization header format: assumed `Authorization: Bearer <token>` for authenticated calls.
2. Role names: code references `ROLE_ADMIN`. For tests and documentation we assume `ADMIN` and `CUSTOMER` are the principal role labels.
3. Payment gateway: PaymentService abstracts gateway details. Specific provider behavior (timeouts, webhook flows, refunds) is not in-scope unless you provide integration details.
4. DTO field-level definitions: field names / types are not enumerated here; I can extract them directly from DTO classes on request.
5. Single-invoice endpoint: absent in controllers; confirm whether desired.

Acceptance Criteria
-------------------
- All endpoints listed above are reachable and return the expected HTTP status codes documented in controllers.
- Admin-only endpoints must reject non-admins with 403.
- Owner checks (customer access) must return 401 when unauthenticated and 403 when authenticated but not owner.

Next steps / options
--------------------
1. I can augment this FSD with DTO field definitions by extracting fields from `backend/src/main/java/**/dto`.
2. I can produce example request/response payloads for each endpoint (useful for API clients and tests). This requires confirmation on auth header format.
3. I can generate a Postman collection or CSV mapping test cases from the FSD.

Please confirm which of the three follow-ups you want next, and whether to assume `Authorization: Bearer <token>` for header examples.



