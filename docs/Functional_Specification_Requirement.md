
# Functional Specification Requirement (FSR)
## ABC Telecom – Postpaid Billing System

### 1. Objective
The Postpaid Billing System enables telecom operators to manage customer accounts, track service usage, generate invoices, and process payments with role-based access control.

### 2. User Roles
- **Customer**: View profile, services, usage, invoices, make payments
- **Admin**: Manage users, services, usage, invoices

### 3. Functional Requirements
#### FR-1 User Registration
New users can register with username, email, password. Default role is customer.

#### FR-2 Authentication
Registered users can log in and receive a JWT token.

#### FR-3 Authorization
Role-based access enforced for admin and customer operations.

#### FR-4 Customer Profile
Customers can view their own profile details.

#### FR-5 Service Management
Admins assign services to customers. Customers can view assigned services.

#### FR-6 Usage Tracking
Admins record usage. Customers view usage history.

#### FR-7 Invoice Generation
Admins generate invoices based on usage.

#### FR-8 Invoice Viewing
Customers can view and download invoices.

#### FR-9 Payments
Customers can pay invoices. Payment status updates automatically.

#### FR-10 Admin User Management
Admins can view, update roles, and delete users.

### 4. Data Integrity Rules
- User ↔ Customer (1:1)
- Customer ↔ Service (1:N)
- Service ↔ Usage (1:N)
- Customer ↔ Invoice (1:N)
- Invoice ↔ Payment (1:N)

### 5. Security
- JWT authentication
- Password hashing
- Role-based access control

### 6. Acceptance Criteria
All user stories must function end-to-end without unauthorized access.
