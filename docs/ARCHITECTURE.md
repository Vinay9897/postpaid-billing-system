# Architecture Overview — Postpaid Billing System

## Purpose
This document describes the architecture and major components of the Postpaid Billing System: a monolithic web application that provides user authentication, customer profile management, and customer service records.

## High-level Components
- Frontend: React (Vite) application located in `frontend/`.
- Backend: Spring Boot application located in `backend/` with layered packages (controller, service, repository, entity, dto).
- Persistence: JPA/Hibernate, target DB PostgreSQL (local dev may use embedded/test DB).
- Security: JWT (RS256), `JwtProvider`, `JwtAuthenticationFilter`, `SecurityConfig`.

## Component Responsibilities
- Frontend (`frontend/`): UI, route-level protection, API clients (`authService.js`, `customerService.js`), and client-side auth state (`authStore.js`).
- Controllers: Thin HTTP adapters that validate DTOs and call service layer methods.
- Services (`*.service` and `*.service.impl`): Business rules, transactions (e.g., registration creates `User` + `Customer`).
- Repositories: Spring Data JPA repositories for `User`, `Customer`, `ServiceEntity`.
- Entities / DTOs: Entities map DB tables; DTOs marshal data across API boundary.

## Important Modules & Files
- `backend/src/main/java/com/abc/postpaid/user/dto/RegisterRequest.java` — registration payload includes `fullName` and `phoneNumber`.
- `backend/.../user/service/impl/AuthServiceImpl.java` — registration logic that creates `User` and linked `Customer` transactionally.
- `backend/.../customer/controller/CustomerController.java` — owner-only customer profile endpoints and `GET /api/customers/me`.
- `backend/.../customer/service/impl/CustomerServiceImpl.java` — service implementations for customer CRUD and service records.
- `backend/.../security/JwtAuthenticationFilter.java` & `SecurityConfig.java` — JWT parsing and security configuration.
- `frontend/src/pages/CustomerProfilePage.jsx` and `frontend/src/services/customerService.js` — UI and API client for customer profile.

## Data Model (concise)
- User: `userId` (PK), `email`, `passwordHash`, `role`.
- Customer: `customerId` (PK), `user` (one-to-one via `user_id`, unique), `fullName`, `address`, `phoneNumber`.
- ServiceEntity: `serviceId`, `customer` (many-to-one), `serviceType`, `startDate`, `status`.

## Authentication & Authorization
- JWT is RS256-signed. Tokens include `sub` (userId) and `role`.
- `JwtAuthenticationFilter` validates token and sets `Authentication` with `principal = sub` and granted authority `ROLE_<ROLE>`.
- Controller owner checks convert `principal` to `Long` and compare with `CustomerResponse.getUserId()` to enforce owner-only access for profile endpoints.

## Typical Request Flow
1. User registers via frontend → `POST /api/auth/register`.
2. `AuthServiceImpl.register()` creates `User` and `Customer` inside a transaction; repository saves to DB.
3. User logs in → receives signed JWT.
4. Subsequent requests include `Authorization: Bearer <token>`; `JwtAuthenticationFilter` sets `SecurityContext`.
5. Controller methods read `SecurityContextHolder.getContext().getAuthentication().getPrincipal()` to identify authenticated user.

## Deployment & Run Notes
- Local backend: `cd backend && mvn spring-boot:run`.
- Local frontend: `cd frontend && npm install && npm run dev`.
- Docker: `docker-compose up --build` (see `docker-compose.yml`).
- Production: avoid `hibernate.ddl-auto=update`; use Flyway or Liquibase for controlled migrations.

## Operational Recommendations
- Do not commit private keys or production secrets to the repo; use secret management.
- Expose `GET /api/customers/me` in login sequence if frontend needs immediate customerId after login.
- Define and document deletion semantics (soft vs hard delete) for `Customer`.

## Testing Strategy
- Unit tests for service layer: mock repositories and exercise success and failure paths.
- Controller tests: MockMvc tests to exercise authorization and DTO mapping.
- Integration tests: SpringBoot tests with test DB or Testcontainers for end-to-end flows (register → login → fetch customer).

## Contacts & Conventions
- Coding style: keep controllers thin; put business logic in service implementations.
- Transactions: annotate service methods that perform multi-entity changes with `@Transactional`.

---
Generated on: December 9, 2025
