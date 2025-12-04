# Postpaid Billing System

Complete implementation of ABC Telecom's Postpaid Billing System with Spring Boot backend and React frontend.

## Project Overview

This is a full-stack billing management system built with:
- **Backend**: Spring Boot 3.2, Java 21, PostgreSQL, JWT Authentication
- **Frontend**: React 18, Vite, React Router, Axios, Zustand
- **Database**: PostgreSQL with Flyway migrations
- **Testing**: JUnit 5, Mockito, Vitest, React Testing Library

## Project Structure

```
postpaid-billing-system/
├── backend/              # Spring Boot REST API
│   ├── pom.xml
│   ├── README.md
│   ├── docker-compose.yml
│   └── src/
│       ├── main/
│       │   ├── java/com/abc/postpaid/
│       │   └── resources/db/migration/
│       └── test/
├── frontend/             # React web application
│   ├── package.json
│   ├── vite.config.js
│   ├── README.md
│   └── src/
└── .github/
    └── prompts/
        └── postpaid-billing-system-prompt.prompt.md
```

## Quick Start

### Prerequisites

- **Java 21+** and **Maven 3.9+**
- **Node.js 18+** and **npm**
- **PostgreSQL 14+** (or Docker)
- **Git**

### Step 1: Clone & Setup Backend

```powershell
cd backend

# Start PostgreSQL (Docker)
docker-compose up -d

# Build backend
mvn clean install

# Run backend (starts on http://localhost:8080)
mvn spring-boot:run
```

### Step 2: Setup Frontend

```powershell
cd frontend

# Install dependencies
npm install

# Start development server (starts on http://localhost:3000)
npm run dev
```

### Step 3: Verify Setup

- Backend API: http://localhost:8080
- Frontend: http://localhost:3000
- Check `/api/health` endpoint for backend status

## Development Roadmap (8 Steps)

| Step | Feature | Status |
|------|---------|--------|
| 0 | Project skeleton & DB migration | ✅ Complete |
| 1 | Authentication (Register & Login) | ⏳ Next |
| 2 | User management (admin) | ⏳ Pending |
| 3 | Customer profile | ⏳ Pending |
| 4 | Services (subscriptions) | ⏳ Pending |
| 5 | Usage records | ⏳ Pending |
| 6 | Billing & invoices | ⏳ Pending |
| 7 | Payments & idempotency | ⏳ Pending |
| 8 | Audit & test integration | ⏳ Pending |

## Database Schema

### Entities (All created by Flyway V1__init.sql)

- **users**: User accounts with JWT authentication
- **customers**: Customer profiles linked to users
- **services**: Subscription records for customers
- **usage_records**: Usage data for billing calculations
- **invoices**: Generated billing invoices
- **payments**: Payment records for invoices

See [Database Documentation](./docs/DATABASE.md) for detailed schema.

## API Documentation

### Base URL
`http://localhost:8080/api`

### Authentication
All endpoints (except `/register` and `/login`) require JWT Bearer token:
```
Authorization: Bearer <access_token>
```

### Key Endpoints

**Authentication (Step 1)**
- `POST /api/register` - Register new user
- `POST /api/login` - Login and get JWT token

**Users (Step 2)**
- `GET /api/users/{id}` - Get user details (admin only)
- `PUT /api/users/{id}` - Update user (admin only)
- `DELETE /api/users/{id}` - Delete user (admin only)

**Customers (Step 3)**
- `GET /api/customers/{id}` - Get customer profile
- `PUT /api/customers/{id}` - Update profile

**Services (Step 4)**
- `GET /api/customers/{id}/services` - List services
- `POST /api/customers/{id}/services` - Add service (admin)

**Usage (Step 5)**
- `POST /api/services/{id}/usage` - Record usage (admin)
- `GET /api/services/{id}/usage` - Get usage history

**Invoices (Step 6)**
- `POST /api/customers/{id}/invoices` - Generate invoice (admin)
- `GET /api/customers/{id}/invoices` - List invoices
- `GET /api/customers/{id}/invoices/{invoiceId}` - Invoice details
- `GET /api/customers/{id}/invoices/{invoiceId}/download` - PDF download

**Payments (Step 7)**
- `POST /api/invoices/{id}/payments` - Record payment
- `GET /api/invoices/{id}/payments` - List payments

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2
- **Language**: Java 21
- **Database**: PostgreSQL 14+
- **ORM**: Hibernate/JPA
- **Security**: Spring Security + JWT (RS256)
- **Migrations**: Flyway
- **Build**: Maven
- **Testing**: JUnit 5, Mockito, H2

### Frontend
- **Framework**: React 18
- **Build Tool**: Vite 5
- **Routing**: React Router 6
- **HTTP Client**: Axios
- **State Management**: Zustand
- **Styling**: CSS3
- **Testing**: Vitest, React Testing Library
- **Linting**: ESLint

## Development Guidelines

### Backend (Java/Spring Boot)

1. **Entities & DTOs**: Use Lombok (`@Data`, `@Builder`)
2. **Services**: Interface-based pattern with `impl` suffix
3. **Controllers**: RESTful with `@RestController` and RBAC
4. **Testing**: JUnit 5 unit tests + H2 integration tests
5. **Code Style**: Follow Spring Boot conventions

### Frontend (React)

1. **Components**: Functional components with hooks
2. **API**: Axios service functions for backend calls
3. **State**: Zustand stores for global state
4. **Routing**: React Router for navigation
5. **Testing**: Test components with React Testing Library

## Running Tests

### Backend
```powershell
cd backend

# Run all tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

### Frontend
```powershell
cd frontend

# Run all tests
npm run test

# Run tests in watch mode
npm run test -- --watch

# Run with coverage
npm run test -- --coverage
```

## Build for Production

### Backend
```powershell
cd backend
mvn clean package -DskipTests
# JAR available at target/postpaid-billing-system-1.0.0.jar
```

### Frontend
```powershell
cd frontend
npm run build
# Build output in dist/
```

## Docker Deployment

```powershell
# Backend
cd backend
docker build -t postpaid-billing-backend .
docker run -p 8080:8080 postpaid-billing-backend

# Frontend
cd frontend
docker build -t postpaid-billing-frontend .
docker run -p 3000:3000 postpaid-billing-frontend
```

## Troubleshooting

### Backend Won't Start
```powershell
# Check Java version
java -version  # Should be 21+

# Check PostgreSQL is running
docker-compose ps

# Check logs
mvn spring-boot:run -X
```

### Frontend Won't Start
```powershell
# Check Node version
node --version  # Should be 18+

# Clear node_modules
rm -r node_modules package-lock.json
npm install

# Check port 3000
netstat -ano | findstr :3000
```

### Database Connection Error
```powershell
# Verify PostgreSQL
docker-compose logs postgres
docker-compose exec postgres psql -U postgres -c "SELECT 1"
```

## Performance Considerations

- JWT tokens are short-lived (15 minutes)
- Database indexes on foreign keys
- Connection pooling configured
- API responses paginated where applicable
- Frontend lazy loading for components

## Security

- **Authentication**: JWT with RS256
- **Authorization**: Role-Based Access Control (RBAC)
- **Passwords**: BCrypt hashing
- **CORS**: Configured for localhost development
- **HTTPS**: Enable in production
- **Secrets**: Use environment variables, not in code

## Contributing

1. Follow the step-by-step development roadmap
2. Implement with full test coverage
3. Use consistent naming conventions
4. Document API changes
5. Test locally before committing

## References

- [Master Prompt](./github/prompts/postpaid-billing-system-prompt.prompt.md)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## License

Internal - ABC Telecom

## Contact

Development Team - ABC Telecom
