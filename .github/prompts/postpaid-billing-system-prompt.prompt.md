MASTER PROMPT — Generate Postpaid Billing System (frontend + backend) in multiple steps

REFERENCE (authoritative): /mnt/data/ABC Telecom_ Postpaid Billing System.pdf. :contentReference[oaicite:1]{index=1}

OVERALL RULES (must be obeyed)
1. Root folder: "Postpaid Billing System" already exists. Create two sub-projects inside it:
   - frontend/  (any JS framework; simple scaffold is fine)
   - backend/   (single Spring Boot app, Java 21)
2. Backend must be a single runnable Spring Boot application (one JAR). Use packages to organize code; do NOT create multiple runnable services or microservices.
3. Backend **MUST** include a `service` package structured with interfaces and implementations:
   - `com.abc.postpaid.<domain>.service` (interfaces)
   - `com.abc.postpaid.<domain>.service.impl` (implementations)
4. Use **Lombok** and **@Builder** for entities/DTOs—do not generate explicit getters/setters manually.
5. **Do not add any additional fields** to any table beyond the exact fields listed in the PDF. Use all fields listed for each entity exactly.
6. Authentication: use JWT access tokens (RS256). **Do not** create any new persistent tables (for example: no refresh_tokens table) unless user explicitly approves later. If the assistant is uncertain about token refresh or persistent tokens, **ask one clarifying question** before producing code.
7. Before generating code for any step, if any requirement or the PDF content for that step is ambiguous, **ask exactly one clarifying question** to the user and wait for the reply.
8. Keep each generated unit small and testable; follow the step sequence below.

DATA MODEL (use these entities with exact fields — no extra fields)
- **Users**
  - user_id (PK)
  - username
  - password_hash
  - email
  - role
  - created_at

- **Customers**
  - customer_id (PK)
  - user_id (FK to Users)
  - full_name
  - address
  - phone_number

- **Services**  (term used in doc; treat as subscription records)
  - service_id (PK)
  - customer_id (FK to Customers)
  - service_type
  - start_date
  - status

- **UsageRecords**
  - usage_id (PK)
  - service_id (FK to Services)
  - usage_date
  - usage_amount
  - unit

- **Invoices**
  - invoice_id (PK)
  - customer_id (FK to Customers)
  - billing_period_start
  - billing_period_end
  - total_amount
  - status

- **Payments**
  - payment_id (PK)
  - invoice_id (FK to Invoices)
  - payment_date
  - amount
  - payment_method

SEQUENCE OF STEPS (each step MUST follow the exact structure: Title / Goal / Endpoints / Data model elements / Acceptance criteria / Tests / ASSUMPTIONS)

1) Step 0 — Project skeleton & DB migration
- Goal:
  Create project skeletons for frontend and backend and add DB migration script for the exact tables above.
- Endpoints:
  (none — scaffolding only)
- Data model elements involved:
  All entities listed above — create Flyway SQL (or equivalent) migrations to create tables with the exact fields and appropriate PK/FK constraints.
- Acceptance criteria:
  - backend builds with Java 21 and required dependencies (Spring Boot, Lombok).
  - Flyway migration V1 creates all tables with exactly the fields in the PDF and correct foreign keys.
  - README with commands to build backend and run migrations.
- Tests:
  - None required for this step beyond build verification.
- ASSUMPTION:
  - ASSUMPTION: use PostgreSQL in dev; docker-compose may include Postgres. No extra columns added.

2) Step 1 — Authentication: register & login (JWT RS256, stateless)
- Goal:
  Implement user registration and login returning JWT access tokens (no persistent refresh tokens by default).
- Endpoints:
  - POST /api/register — request: { username, email, password } ; response: 201 Created (user_id)
  - POST /api/login — request: { username, password } ; response: { access_token, expires_in }
- Data model elements involved:
  - Users: user_id, username, password_hash, email, role, created_at
- Acceptance criteria:
  - Passwords stored as password_hash (BCrypt).
  - Login returns a signed RS256 JWT whose `sub` identifies user_id and includes `role`.
  - Only the exact user fields are persisted; no additional user table fields are created.
- Tests:
  - Unit: password hashing and verification logic.
  - Integration: register flow then login flow returns valid JWT; call a protected endpoint with token to confirm authentication.
- ASSUMPTION:
  - ASSUMPTION: tokens are short-lived (default 15 minutes). If team wants refresh tokens later, the assistant must ask the user for permission before adding persistent refresh storage.

3) Step 2 — User management (admin-only endpoints)
- Goal:
  Implement admin-only user retrieval, update, delete as in the document.
- Endpoints:
  - GET /api/users/{id} — response: user data (admin only)
  - PUT /api/users/{id} — request: fields to update (admin only)
  - DELETE /api/users/{id} — (admin only)
- Data model elements involved:
  - Users: all fields above
- Acceptance criteria:
  - RBAC enforced: only request with JWT `role` = admin can access these endpoints.
  - Updates only modify fields present in Users table — do not create additional fields.
- Tests:
  - Unit: authorization checks.
  - Integration: ensure non-admin token is rejected with 403; admin token allowed.
- ASSUMPTION:
  - ASSUMPTION: role values used are exactly those in the document (e.g., 'customer', 'admin'); map them to constants in code.

4) Step 3 — Customer profile (owner + admin access)
- Goal:
  Implement retrieval of customer profile (owner or admin) and other customer operations if in the PDF.
- Endpoints:
  - GET /api/customers/{id} — returns customer profile (owner or admin)
  - (If document includes editing) PUT /api/customers/{id} — update customer profile (owner or admin)
- Data model elements involved:
  - Customers: customer_id, user_id, full_name, address, phone_number
- Acceptance criteria:
  - Owner access enforced by comparing JWT `sub` (user_id) with customers.user_id; admin bypass allowed.
  - All stored customer fields exactly match the PDF schema.
- Tests:
  - Integration: create user & customer mapping; owner can GET/PUT; other non-admin user denied.
- ASSUMPTION:
  - ASSUMPTION: customer creation is performed manually by admin or during registration per your choice; assistant must ask if automatic creation on register is desired.

5) Step 4 — Services (subscriptions) — list & add
- Goal:
  Implement listing services for a customer and adding a new service (admin only) per the document.
- Endpoints:
  - GET /api/customers/{id}/services — list services for the customer (owner or admin)
  - POST /api/customers/{id}/services — create a service record (admin only)
- Data model elements involved:
  - Services: service_id, customer_id, service_type, start_date, status
- Acceptance criteria:
  - POST sets start_date (use provided value or default to today) and persists only the listed fields.
  - GET returns all fields for each service exactly as in DB.
- Tests:
  - Integration: admin creates service, owner lists services; non-admin denied on create.
- ASSUMPTION:
  - ASSUMPTION: service_type is a text value; pricing/rate cards not applied at create stage (billing step will handle).

6) Step 5 — UsageRecords — add & retrieve
- Goal:
  Enable recording usage records (admin adds) and allow customers to view their usage history (owner or admin).
- Endpoints:
  - POST /api/services/{id}/usage — request: { usage_date, usage_amount, unit } (admin only)
  - GET /api/services/{id}/usage — list usage records (owner or admin)
- Data model elements involved:
  - UsageRecords: usage_id, service_id, usage_date, usage_amount, unit
- Acceptance criteria:
  - POST validates the referenced service_id exists and persists only the listed fields.
  - GET returns usage records filtered by service_id.
- Tests:
  - Unit: validation for service existence & input ranges.
  - Integration: add usage and retrieve; owner with matching customer/service can retrieve.
- ASSUMPTION:
  - ASSUMPTION: ingestion is manual via API for this build; automated ingestion is out of scope.

7) Step 6 — Billing & Invoice generation
- Goal:
  Aggregate usage for a billing period and generate invoices; store total_amount and status; expose invoice listing and download endpoint for PDF placeholder.
- Endpoints:
  - POST /api/customers/{id}/invoices — generate invoice(s) for billing period (admin only or scheduled job); request may include billing_period_start & billing_period_end; response: created invoice_id(s)
  - GET /api/customers/{id}/invoices — list invoices (owner or admin)
  - GET /api/customers/{id}/invoices/{invoiceId} — invoice details (owner or admin)
  - GET /api/customers/{id}/invoices/{invoiceId}/download — return application/pdf (placeholder allowed)
- Data model elements involved:
  - Invoices: invoice_id, customer_id, billing_period_start, billing_period_end, total_amount, status
- Acceptance criteria:
  - Invoice total computed from usage aggregation per documented assumptions (assistant should include a minimal sample rate-card algorithm but must NOT store rate data in DB as extra fields); totals stored in `total_amount`.
  - PDF placeholder created and accessible via download endpoint; invoice record stores only fields in the PDF.
- Tests:
  - Unit: billing calculator logic with sample data (edge cases: zero usage).
  - Integration: create usage entries; run invoice generation; verify invoice.total_amount matches expected calculation; download returns PDF content.
- ASSUMPTION:
  - ASSUMPTION: tax/discounts are NOT stored in DB (since fields not present); include calculation in code but store only final total_amount. Document any assumptions in comments/TODOs.

8) Step 7 — Payments: create & list (idempotency)
- Goal:
  Allow recording payment for invoice and retrieving payments; enforce idempotency to avoid duplicate records. Webhook/provider simulation is allowed but must not require extra persistent fields.
- Endpoints:
  - POST /api/invoices/{id}/payments — request: { amount, payment_method, idempotency_key } ; response: payment_id and status
  - GET /api/invoices/{id}/payments — list payments for invoice (owner or admin)
- Data model elements involved:
  - Payments: payment_id, invoice_id, payment_date, amount, payment_method
- Acceptance criteria:
  - If same idempotency_key is sent for same invoice, do not create duplicate Payment records — return existing payment record. Implement idempotency without adding new columns to existing tables; if temporary storage is required for idempotency, the assistant must **ask one clarifying question** before introducing any extra persistent artifact.
  - On successful payment record creation, set payment_date and persist only the five listed fields.
- Tests:
  - Unit: idempotency logic (same key twice returns same record).
  - Integration: create invoice, POST payment twice with same key, assert only one payment row exists.
- ASSUMPTION:
  - ASSUMPTION: external gateway integration is out-of-scope; simulate provider responses in-memory.

9) Step 8 — Audit & tests integration
- Goal:
  Add minimal audit entries for admin-critical actions and ensure test coverage for key flows.
- Endpoints:
  (audit entries are written internally — no public endpoints unless the document requires them)
- Data model elements involved:
  - (No new persistent fields are allowed per instruction; if the assistant believes audit_log table is essential, ask the user one clarifying question before adding.)
- Acceptance criteria:
  - Actions to audit: invoice generation, payment creation, user updates/deletes by admin.
  - Tests exist that assert audit action was performed or at least that these flows executed without errors.
- Tests:
  - Unit & Integration tests for invoice, payment idempotency, and auth + access control.
- ASSUMPTION:
  - ASSUMPTION: if audit persistence requires a new table, assistant must ask permission before creating it. Otherwise, audit may be logged to app logs only.

GLOBAL TEST & CI REQUIREMENTS (apply across steps)
- Use JUnit + Mockito for unit tests; use H2 for integration tests.
- Include integration tests for: register/login + protected endpoint; owner vs non-owner access; billing total correctness; payment idempotency.
- Provide a GitHub Actions workflow that runs `mvn -DskipTests=false test` and fails on test failures.

FINAL NOTES FOR THE CODE-GENERATION ASSISTANT
- Do NOT add any DB fields or tables not listed without asking the user a clarifying question first.
- Use Java 21 and annotate entities/DTOs with Lombok + @Builder.
- Enforce RBAC in every endpoint as specified.
- Keep each generated PR/patch limited to the files required for the current step.
- For any ambiguity in a step (e.g., whether customer is auto-created on registration, or how idempotency storage should be implemented without extra DB fields), ask exactly one clarifying question to the user and pause until answered.

End of master prompt.
