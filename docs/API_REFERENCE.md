API Reference — Postpaid Billing System

Overview
- This file summarizes main backend REST endpoints and DTO shapes. Use controller classes under `backend/src/main/java/com/abc/postpaid/*/controller` as source-of-truth for exact paths.

Common prefixes
- API base: `/api`
- Example resource patterns used by controllers:
  - `/api/users` — admin user management
  - `/api/customers` — customers and nested resources
  - `/api/invoices` — invoice management
  - `/api/payments` — payment management (often nested under invoices)
  - `/api/services` — customer services and usage

Representative endpoints
- Users (admin)
  - GET `/api/users` — list users
  - POST `/api/users` — create user (Admin operations)
  - GET `/api/users/{id}` — get user
  - PUT `/api/users/{id}` — update user
  - DELETE `/api/users/{id}` — delete user

- Customers
  - GET `/api/customers/{id}` — get customer by id
  - GET `/api/customers/{id}/services` — list services for a customer
  - POST `/api/customers` — create customer (admin flow)

- Invoices
  - POST `/api/customers/{customerId}/invoices` — create invoice for customer
  - GET `/api/invoices/{invoiceId}` — get invoice
  - GET `/api/customers/{customerId}/invoices` — list invoices for customer
  - GET `/api/invoices?start={date}&end={date}` — list by date range (controller may expose query-based filtering)

- Payments
  - POST `/api/invoices/{invoiceId}/payments` — record payment for invoice
  - GET `/api/payments/{paymentId}` — get payment
  - GET `/api/invoices/{invoiceId}/payments` — list payments for invoice

- Usage
  - POST `/api/services/{serviceId}/usage` — create usage record
  - GET `/api/usage/{usageId}` — get usage record
  - GET `/api/services/{serviceId}/usage` — list usage for service

DTO Summaries (representative)
- `InvoiceRequest`:
  - `billingPeriodStart` (LocalDate)
  - `billingPeriodEnd` (LocalDate)
  - `totalAmount` (BigDecimal)
  - `status` (String)

- `InvoiceResponse`:
  - `invoiceId` (Long)
  - `customerId` (Long)
  - `billingPeriodStart` (LocalDate)
  - `billingPeriodEnd` (LocalDate)
  - `totalAmount` (BigDecimal)
  - `status` (String)

- `PaymentRequest`:
  - `paymentDate` (LocalDate)
  - `amount` (BigDecimal)
  - `paymentMethod` (String)

- `UsageRecordRequest`:
  - `usageDate` (LocalDate)
  - `usageAmount` (BigDecimal)
  - `unit` (String)

Notes
- This page is a high-level reference. For exact field names, validation rules, and request/response JSON shapes, inspect the DTO classes in `backend/src/main/java/com/abc/postpaid/*/dto` and controller method signatures.
- Authentication: Most endpoints require a valid JWT in `Authorization: Bearer <token>` header.
