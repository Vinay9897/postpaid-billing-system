# Functional Requirements — Postpaid Billing System

Version: 1.0
Date: 2025-12-22
Author: Project automation

Purpose

This document lists concise, testable functional requirements for the Postpaid Billing System backend. Each requirement includes acceptance criteria (Given / When / Then), rationale/notes, and traceability back to the functional specification and test cases in the repository.

Scope

Applies to the backend service implemented under `backend/` (Spring Boot). Covers user registration, authentication, role-based access, customer profile management, service management, usage tracking, invoice generation/viewing, and payment processing.

Actors

- ADMIN: user with administrative privileges
- CUSTOMER: end-customer with account and services
- SYSTEM: internal processes (invoice generation)

Glossary

- JWT: JSON Web Token used for authentication (Authorization: Bearer <token>)
- Service: billable service attached to a customer
- Invoice: billing document generated for a customer for a billing period

Requirements (by module)

User Registration (REG-FR)

- REG-FR-01: The system shall allow a new user to register with username, email and password.
  - Rationale: Standard onboarding flow.
  - Acceptance criteria:
    - Given no existing account for the email, When POST /api/register with valid payload, Then respond 201 and return a user identifier.
  - Traceability: `FUNCTIONAL_SPECIFICATION.md` - USER REGISTRATION; Test Cases: REG-01; Automated suites: (none mapped)

- REG-FR-02: The system shall validate required fields when registering.
  - Acceptance criteria:
    - Given a request missing required fields, When POST /api/register, Then respond 400 with validation details.
  - Traceability: Test Cases: REG-02, REG-03

Authentication & Authorization (AUTH-FR)

- AUTH-FR-01: The system shall authenticate users using username/email and password and return a JWT on success.
  - Acceptance criteria:
    - Given valid credentials, When POST /api/login, Then respond 200 and return a token and expiresIn value.
  - Traceability: AUTH-01; Automated suites: `com.abc.postpaid.user.controller.AuthIntegrationTest`, `com.abc.postpaid.user.service.AuthServiceImplTest`

- AUTH-FR-02: The system shall reject invalid credentials with 400/401 and not return a token.
  - Acceptance criteria:
    - Given invalid credentials, When POST /api/login, Then respond 400/401 and no token.
  - Traceability: AUTH-02; Automated suites: same as AUTH-FR-01

Role-based Access Control (RBAC-FR)

- RBAC-FR-01: Admin-only endpoints shall return 403 for non-admins.
  - Acceptance criteria:
    - Given a CUSTOMER token, When calling POST /api/users (admin endpoint), Then respond 403 Forbidden.
  - Traceability: RBAC-01, RBAC-02; Automated suites: `com.abc.postpaid.user.controller.AdminUserControllerTest`, `AdminUserControllerIntegrationTest`, `AdminUserServiceImplTest`

Customer Profile (PROFILE-FR)

- PROFILE-FR-01: Customers shall be able to view their own profile.
  - Acceptance criteria:
    - Given an authenticated customer, When GET /api/customers/{id} with owner token, Then respond 200 with CustomerResponse matching the token principal.
  - Traceability: PROFILE-01; Automated suite: `com.abc.postpaid.customer.controller.CustomerControllerTest`

- PROFILE-FR-02: Admins shall be able to update customer profiles via admin paths; invalid input shall return 400.
  - Acceptance criteria:
    - Given admin credentials, When PUT /api/admin/customers/{id} with invalid email, Then respond 400 with validation errors.
  - Traceability: PROFILE-02; Automated suites: `com.abc.postpaid.customer.controller.AdminCustomerControllerTest`, `AdminCustomerControllerIntegrationTest`

Service Management (SERVICE-FR)

- SERVICE-FR-01: Admin users shall be able to create services for a customer.
  - Acceptance criteria:
    - Given admin authenticated, When POST /api/admin/customers/{customerId}/services with valid payload, Then respond 201 and return service_id.
  - Traceability: SERVICE-01; Automated suites: (none mapped)

- SERVICE-FR-02: Customers shall be able to list services for themselves.
  - Acceptance criteria:
    - Given customer token, When GET /api/customers/{id}/services, Then respond 200 with services list.
  - Traceability: SERVICE-02; Automated suites: (none mapped)

Usage Tracking (USAGE-FR)

- USAGE-FR-01: Admins shall be able to record usage linked to a service.
  - Acceptance criteria:
    - Given admin authenticated, When POST /api/services/{serviceId}/usage with UsageRecordRequest, Then respond 201 and persist the usage record.
  - Traceability: USAGE-01; Automated suite: `com.abc.postpaid.billing.service.UsageRecordServiceImplTest`

- USAGE-FR-02: Customers shall not be able to create usage via admin endpoints (403).
  - Acceptance criteria:
    - Given customer token, When POST /api/services/{serviceId}/usage, Then respond 403 Forbidden.
  - Traceability: USAGE-02; Automated suite: UsageRecordServiceImplTest

Invoice Generation & Viewing (INVOICE-FR)

- INVOICE-FR-01: Admins shall be able to create invoices for a customer for a billing period.
  - Acceptance criteria:
    - Given admin authenticated, When POST /api/customers/{id}/invoices with valid InvoiceRequest, Then respond 201 and return invoiceId.
  - Traceability: INVGEN-01; Automated suites: (none mapped)

- INVOICE-FR-02: Customers shall be able to list their invoices and not view other customers' invoices.
  - Acceptance criteria:
    - Given owner token, When GET /api/customers/{id}/invoices, Then respond 200 with invoice list for that customer.
    - Given non-owner token, When GET /api/customers/{otherId}/invoices, Then respond 403 or 404 per design (system returns 403 currently).
  - Traceability: INVVIEW-01, INVVIEW-02; Automated suites: (none mapped)

Payments (PAY-FR)

- PAY-FR-01: Customers shall be able to record a payment for an invoice.
  - Acceptance criteria:
    - Given invoice exists, When POST /api/invoices/{invoiceId}/payments with valid PaymentRequest, Then respond 201 and return paymentId; invoice status optionally updated.
  - Traceability: PAY-01; Automated suites: (none mapped)

Admin User Management (ADMIN-UM-FR)

- ADMIN-UM-FR-01: Admins shall be able to create and manage users.
  - Acceptance criteria:
    - Given admin token, When POST /api/users, Then respond 201 and create the user.
  - Traceability: ADMIN-UM-01; Automated suites: `com.abc.postpaid.user.controller.AdminUserControllerTest`, `AdminUserControllerIntegrationTest`, `AdminUserServiceImplTest`

- ADMIN-UM-FR-02: Non-admins shall not be able to access admin user management endpoints.
  - Acceptance criteria:
    - Given customer token, When POST /api/users, Then respond 403 Forbidden.
  - Traceability: ADMIN-UM-02

Non-functional requirements (selected)

- NFR-01 (Security): All protected endpoints shall require `Authorization: Bearer <token>` and validate the JWT signature and expiry.
  - Acceptance criteria: Unauthorized requests without a valid token receive 401.

- NFR-02 (Performance): Typical API endpoints shall respond within 500ms under light load (single-request local testing); heavy-load SLA to be defined separately.

Traceability matrix (summary)

| Requirement ID | FSD section | Functional Test Case IDs | Automated test suites |
|---|---:|---|---|
| REG-FR-01 | USER REGISTRATION | REG-01 | (none)
| REG-FR-02 | USER REGISTRATION | REG-02, REG-03 | (none)
| AUTH-FR-01 | LOGIN & AUTHENTICATION | AUTH-01 | com.abc.postpaid.user.controller.AuthIntegrationTest, com.abc.postpaid.user.service.AuthServiceImplTest
| AUTH-FR-02 | LOGIN & AUTHENTICATION | AUTH-02 | same as AUTH-FR-01
| RBAC-FR-01 | ROLE-BASED AUTHORIZATION | RBAC-01, RBAC-02 | com.abc.postpaid.user.controller.AdminUserControllerTest, AdminUserControllerIntegrationTest, AdminUserServiceImplTest
| PROFILE-FR-01 | CUSTOMER PROFILE | PROFILE-01 | com.abc.postpaid.customer.controller.CustomerControllerTest
| PROFILE-FR-02 | CUSTOMER PROFILE | PROFILE-02 | com.abc.postpaid.customer.controller.AdminCustomerControllerTest, AdminCustomerControllerIntegrationTest
| USAGE-FR-01 | USAGE TRACKING | USAGE-01 | com.abc.postpaid.billing.service.UsageRecordServiceImplTest
| USAGE-FR-02 | USAGE TRACKING | USAGE-02 | com.abc.postpaid.billing.service.UsageRecordServiceImplTest
| INVOICE-FR-01 | INVOICE GENERATION | INVGEN-01 | (none)
| INVOICE-FR-02 | INVOICE VIEWING | INVVIEW-01, INVVIEW-02 | (none)
| PAY-FR-01 | PAYMENT PROCESSING | PAY-01 | (none)
| ADMIN-UM-FR-01 | ADMIN USER MANAGEMENT | ADMIN-UM-01 | com.abc.postpaid.user.controller.AdminUserControllerTest, AdminUserControllerIntegrationTest, AdminUserServiceImplTest

Change history

- 2025-12-22 v1.0 — Initial automatic generation.

Notes / next steps

- I inferred mappings between automated suites and functional requirements by test-suite and test-case names. If you want stricter mapping, provide a list of Test Case IDs to update or confirm alternate mappings.
- I can create a CSV or traceability spreadsheet from the above matrix if helpful.
