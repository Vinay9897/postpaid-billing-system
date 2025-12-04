# Postpaid Billing System - Backend

Backend API for the ABC Telecom Postpaid Billing System built with Spring Boot 3.2, Java 21, and PostgreSQL.

## Prerequisites

- **Java 21** or higher
- **Maven 3.9+**
- **PostgreSQL 14+** (or use docker-compose)
- **Git**

## Quick Start

### 1. Setup PostgreSQL Database

**Option A: Using Docker Compose (Recommended)**

```powershell
cd backend
docker-compose up -d
```

This starts a PostgreSQL container on `localhost:5432` with:
- Database: `postpaid_billing_db`
- User: `postgres`
- Password: `postgres`

**Option B: Manual PostgreSQL Setup**

Create database and user:
```sql
CREATE DATABASE postpaid_billing_db;
CREATE USER postgres WITH PASSWORD 'postgres';
ALTER ROLE postgres WITH SUPERUSER;
GRANT ALL PRIVILEGES ON DATABASE postpaid_billing_db TO postgres;
```

### 2. Build the Backend

```powershell
mvn clean install -DskipTests
```

### 3. Run Flyway Migrations

Migrations run automatically on application startup. To verify:

```powershell
mvn flyway:info
```

### 4. Start the Application

```powershell
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints (by Step)

### Step 0: Project Setup (Current)
- No endpoints in this step
- Database tables created via Flyway

### Step 1: Authentication (Register & Login)
- `POST /api/register` - User registration
- `POST /api/login` - User login (returns JWT)

### Step 2: User Management (Admin Only)
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Step 3: Customer Profile
- `GET /api/customers/{id}` - Get customer profile
- `PUT /api/customers/{id}` - Update customer profile

### Step 4: Services (Subscriptions)
- `GET /api/customers/{id}/services` - List services
- `POST /api/customers/{id}/services` - Add service

### Step 5: Usage Records
- `POST /api/services/{id}/usage` - Record usage
- `GET /api/services/{id}/usage` - View usage history

### Step 6: Billing & Invoices
- `POST /api/customers/{id}/invoices` - Generate invoice
- `GET /api/customers/{id}/invoices` - List invoices
- `GET /api/customers/{id}/invoices/{invoiceId}` - Invoice details
- `GET /api/customers/{id}/invoices/{invoiceId}/download` - Download PDF

### Step 7: Payments
- `POST /api/invoices/{id}/payments` - Record payment
- `GET /api/invoices/{id}/payments` - List payments

## Database Schema

All tables created by Flyway migration `V1__init.sql`:

### users
- `user_id` (PK)
- `username` (unique)
- `password_hash`
- `email` (unique)
- `role` (customer, admin)
- `created_at`

### customers
- `customer_id` (PK)
- `user_id` (FK → users)
- `full_name`
- `address`
- `phone_number`

### services
- `service_id` (PK)
- `customer_id` (FK → customers)
- `service_type`
- `start_date`
- `status`

### usage_records
- `usage_id` (PK)
- `service_id` (FK → services)
- `usage_date`
- `usage_amount`
- `unit`

### invoices
- `invoice_id` (PK)
- `customer_id` (FK → customers)
- `billing_period_start`
- `billing_period_end`
- `total_amount`
- `status`

### payments
- `payment_id` (PK)
- `invoice_id` (FK → invoices)
- `payment_date`
- `amount`
- `payment_method`

## Running Tests

```powershell
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

## Project Structure

```
backend/
├── pom.xml
├── README.md
├── docker-compose.yml
├── src/
│   ├── main/
│   │   ├── java/com/abc/postpaid/
│   │   │   ├── config/            # Spring configuration beans
│   │   │   ├── entity/            # JPA entities (Lombok annotated)
│   │   │   ├── dto/               # Data Transfer Objects (Lombok annotated)
│   │   │   ├── controller/        # REST controllers
│   │   │   ├── service/           # Service interfaces
│   │   │   ├── service/impl/      # Service implementations
│   │   │   ├── repository/        # Spring Data JPA repositories
│   │   │   ├── security/          # JWT & security config
│   │   │   ├── exception/         # Custom exceptions
│   │   │   ├── util/              # Utility classes
│   │   │   └── PostpaidBillingSystemApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/      # Flyway SQL migrations
│   └── test/
│       └── java/com/abc/postpaid/  # Unit & integration tests
└── target/                          # Build output
```

## Development Guidelines

### Entities & DTOs
- All entities and DTOs use **Lombok** (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- Never add fields beyond those in the master prompt
- Entities use `@Entity` and `@Table` annotations

### Services
- All services follow interface-based pattern:
  - Interfaces in `com.abc.postpaid.<domain>.service`
  - Implementations in `com.abc.postpaid.<domain>.service.impl`
- Mark as `@Service` stereotype
- Use `@Transactional` for data modification methods

### Controllers
- Paths: `/api/<resource>`
- Use `@RestController` and `@RequestMapping`
- RBAC enforced via `@PreAuthorize` annotations
- Return appropriate HTTP status codes

### Authentication
- JWT tokens use RS256 algorithm
- Stateless (no session storage)
- Token claims: `sub` (user_id), `role`
- Short-lived (15 minutes default)

### Testing
- Use **JUnit 5** and **Mockito**
- Integration tests use **H2** in-memory database
- Test one concern per test method
- Use descriptive test names

## Configuration

### PostgreSQL Connection
Edit `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postpaid_billing_db
    username: postgres
    password: postgres
```

### JWT Configuration
Will be added in Step 1 (Security configuration class)

## Troubleshooting

### Build Fails
```powershell
# Clean and rebuild
mvn clean install -DskipTests

# Check Java version
java -version  # Should be 21+
```

### Database Connection Error
```powershell
# Verify PostgreSQL is running
docker-compose ps

# Check logs
docker-compose logs postgres
```

### Port 8080 Already in Use
Edit `application.yml` and change `server.port`

## Next Steps

1. **Step 1**: Implement authentication (register/login with JWT)
2. **Step 2**: Implement admin user management
3. **Step 3**: Implement customer profile endpoints
4. Proceed through Steps 4-8 as outlined in the master prompt

## References

- [Spring Boot 3.2 Documentation](https://spring.io/projects/spring-boot)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Security with JWT](https://spring.io/guides/tutorials/spring-boot-oauth2/)
- [Java 21 Features](https://www.oracle.com/java/technologies/javase/21-relnotes.html)
