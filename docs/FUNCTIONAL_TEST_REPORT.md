# Functional Test Case Report — backend

This document contains functional test cases for the backend application at `backend/`.
Only behaviors derived from the following features were used: User Registration; Login & Authentication; Role-Based Authorization; Customer Profile; Service Management; Usage Tracking; Invoice Generation; Invoice Viewing; Payment Processing; Admin User Management.

## Document notes
- Each test case follows the required structure: Test Case ID, Module Name, Feature Name, User Role, Preconditions, Test Steps, Test Data, Expected Result, Actual Result (blank), Status (blank).
- Positive and negative cases are included. Authorization, validation, and error-handling scenarios are explicitly covered.

## Assumptions
The repository does not explicitly document certain runtime details. The following items are assumptions (do not treat them as facts):

1. Roles present in the system are at least `ADMIN` and `CUSTOMER`. If additional roles exist, they are out of scope until confirmed.
2. Authentication is token-based (e.g., JWT or session token). Specific header names and expiry rules are not defined here.
3. Payment methods and external payment gateway behaviors (e.g., refunds, gateway timeouts) are not described; tests use a generic "valid payment token" or "invalid payment token" as test data.
4. Invoice generation mechanism (manual/admin-trigger vs scheduled) is not documented; tests do not assume how invoices are created—only the presence or absence of invoices.
5. Usage records include at least: customer identifier, service identifier, units/amount, and period. Exact schema is not assumed.
6. Endpoints/URLs are not assumed; these cases reference feature-level behaviors only. Implementing tests should map these behaviors to real routes in the backend.

7. Owner (customer) profile update endpoint is not present under `CustomerController` in the codebase; only admin update exists at `PUT /api/admin/customers/{id}`. Where an owner update was listed in test cases, the implementation tests should instead use the admin endpoint or add an owner endpoint if intended.

If any of the above assumptions are incorrect or you can provide specifics (role names, auth header, payment provider, invoice trigger), I will update the report.

---

## Test Cases

### User Registration

- Test Case ID: REG-01
- Module Name: User Registration
- Feature Name: New user sign-up
- User Role: Guest
- Preconditions: No existing account for the `email` used in test data.
 - Endpoint: POST /api/register
- Test Steps:
  1. Submit registration payload with all required fields (email, password, fullName).
  2. Confirm response indicates success (created) and returns user id.
  3. Attempt to login with the registered credentials.
- Test Data: { email: "new.user@example.com", password: "ValidPass123!", fullName: "New User" }
- Expected Result: Account created; login succeeds and returns auth token.
- Actual Result:
- Status:

- Test Case ID: REG-02
- Module Name: User Registration
- Feature Name: New user sign-up validation
- User Role: Guest
- Preconditions: None
 - Endpoint: POST /api/register
- Test Steps:
  1. Submit registration with missing required field (no password).
  2. Observe response.
- Test Data: { email: "bad.user@example.com", fullName: "Bad User" }
- Expected Result: 4xx validation error specifying missing password field; no account created.
- Actual Result:
- Status:

- Test Case ID: REG-03
- Module Name: User Registration
- Feature Name: Duplicate registration
- User Role: Guest
- Preconditions: Account already exists for test email.
 - Endpoint: POST /api/register
- Test Steps:
  1. Submit registration payload using an email already registered.
  2. Observe response.
- Test Data: { email: "existing@example.com", password: "AnyPass123", fullName: "Existing" }
- Expected Result: 4xx conflict error (duplicate email); helpful error message.
- Actual Result:
- Status:

### Login & Authentication

- Test Case ID: AUTH-01
- Module Name: Login & Authentication
- Feature Name: Successful login
- User Role: Customer
- Preconditions: Valid user account exists with known credentials.
 - Endpoint: POST /api/login
- Test Steps:
  1. Submit login with correct credentials.
  2. Observe response contains an auth token and/or session info.
  3. Use returned token to call an authenticated endpoint (e.g., fetch profile).
- Test Data: { email: "customer@example.com", password: "CustPass1" }
- Expected Result: Login succeeds; token is returned and works for authenticated calls.
- Actual Result:
- Status:

- Test Case ID: AUTH-02
- Module Name: Login & Authentication
- Feature Name: Login failure — invalid credentials
- User Role: Guest
- Preconditions: Account exists but password is incorrect for the attempt.
 - Endpoint: POST /api/login
- Test Steps:
  1. Submit login with correct email but incorrect password.
  2. Observe response.
- Test Data: { email: "customer@example.com", password: "WrongPass" }
- Expected Result: 401 Unauthorized with minimal info; no token issued.
- Actual Result:
- Status:

- Test Case ID: AUTH-03
- Module Name: Login & Authentication
- Feature Name: Access protected resource without token
- User Role: Guest
- Preconditions: Protected endpoint requires authentication.
- Test Steps:
  1. Call a protected endpoint (e.g., customer profile) with no auth header.
  2. Observe response.
- Test Data: none
- Expected Result: 401 Unauthorized and no sensitive data returned.
- Actual Result:
- Status:
 - Endpoint (example protected resource): GET /api/customers/{id}

### Role-Based Authorization

- Test Case ID: RBAC-01
- Module Name: Role-Based Authorization
- Feature Name: Admin-only endpoint access
- User Role: Customer
- Preconditions: Customer is authenticated with role CUSTOMER; an endpoint exists with ADMIN-only access.
- Test Steps:
  1. Authenticate as `CUSTOMER`.
  2. Call ADMIN-only endpoint (e.g., create service or admin user management endpoint).
  3. Observe response.
- Test Data: { token: "customer-auth-token" }
- Expected Result: 403 Forbidden or equivalent; operation not allowed.
- Actual Result:
- Status:
 - Endpoint examples (admin-only):
   - POST /api/users (AdminUserController)
   - POST /api/admin/customers/{customerId}/services (AdminCustomerController)

- Test Case ID: RBAC-02
- Module Name: Role-Based Authorization
- Feature Name: Admin access to admin endpoints
- User Role: Admin
- Preconditions: Admin user exists and is authenticated.
- Test Steps:
  1. Authenticate as `ADMIN`.
  2. Call an admin-only endpoint (e.g., list all users, create service).
  3. Observe response.
- Test Data: { token: "admin-auth-token" }
- Expected Result: Request succeeds with expected data or confirmation.
- Actual Result:
- Status:
 - Endpoint examples (admin-only):
   - GET /api/users (AdminUserController)
   - POST /api/admin/customers/{customerId}/services (AdminCustomerController)

### Customer Profile

- Test Case ID: PROFILE-01
- Module Name: Customer Profile
- Feature Name: View own profile
- User Role: Customer
- Preconditions: Customer authenticated.
- Test Steps:
  1. Call profile GET endpoint with own token.
  2. Observe returned profile contains expected fields (email, fullName, customerId).
- Test Data: { token: "customer-auth-token" }
- Expected Result: 200 OK with correct user profile data for the authenticated user.
- Actual Result:
- Status:
 - Endpoint: GET /api/customers/{id}

- Test Case ID: PROFILE-02
- Module Name: Customer Profile
- Feature Name: Update profile — invalid input
- User Role: Customer
- Preconditions: Customer authenticated.
- Test Steps:
  1. Submit profile update containing invalid email format or invalid phone.
  2. Observe response.
- Test Data: { email: "not-an-email", fullName: "Name" }
- Expected Result: 4xx validation error listing invalid fields; previous data unchanged.
- Actual Result:
- Status:
 - Endpoint (admin-managed update): PUT /api/admin/customers/{id}
 - Note: Owner-level update endpoint is not present in `CustomerController`; using admin update endpoint for update tests (see Assumptions point 7).
- Test Case ID: PROFILE-03
- Module Name: Customer Profile
- Feature Name: Unauthorized profile access
- User Role: Customer A trying to access Customer B
- Preconditions: Two customer accounts exist; Customer A is authenticated.
- Test Steps:
  1. Customer A calls GET profile endpoint for Customer B's id (if such id-based endpoint exists).
  2. Observe response.
- Test Data: { requesterToken: "custA-token", targetCustomerId: "custB-id" }
- Expected Result: 403 Forbidden or 404 Not Found (depending on design) to avoid data leak.
- Actual Result:
- Status:
 - Endpoint: GET /api/customers/{id}

### Service Management

- Test Case ID: SERVICE-01
- Module Name: Service Management
- Feature Name: Admin creates a service
- User Role: Admin
- Preconditions: Admin authenticated.
- Test Steps:
  1. Admin submits payload to create a service (name, description, rate).
  2. Observe response and verify returned service id.
- Test Data: { name: "Premium Data", description: "High-speed", rate: 0.10 }
- Expected Result: Service created; 201 Created and service appears in list endpoints.
- Actual Result:
- Status:
 - Endpoint: POST /api/admin/customers/{customerId}/services

- Test Case ID: SERVICE-02
- Module Name: Service Management
- Feature Name: Customer views services
- User Role: Customer
- Preconditions: One or more services exist.
- Test Steps:
  1. Customer calls services list endpoint.
  2. Observe response contains available services with expected fields.
- Test Data: { token: "customer-auth-token" }
- Expected Result: 200 OK and list of services (id, name, rate) visible to customer.
- Actual Result:
- Status:
 - Endpoint: GET /api/customers/{id}/services

- Test Case ID: SERVICE-03
- Module Name: Service Management
- Feature Name: Unauthorized service creation
- User Role: Customer
- Preconditions: Customer authenticated.
- Test Steps:
  1. Customer attempts to create a service using the admin API.
  2. Observe response.
- Test Data: { name: "Malicious Service", rate: 999 }
- Expected Result: 403 Forbidden; service not created.
- Actual Result:
- Status:
 - Endpoint: POST /api/admin/customers/{customerId}/services

### Usage Tracking

- Test Case ID: USAGE-01
- Module Name: Usage Tracking
- Feature Name: Record usage and view own usage
- User Role: Customer
- Preconditions: Usage records exist for the customer for a billing period.
- Test Steps:
  1. Customer queries usage for a specific period.
  2. Observe usage entries correspond to service and quantity.
- Test Data: { token: "customer-auth-token", period: "2025-11" }
- Expected Result: 200 OK and usage list containing items with serviceId, units, timestamp.
- Actual Result:
- Status:
 - Endpoint (view by service): GET /api/services/{serviceId}/usage

- Test Case ID: USAGE-02
- Module Name: Usage Tracking
- Feature Name: Admin views customer usage
- User Role: Admin
- Preconditions: Admin authenticated; usage exists for target customer.
- Test Steps:
  1. Admin queries usage for a specific customer for a period.
  2. Observe response contains usage items and totals.
- Test Data: { token: "admin-auth-token", customerId: "cust-123", period: "2025-11" }
- Expected Result: 200 OK and usage items for specified customer.
- Actual Result:
- Status:
 - Endpoint (list by service): GET /api/services/{serviceId}/usage
 - Note: the codebase exposes usage per service id; administrative aggregation by customer may be provided by service layer but not as a dedicated endpoint in controllers.

- Test Case ID: USAGE-03
- Module Name: Usage Tracking
- Feature Name: Access control for usage data
- User Role: Customer A tries to access Customer B usage
- Preconditions: Two customers exist; Customer A authenticated.
- Test Steps:
  1. Customer A calls usage endpoint with Customer B id (if endpoint allows id param).
  2. Observe response.
- Test Data: { token: "custA-token", targetCustomerId: "custB-id" }
- Expected Result: 403 Forbidden or 404 Not Found to protect privacy.
- Actual Result:
- Status:
 - Endpoint: GET /api/services/{serviceId}/usage
 - Note: Service usage is exposed by service id. Ensure mapping from customer to service ids before attempting access checks.

### Invoice Generation

- Test Case ID: INVGEN-01
- Module Name: Invoice Generation
- Feature Name: Generate invoice for period with usage
- User Role: System/Admin
- Preconditions: Usage records exist for the customer for the billing period.
- Test Steps:
  1. Trigger invoice generation (manual admin trigger if available, or verify generated invoice exists).
  2. Retrieve generated invoice and compare line items to usage records.
- Test Data: { customerId: "cust-123", period: "2025-11" }
- Expected Result: Invoice created with correct totals derived from usage and service rates; contains invoice id, date, line items, total amount.
- Actual Result:
- Status:
 - Endpoint (admin-trigger): POST /api/customers/{id}/invoices
 - Note: InvoiceController exposes creation at POST /api/customers/{id}/invoices and listing at GET /api/customers/{id}/invoices.

- Test Case ID: INVGEN-02
- Module Name: Invoice Generation
- Feature Name: No invoice when no usage
- User Role: System/Admin
- Preconditions: No usage records present for the period.
- Test Steps:
  1. Attempt to generate invoice for a period with zero usage.
  2. Observe response or absence of invoice.
- Test Data: { customerId: "cust-999", period: "2025-10" }
- Expected Result: No invoice created; system returns a clear message or an empty invoice amount (design-specific — record expectation clearly in implementation tests).
- Actual Result:
- Status:
 - Endpoint: POST /api/customers/{id}/invoices

### Invoice Viewing

- Test Case ID: INVVIEW-01
- Module Name: Invoice Viewing
- Feature Name: Customer views own invoice
- User Role: Customer
- Preconditions: One or more invoices exist for the customer.
- Test Steps:
  1. Customer requests invoice list or a specific invoice id.
  2. Observe returned invoices contain expected fields (id, period, amount, status).
- Test Data: { token: "customer-auth-token" }
- Expected Result: 200 OK and invoices for the authenticated customer only.
- Actual Result:
- Status:
 - Endpoint: GET /api/customers/{id}/invoices
 - Note: A single-invoice-by-id endpoint is not present in InvoiceController; use listing and filter by invoice id in response or add endpoint if needed.
- Test Case ID: INVVIEW-02
- Module Name: Invoice Viewing
- Feature Name: Customer cannot view another's invoice
- User Role: Customer A
- Preconditions: Customer B has an invoice; Customer A authenticated.
- Test Steps:
  1. Customer A attempts to fetch Customer B's invoice by id.
  2. Observe response.
- Test Data: { token: "custA-token", invoiceId: "invoice-of-custB" }
- Expected Result: 403 Forbidden or 404 Not Found (no data leakage).
- Actual Result:
- Status:
 - Endpoint: GET /api/customers/{id}/invoices

### Payment Processing

- Test Case ID: PAY-01
- Module Name: Payment Processing
- Feature Name: Successful payment for an invoice
- User Role: Customer
- Preconditions: Customer has an outstanding invoice.
- Test Steps:
  1. Customer submits payment for invoice with valid payment details/token.
  2. Observe response and invoice status update.
  3. Verify payment record is created and invoice status changes to PAID (or partial if partial payments supported).
- Test Data: { invoiceId: "inv-123", paymentToken: "valid-payment-token", amount: 100.00 }
- Expected Result: Payment accepted; response confirms payment; invoice is marked paid and payment record shows amount and timestamp.
- Actual Result:
- Status:
 - Endpoint: POST /api/invoices/{id}/payments

- Test Case ID: PAY-02
- Module Name: Payment Processing
- Feature Name: Payment failure due to invalid payment details
- User Role: Customer
- Preconditions: Outstanding invoice exists.
- Test Steps:
  1. Submit payment with invalid payment token or malformed payment data.
  2. Observe response and invoice status.
- Test Data: { invoiceId: "inv-123", paymentToken: "invalid-token", amount: 100.00 }
- Expected Result: Payment rejected with clear error and invoice remains unpaid.
- Actual Result:
- Status:
 - Endpoint: POST /api/invoices/{id}/payments

- Test Case ID: PAY-03
- Module Name: Payment Processing
- Feature Name: Authorization for recording payments
- User Role: Non-admin attempts to record arbitrary payment for other user
- Preconditions: Non-admin authenticated; admin-only endpoints exist to mark payments manually.
- Test Steps:
  1. Non-admin calls admin payment-record endpoint trying to mark a payment for another customer.
  2. Observe response.
- Test Data: { token: "customer-token", targetInvoice: "inv-234" }
- Expected Result: 403 Forbidden; operation not allowed.
- Actual Result:
- Status:
 - Endpoint: POST /api/invoices/{id}/payments
 - Note: PaymentController exposes POST /api/invoices/{id}/payments and GET /api/invoices/{id}/payments. The controller requires authentication but does not strictly enforce admin-only restrictions in code; implementer tests should verify ownership checks at service layer.
### Admin User Management

- Test Case ID: ADMIN-UM-01
- Module Name: Admin User Management
- Feature Name: Admin creates new user
- User Role: Admin
- Preconditions: Admin authenticated.
- Test Steps:
  1. Admin submits payload to create a new user with role CUSTOMER.
  2. Observe response and verify new user exists.
- Test Data: { email: "created.by.admin@example.com", role: "CUSTOMER" }
- Expected Result: 201 Created; new user record present and usable.
- Actual Result:
- Status:
 - Endpoint: POST /api/users

- Test Case ID: ADMIN-UM-02
- Module Name: Admin User Management
- Feature Name: Non-admin cannot manage users
- User Role: Customer
- Preconditions: Customer authenticated.
- Test Steps:
  1. Customer attempts to call user management endpoints (create/update/delete user).
  2. Observe response.
- Test Data: { token: "customer-auth-token" }
- Expected Result: 403 Forbidden; customer cannot manage users.
- Actual Result:
- Status:
 - Endpoint examples: POST /api/users, PUT /api/users/{id}, DELETE /api/users/{id}

- Test Case ID: ADMIN-UM-03
- Module Name: Admin User Management
- Feature Name: Admin updates user role
- User Role: Admin
- Preconditions: Target user exists.
- Test Steps:
  1. Admin updates the role of an existing user (e.g., promote to ADMIN).
  2. Observe response and ability of updated user to access admin endpoints.
- Test Data: { targetUserEmail: "to.promote@example.com", newRole: "ADMIN" }
- Expected Result: Role change persisted; subsequent auth of the user reflects new privileges.
- Actual Result:
- Status:
 - Endpoint: PUT /api/users/{id}

---

## Test Coverage Summary

- Features covered: registration, login/auth, role-based authorization, customer profile, service management, usage tracking, invoice generation, invoice viewing, payment processing, admin user management.
- For each major feature, at least one positive and one negative/authorization test is included.
- If you need this file split into a test-runner-compatible format (e.g., CSV or individual test files mapped to endpoints), provide endpoint mapping and I will generate it.

---

Please review the Assumptions section and supply any definitive details (role names, auth header name, payment gateway specifics, invoice trigger method). I will iterate the report to align precisely with implementation details.
