# Code Review — Postpaid Billing System

**Executive Summary**
- The recent changes implement progressive registration (creating a `Customer` on registration), owner-only customer profile endpoints (including `GET /api/customers/me`), and frontend pages for customer profile. Tests and controller MockMvc tests were added. Overall architecture and layering are sound; attention is needed around authorization semantics, JWT principal handling, CORS, test coverage gaps in `CustomerServiceImpl`, and secrets management.

**Pros**
- **Layering:** Controllers are thin and delegate to services; service implementations handle entity ↔ DTO mapping — good separation of concerns.
- **Atomic Registration:** `AuthServiceImpl.register()` creates `User` and `Customer` transactionally — avoids orphan state.
- **Progressive Profiling:** Registration accepts `full_name` and `phone_number` and persists them to `Customer` — improves UX.
- **Owner-focused Security:** Customer profile endpoints now enforce owner-only access and added `GET /api/customers/me` which reduces accidental id confusion from the frontend.
- **Tests Added:** MockMvc tests for `CustomerController` improve controller-level validation of authorization and responses.

**Cons / Risks**
- **JWT Principal Usage:** `JwtAuthenticationFilter` sets the `Authentication` principal to the token `sub` string (user id) and authorities to `ROLE_<ROLE>`. Controllers parse `principal` as `Long`. This works but is brittle: any change in principal format (e.g., use of `UserDetails`) will break owner checks. Stronger approach: set a typed principal or store `userId` in a `UserPrincipal` object.
- **Role Handling Assumptions:** Some earlier code attempted to treat admins specially; the current change removes admin bypass for customer profile CRUD. Ensure other admin operations are documented and restricted appropriately. Role normalization utilities would avoid brittle comparisons.
- **CORS & Dev Origins:** CORS configuration was added (allowing `localhost`). Remember to tighten this for staging/production.
- **Secrets in Repo:** I saw keystore files under `target/classes/keystore/private.pem` — ensure private keys and secrets are not committed to source control and are loaded from a secure store in production.
- **Delete Semantics Unclear:** `DELETE /api/customers/{id}` currently deletes `Customer` but leaves `User`. Document desired behavior (soft-delete, cascade delete, or keep user).
- **Coverage Gaps (service layer):** You reported coverage gaps for `CustomerServiceImpl` methods (`getCustomer`, `getCustomerByUserId`, `updateCustomer`, `deleteCustomer`, and their mapping lambdas). These are untested and represent risk for regressions.
- **Testing Assumptions:** Some unit tests manipulate `SecurityContextHolder` with string principals. Integration tests should validate the full auth filter pipeline (including `JwtAuthenticationFilter`) to ensure the real auth behavior matches tests.

**Actionable Recommendations (prioritized)**
1. **Add targeted unit tests for `CustomerServiceImpl`** (high priority):
   - Cover `getCustomer(Long)` success and not-found paths.
   - Cover `getCustomerByUserId(Long)` success and not-found paths.
   - Cover `updateCustomer(Long, CustomerRequest)` with partial updates and validate persisted changes via mocked `CustomerRepository`.
   - Cover `deleteCustomer(Long)` path and error handling.
   - Tests should also exercise the mapping lambdas in `listCustomers()` and `listServicesForCustomer()`.

2. **Harden JWT principal handling** (medium priority):
   - Introduce a small `UserPrincipal` class that stores `userId` and `roles`. In `JwtAuthenticationFilter`, set `new UsernamePasswordAuthenticationToken(userPrincipal, null, grantedAuthorities)` instead of raw `sub` string. Update controllers to safely extract `userId` from the principal type.
   - Alternatively, create a helper method `AuthUtils.getAuthUserId(Authentication)` to centralize conversion logic and null handling.

3. **Document and decide deletion semantics** (medium priority):
   - Decide whether deleting `Customer` should also delete or disable `User`. Implement soft-delete if historical data must be kept.

4. **Remove secrets from repository and use secret management** (high priority for production):
   - Remove any private keys/keystores from VCS and load them from environment or a secret manager. Add a local dev key only if necessary and ignore it via `.gitignore`.

5. **CORS & Security hardening** (medium priority):
   - Limit CORS origins in non-dev profiles. Consider CSRF protection if cookies are used. Ensure TLS termination occurs before the app in production.

6. **Coverage & CI** (high priority):
   - Add the unit tests (see #1). Add a code coverage check (e.g., in CI) that flags method-level regression for key services.

7. **API / Frontend alignment** (low-medium):
   - Prefer returning `customerId` or the full `CustomerResponse` as part of the login/registration response so frontend can navigate directly to profile without guessing ids.

**Concrete Test Suggestions**
- Create `CustomerServiceImplTest` (JUnit + Mockito) that:
  - Mocks `CustomerRepository`, `UserRepository`, and `ServiceRepository`.
  - Asserts mapping from `Customer` → `CustomerResponse` for `getCustomer()` and `getCustomerByUserId()`.
  - Verifies partial updates in `updateCustomer()` only change non-null fields.
  - Simulates `findByUserUserId()` returning empty to assert `IllegalArgumentException` branches.

**Small Code Examples**
- Centralized principal extraction (helper):
  - `public static Long extractUserId(Authentication auth) { if (auth==null) return null; Object p = auth.getPrincipal(); if (p instanceof UserPrincipal) return ((UserPrincipal)p).getUserId(); try { return Long.valueOf(String.valueOf(p)); } catch (Exception e) { return null; } }`

**Follow-up / Next Steps**
- I can implement the `CustomerServiceImpl` unit tests now and run them to improve coverage. Say “Add service tests” and I will propose tests and then apply them.
- I can add the `UserPrincipal` wrapper and migrate controllers to use it (requires coordinated changes to `JwtAuthenticationFilter` and tests). Say “Harden auth principal” to proceed.

---
Generated on: December 9, 2025
