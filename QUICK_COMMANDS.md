# Quick Command Reference - Postpaid Billing System

## 🚀 Quick Start (5 minutes)

### Terminal 1: Start PostgreSQL + Backend
```powershell
cd backend

# Start database
docker-compose up -d

# Build backend (first time only)
mvn clean install -DskipTests

# Run backend on http://localhost:8080
mvn spring-boot:run
```

### Terminal 2: Start Frontend
```powershell
cd frontend

# Install dependencies (first time only)
npm install

# Run dev server on http://localhost:3000
npm run dev
```

### Verify Everything Works
```powershell
# Open browser
http://localhost:3000     # Frontend
http://localhost:8080     # Backend API
```

---

## Backend Commands

### Build & Run
```powershell
cd backend

# Clean build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Database
```powershell
cd backend

# Check database status
docker-compose ps

# View database logs
docker-compose logs postgres

# Stop database
docker-compose down

# Stop and remove data
docker-compose down -v

# Connect to database
docker-compose exec postgres psql -U postgres -d postpaid_billing_db

# Run SQL query in container
docker-compose exec postgres psql -U postgres -d postpaid_billing_db -c "SELECT * FROM users;"
```

### Testing
```powershell
cd backend

# Run all tests
mvn test

# Run single test class
mvn test -Dtest=UserServiceTest

# Run single test method
mvn test -Dtest=UserServiceTest#testRegisterUser

# Run tests with coverage
mvn test jacoco:report

# Skip tests during build
mvn clean install -DskipTests
```

### Migrations
```powershell
cd backend

# Check migration status
mvn flyway:info

# Validate migrations
mvn flyway:validate

# Repair migrations (if locked)
mvn flyway:repair
```

### Building JAR
```powershell
cd backend

# Create production JAR
mvn clean package -DskipTests

# JAR location: target/postpaid-billing-system-1.0.0.jar

# Run JAR directly
java -jar target/postpaid-billing-system-1.0.0.jar
```

### Debugging
```powershell
cd backend

# Run with debug output
mvn spring-boot:run -X

# Show Maven dependency tree
mvn dependency:tree

# Check for outdated dependencies
mvn versions:display-dependency-updates
```

---

## Frontend Commands

### Development
```powershell
cd frontend

# Install dependencies
npm install

# Start dev server (http://localhost:3000)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run linter
npm run lint

# Fix linting issues
npm run lint -- --fix
```

### Testing
```powershell
cd frontend

# Run all tests
npm run test

# Run tests in watch mode
npm run test -- --watch

# Run tests with coverage
npm run test -- --coverage

# Run specific test file
npm run test -- LoginPage
```

### Package Management
```powershell
cd frontend

# Add new package
npm install <package-name>

# Add dev dependency
npm install --save-dev <package-name>

# Remove package
npm uninstall <package-name>

# Update all packages
npm update

# Check for outdated packages
npm outdated

# Audit for security issues
npm audit

# Fix security vulnerabilities
npm audit fix
```

### Cleanup
```powershell
cd frontend

# Clear npm cache
npm cache clean --force

# Remove node_modules and reinstall
rm -r node_modules package-lock.json
npm install
```

---

## Docker Commands

### Start All Services
```powershell
cd backend

# Start in background
docker-compose up -d

# Start with logs visible
docker-compose up

# Start specific service
docker-compose up postgres
```

### Stop All Services
```powershell
# Stop but keep data
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop, remove, and delete data
docker-compose down -v
```

### View Logs
```powershell
cd backend

# View all logs
docker-compose logs

# View specific service
docker-compose logs postgres

# Follow logs in real-time
docker-compose logs -f

# Show last 50 lines
docker-compose logs --tail 50
```

### Database Operations
```powershell
cd backend

# Connect to PostgreSQL shell
docker-compose exec postgres psql -U postgres

# List all databases
docker-compose exec postgres psql -U postgres -l

# Execute SQL file
docker-compose exec postgres psql -U postgres -d postpaid_billing_db -f /path/to/file.sql

# Backup database
docker-compose exec postgres pg_dump -U postgres postpaid_billing_db > backup.sql

# Restore database
docker-compose exec -T postgres psql -U postgres postpaid_billing_db < backup.sql
```

---

## Project Status Commands

### Check Backend
```powershell
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info
```

### Check Frontend
```powershell
# Frontend loads
curl http://localhost:3000

# Check frontend is serving files
curl http://localhost:3000/index.html
```

### Check Database
```powershell
cd backend

# Show all tables
docker-compose exec postgres psql -U postgres -d postpaid_billing_db -c "\dt"

# Show table structure
docker-compose exec postgres psql -U postgres -d postpaid_billing_db -c "\d users"

# Count rows in table
docker-compose exec postgres psql -U postgres -d postpaid_billing_db -c "SELECT COUNT(*) FROM users;"
```

---

## Useful Development Workflows

### Complete Build & Test
```powershell
# Backend
cd backend
mvn clean install
mvn test

# Frontend
cd frontend
npm install
npm run test
```

### Start Fresh Development Environment
```powershell
# Stop everything
docker-compose down -v

# Start database
docker-compose up -d

# Start backend
mvn spring-boot:run

# In another terminal, start frontend
cd frontend
npm run dev
```

### Deploy to Production
```powershell
# Backend JAR
cd backend
mvn clean package -DskipTests
# Deploy target/postpaid-billing-system-1.0.0.jar

# Frontend
cd frontend
npm run build
# Deploy dist/ folder
```

### Troubleshooting
```powershell
# Check Java version
java -version

# Check Node version
node --version

# Check npm version
npm --version

# Check Maven version
mvn --version

# Check Maven builds
mvn clean verify

# Clear all caches
mvn clean
npm cache clean --force
docker system prune
```

---

## Environment Setup Verification

```powershell
# Verify all prerequisites
echo "Java:" && java -version
echo "Maven:" && mvn --version
echo "Node:" && node --version
echo "npm:" && npm --version
echo "Docker:" && docker --version
echo "Docker Compose:" && docker-compose --version
```

---

## Help & Documentation

```powershell
# Maven help
mvn help:describe -Dplugin=org.springframework.boot:spring-boot-maven-plugin

# npm help
npm help

# Check specific npm package info
npm info react

# View installed packages
npm list

# View global packages
npm list -g
```

---

## Common Issues & Solutions

### Port Already in Use
```powershell
# Frontend port 3000 already in use?
# Edit frontend/vite.config.js and change port

# Backend port 8080 already in use?
# Edit backend/src/main/resources/application.yml and change server.port
```

### Database Connection Error
```powershell
# Restart database
cd backend
docker-compose down -v
docker-compose up -d

# Wait for health check
docker-compose exec postgres psql -U postgres -c "SELECT 1"
```

### Build Failures
```powershell
# Clean and rebuild
mvn clean install -DskipTests

# Update Maven
mvn -v

# Check for conflicts
mvn dependency:tree
```

### Node Modules Issues
```powershell
cd frontend
rm -r node_modules package-lock.json
npm install
npm cache clean --force
```

---

**Last Updated**: December 4, 2025
**Project**: Postpaid Billing System - Step 0 Complete
