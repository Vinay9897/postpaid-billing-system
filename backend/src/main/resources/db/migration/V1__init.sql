-- Flyway Migration V1: Initialize database schema
-- Creates all required tables with exact fields from the Postpaid Billing System specification

-- Create Users table
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'customer',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create Customers table
CREATE TABLE customers (
    customer_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone_number VARCHAR(20),
    CONSTRAINT fk_customers_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create Services table (subscription records)
CREATE TABLE services (
    service_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_services_customer_id FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Create UsageRecords table
CREATE TABLE usage_records (
    usage_id BIGSERIAL PRIMARY KEY,
    service_id BIGINT NOT NULL,
    usage_date DATE NOT NULL,
    usage_amount NUMERIC(12, 2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    CONSTRAINT fk_usage_records_service_id FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
);

-- Create Invoices table
CREATE TABLE invoices (
    invoice_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    billing_period_start DATE NOT NULL,
    billing_period_end DATE NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'draft',
    CONSTRAINT fk_invoices_customer_id FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Create Payments table
CREATE TABLE payments (
    payment_id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount NUMERIC(12, 2) NOT NULL,
    payment_method VARCHAR(100) NOT NULL,
    CONSTRAINT fk_payments_invoice_id FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id) ON DELETE CASCADE
);

-- Create indexes for common queries
CREATE INDEX idx_customers_user_id ON customers(user_id);
CREATE INDEX idx_services_customer_id ON services(customer_id);
CREATE INDEX idx_usage_records_service_id ON usage_records(service_id);
CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
