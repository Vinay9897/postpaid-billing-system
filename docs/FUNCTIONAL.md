# Functional Documentation — Postpaid Billing System

## Summary
The Postpaid Billing System is a monolithic web application that allows users to register, authenticate, and manage customer profiles and service records. The system enforces owner-only access to customer profile CRUD operations and issues JWTs for authenticated sessions.

## Key Features
- Progressive registration: collect `email`, `password`, `full_name`, and `phone_number` at signup.
- Automatic creation of a `Customer` row linked one-to-one with the created `User` during registration.
- JWT-based authentication with role claims.
- Owner-only access to customer profile endpoints (no admin override for profile CRUD).
- Customer service management (create and list services associated with a customer).

## Actors
- Customer: registers, logs in, views/edits/deletes their own profile, manages personal services.
- Admin: system management; by decision, admin cannot override customer profile CRUD endpoints.

## User Flows

- Registration
  1. User fills registration form with `email`, `password`, `full_name`, `phone_number`.
  2. Frontend posts to `POST /api/auth/register`.
  3. Backend creates `User` and `Customer` in a transaction and returns success.

- Login
  1. User posts credentials to `POST /api/auth/login`.
  2. Backend returns a JWT including `sub` (userId) and `role`.
  3. Frontend stores JWT and decodes `sub` for `authStore.user.userId`.

- View Profile
  - Customer calls `GET /api/customers/me` (recommended) or `GET /api/customers/{customerId}` if they have the ID.
  - Backend verifies `principal` equals the linked `userId` on the customer record and returns `CustomerResponse`.

- Edit Profile
  - Customer calls `PUT /api/customers/{customerId}` with `OwnerCustomerUpdateRequest` payload containing only owner-editable fields.
  - Backend verifies owner, applies changes, and returns 200.

- Delete Profile
  - Customer calls `DELETE /api/customers/{customerId}`; backend deletes the `Customer` row (current behavior does not delete the `User`).

## API Summary
- `POST /api/auth/register` — request: `email`, `password`, `full_name`, `phone_number`.
- `POST /api/auth/login` — returns JWT.
- `GET /api/customers/me` — returns the authenticated user's customer record.
- `GET /api/customers/{id}` — returns customer by id (owner-only).
- `PUT /api/customers/{id}` — update owner-editable fields.
- `DELETE /api/customers/{id}` — delete owner customer record.

## DTOs and Fields (important)
- `RegisterRequest` — `email`, `password`, `fullName`, `phoneNumber` (accepts `full_name` and `phone_number` JSON keys).
- `CustomerRequest` — `userId`, `fullName`, `address`, `phoneNumber` (used by service layer).
- `CustomerResponse` — `customerId`, `userId`, `fullName`, `address`, `phoneNumber`.
- `OwnerCustomerUpdateRequest` — `fullName`, `address`, `phoneNumber` (owner-editable subset).

## Error Handling
- `401 Unauthorized` — when unauthenticated requests call owner-only endpoints such as `/api/customers/me`.
- `403 Forbidden` — authenticated user tries to access or modify a customer they do not own.
- `404 Not Found` — requested customer record not found.

## UX & Frontend Notes
- Frontend uses `authStore` to keep JWT and decoded `sub` (userId).
- Use `/customers/me` route in the UI to avoid passing ids; only use `/customers/{id}` when `customerId` is known.
- Registration form should send `full_name` and `phone_number` JSON keys to match backend DTO mapping.

## Testing Recommendations
- Controller behavior: MockMvc tests for owner-only enforcement, `/me` flows, update/delete success & forbidden cases.
- Service layer: unit tests to cover `getCustomer`, `getCustomerByUserId`, `updateCustomer`, `deleteCustomer` and mapping lambdas.
- Integration: test registration creates both `User` and `Customer` atomically.

---
Generated on: December 9, 2025
