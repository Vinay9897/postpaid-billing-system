# JUnit Test Case Report — backend

This document lists suggested JUnit test cases (class + method names) that map to the backend codebase controllers and services. Each test case is written in a format suitable for implementation with JUnit 5 (and Spring Boot Test for integration tests). The tests include positive, negative, authorization, validation and error-handling scenarios.

Guidance
- Test type column indicates Unit vs Integration. Use @SpringBootTest and TestRestTemplate or MockMvc for integration tests that exercise controllers and security.
- Use test class names that match repository conventions: e.g., `AuthControllerTest`, `AdminUserControllerTest`, `CustomerControllerTest`, `InvoiceControllerIntegrationTest`.
- For endpoints requiring authentication, use a valid JWT created by a test helper or a test JwtProvider configured with test keys. Tests should include negative cases for missing/invalid tokens.
- For DB-backed tests prefer an in-memory database (H2) and @DataJpaTest or @SpringBootTest with a test profile.

Table of test cases

| TestCase ID | Module | Feature | Suggested Test Class | Test Method | Type | Preconditions | Steps | Test Data | Expected Assertions | Notes |
|---|---|---:|---|---|---:|---|---|---|---|---|

| AUTH-01 | Auth | Successful registration and login | `AuthControllerTest` | registerAndLogin_success() | Integration | Fresh DB, no user with test email | 1) POST /api/register with valid payload 2) Assert 201 and user_id 3) POST /api/login with same credentials 4) Assert 200 and token present | Register: { username, email, password, fullName } | Registration returns 201 and user_id; login returns token and expiresIn > 0; token validates against JwtProvider | Use MockMvc or TestRestTemplate. Verify saved user via UserRepository. |

| AUTH-02 | Auth | Registration validation failure | `AuthControllerTest` | register_missingPassword_returnsBadRequest() | Integration | None | POST /api/register with missing password | { username, email } | 400 Bad Request; response contains validation error for password | If controller uses Bean Validation, assert error field. If not, expects 400 from GlobalExceptionHandler mapping. |

| AUTH-03 | Auth | Duplicate registration | `AuthControllerTest` | register_duplicateEmail_returnsConflict() | Unit/Integration | Existing user with same email | POST /api/register with duplicate email | { username, email(existing), password } | 4xx error — prefer 409 Conflict or 400 with message 'email_exists' | Service currently throws IllegalArgumentException("email_exists") — assert mapping in GlobalExceptionHandler or controller behaviour. |

| AUTH-04 | Auth | Login failure invalid credentials | `AuthControllerTest` | login_invalidCredentials_returnsUnauthorized() | Integration | User exists with known password | POST /api/login with wrong password | { username, password: wrong } | 400 Bad Request or 401 Unauthorized mapped from IllegalArgumentException; no token returned | GlobalExceptionHandler maps IllegalArgumentException -> 400 currently. Document and assert whichever mapping is correct. |

| ADMIN-UM-01 | Admin User Management | Admin creates user (happy path) | `AdminUserControllerTest` | adminCreateUser_success() | Integration | Admin JWT available | POST /api/users with AdminCreateUserRequest | { username, email, password, role } | 201 Created with user_id; repo contains created user with role | Use @WithMockUser or provide admin token. Controller uses @PreAuthorize('hasRole("ADMIN")') so test must have ADMIN authority. |

| ADMIN-UM-02 | Admin User Management | Non-admin cannot create user | `AdminUserControllerTest` | nonAdminCreateUser_forbidden() | Integration | Customer token or no token | POST /api/users | same payload | 403 Forbidden if authenticated as non-admin; 401 if unauthenticated | Use MockMvc with authentication principal set as non-admin. |

| ADMIN-UM-03 | Admin User Management | List users pagination required | `AdminUserControllerTest` | listUsers_paged() | Unit/Integration | Many users in DB | GET /api/users?page=0&size=20 | query params | 200 OK and paged response (if implemented) or full list | NOTE: code currently returns findAll(); if you implement pagination add tests; otherwise assert expected behavior and include performance note. |

| CUST-01 | Customer | View own profile | `CustomerControllerTest` | getCustomer_owner_canView() | Integration | Customer A exists and token for A | GET /api/customers/{customerAId} with token for A | N/A | 200 OK and body contains CustomerResponse with correct userId and customerId | Use token generated for user id string principal as code expects. |

| CUST-02 | Customer | Unauthorized view of another profile | `CustomerControllerTest` | getCustomer_otherCustomer_forbidden() | Integration | Two customers exist | GET /api/customers/{customerBId} with token for CustomerA | N/A | 403 Forbidden (or 404 depending on design) | Code returns 403 when authenticated user id != owner. Assert 403. |

| CUST-03 | Customer | Unauthenticated access to profile | `CustomerControllerTest` | getCustomer_noAuth_unauthorized() | Integration | Customer exists | GET /api/customers/{id} without Authorization header | N/A | 401 Unauthorized | Code returns 401 when authUserId null. |

| CUST-04 | Customer | List own services | `CustomerControllerTest` | listServices_owner_canList() | Integration | Customer has services configured | GET /api/customers/{id}/services with owner token | N/A | 200 OK and list of ServiceResponse matching DB | Also assert fields (serviceId, name, rate) returned. |

| CUST-05 | Customer | List services unauthorized | `CustomerControllerTest` | listServices_other_forbidden() | Integration | Two customers; services for B exist | GET /api/customers/{B}/services with token A | N/A | 403 Forbidden | Ensure 403. |

| SERVICE-01 | Service Management | Admin creates service for customer | `AdminCustomerControllerTest` | createServiceForCustomer_success() | Integration | Admin token; customer exists | POST /api/admin/customers/{customerId}/services | ServiceRequest { name, rate } | 201 Created; response contains service_id; serviceRepository has new service linked to customer | Assert persisted values match request. |

| SERVICE-02 | Service Management | Customer cannot create service | `AdminCustomerControllerTest` | createService_customerForbidden() | Integration | Customer token | POST /api/admin/customers/{customerId}/services | payload | 403 Forbidden | Controller has @PreAuthorize on AdminCustomerController. |

| USAGE-01 | Usage Tracking | Admin creates usage record | `UsageControllerTest` | createUsage_admin_success() | Integration | Admin token; service exists | POST /api/services/{serviceId}/usage | UsageRecordRequest { units, timestamp, metadata } | 201 Created and usage saved linking serviceId | Asserts on repository count and field values. |

| USAGE-02 | Usage Tracking | Customer cannot create usage | `UsageControllerTest` | createUsage_customer_forbidden() | Integration | Customer token | POST /api/services/{serviceId}/usage | payload | 403 Forbidden | Controller checks isAdmin(auth). |

| USAGE-03 | Usage Tracking | List usage for service | `UsageControllerTest` | getUsageRecords_success() | Integration | Usage records exist for service | GET /api/services/{serviceId}/usage | N/A | 200 OK and list contains UsageRecordResponse items matching DB | Verify mapping with fields (units, timestamp, serviceId). |

| INVGEN-01 | Invoice | Admin creates invoice | `InvoiceControllerTest` | createInvoice_admin_success() | Integration | Admin token; customer exists and usage/prices available or InvoiceRequest provided | POST /api/customers/{id}/invoices | InvoiceRequest { billingPeriodStart, billingPeriodEnd, totalAmount } | 201 Created with invoiceId; invoice repository contains new invoice | Assert fields and status default (e.g., 'unpaid'). |

| INVGEN-02 | Invoice | Create invoice unauthorized (non-admin) | `InvoiceControllerTest` | createInvoice_customer_forbidden() | Integration | Customer token | POST /api/customers/{id}/invoices | payload | 403 Forbidden | InvoiceController checks isAdmin(auth) before creation. |

| INVVIEW-01 | Invoice Viewing | Customer lists own invoices | `InvoiceControllerTest` | listInvoices_owner_success() | Integration | Customer token; invoices exist | GET /api/customers/{id}/invoices | N/A | 200 OK and response contains invoices for that customer only | Test both admin and owner cases (admin can list any customer's invoices). |

| PAY-01 | Payment Processing | Record payment success | `PaymentControllerTest` | recordPayment_authenticated_success() | Integration | Authenticated user; invoice exists | POST /api/invoices/{invoiceId}/payments | PaymentRequest { paymentDate, amount, paymentMethod } | 201 Created with paymentId; payment saved and invoice status updated (if implemented) | Also assert listPaymentsByInvoice returns the payment. |

| PAY-02 | Payment Processing | Record payment unauthenticated | `PaymentControllerTest` | recordPayment_noAuth_unauthorized() | Integration | No auth header | POST /api/invoices/{invoiceId}/payments | payload | 401 Unauthorized | PaymentController returns 401 when auth == null. |

| ADMIN-ROLE-01 | Admin User Management | Admin updates user role and new role gains admin access | `AdminUserControllerTest` | updateUserRole_promoteToAdmin_thenAccessAdminEndpoint() | Integration | Admin user A, target user B exists | 1) Admin promotes B via PUT /api/users/{id} 2) Authenticate as B and call admin endpoint POST /api/admin/customers | Appropriate payloads | After promotion, B's token (or new auth) should allow admin endpoints; prior to promotion B cannot access admin endpoints | Because role persistence is in User entity, test should assert DB updated and security checks reflect change. |

| SERVICE-PERF-01 | Performance | Large list endpoints should be paginated | `AdminUserControllerTest` | listUsers_largeDataset_paged() | Integration | Many users inserted | GET /api/users?page=0&size=50 | N/A | Response is paged and size <= requested page size | If pagination not implemented, test documents current behavior and will fail; use as regression test. |

| TRAN-01 | Transactions | Service create operations are transactional | `CustomerServiceTest` | createCustomer_transactional_rollbackOnFailure() | Unit | Mock repository to throw on save | Call createCustomer and simulate partial failure on second save | Expect transaction rollback (no persisted partial data) | Use @Transactional test + rollback or Mockito to simulate behavior; ensure method annotated with @Transactional. |

| VALID-01 | Validation | Admin create customer invalid payload | `AdminCustomerControllerTest` | createCustomer_invalidEmail_badRequest() | Integration | Admin token | POST /api/admin/customers with invalid email | CustomerRequest { email: 'bad', ... } | 400 Bad Request with field errors map (see GlobalExceptionHandler.handleMethodArgumentNotValid) | Assert response contains errors.email entry. |

| SEC-JWT-01 | Security | Invalid JWT should not authenticate request | `JwtProviderUnitTest` | invalidToken_rejected() | Unit | Invalid/tampered token | Call JwtProvider.validateTokenAndGetClaims with tampered token | IllegalArgumentException("invalid_signature") or equivalent thrown | Assert exception type/message. |

| SEC-JWT-02 | Security | Token expiry enforced | `JwtProviderUnitTest` | expiredToken_rejected() | Unit | Token with exp in the past | Validate token | IllegalArgumentException("token_expired") thrown | Unit test constructs token with past exp or mock system clock. |

| OBS-01 | Observability | Global exception handler logs and returns structured error | `GlobalExceptionHandlerTest` | handleUnhandledException_logsAndReturnsInternalError() | Unit | Simulate controller throwing RuntimeException | Invoke controller via MockMvc or call handler | 500 response body contains { error: "internal_error" } and exception logged | If logging behavior not implemented, test records current behavior; recommend implementing logging. |


Test class / method naming conventions (suggested)
- Controller integration tests: `*ControllerTest` or `*ControllerIntegrationTest` for tests that start the Spring context.
- Service unit tests: `*ServiceTest` (mock repositories and assert business logic).
- Security/unit tests for JwtProvider: `JwtProviderTest`.
- Use JUnit 5 (jupiter), Mockito for mocking, AssertJ or Hamcrest for assertions.

Test data and helpers suggestions
- Add test utility to create JWTs for tests (with test keypair) or reuse `JwtProvider` in a test profile with test keys.
- Centralize test fixtures: methods that create User, Customer, Service, Invoice, Payment entities and persist them in test DB.
- Provide a TestAuthenticationHelper that returns Authorization header values for given user ids/roles to avoid repeating token generation in tests.

Mapping test cases to code locations
- Auth tests -> `backend/src/main/java/com/abc/postpaid/user/controller/AuthController.java` and `backend/src/main/java/com/abc/postpaid/user/service/impl/AuthServiceImpl.java`.
- Admin user tests -> `backend/src/main/java/com/abc/postpaid/user/controller/AdminUserController.java` and `.../AdminUserServiceImpl.java`.
- Customer tests -> `backend/src/main/java/com/abc/postpaid/customer/controller/CustomerController.java` and service impl.
- Billing tests -> `backend/src/main/java/com/abc/postpaid/billing/controller/*` and `.../service/impl/*`.

Prioritization (recommended order to implement tests)
1. Auth flows (registration/login) — security critical.
2. Owner/authorization checks for CustomerController and Admin controllers.
3. Invoice creation & viewing flows.
4. Payment processing flows.
5. Usage tracking and service management.
6. Negative/validation tests and performance/pagination tests.

How to run (suggested commands)
Use the project's Maven wrapper (if present) or Maven to run tests. Example (PowerShell):

```powershell
cd backend
mvn -DskipTests=false test
```

Notes
- This report describes test cases and structure only — it intentionally avoids generating test code. If you want, I can convert selected cases into skeleton JUnit test files (no implementation) or fully implemented tests — confirm which and whether to create unit or integration tests first.

---

If you'd like, I can now:
- Generate skeleton JUnit test classes for the top-priority test cases (1–4), or
- Produce a Postman/Newman collection for controller-level integration tests mapped to the same cases, or
- Extract DTO fields to populate concrete request bodies for each test.

Please tell me which follow-up you'd like. 
