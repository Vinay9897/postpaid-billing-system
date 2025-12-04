package com.abc.postpaid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Postpaid Billing System backend application.
 * Spring Boot auto-configuration will:
 * - Configure embedded Tomcat
 * - Set up JPA/Hibernate ORM with PostgreSQL
 * - Enable Spring Security with JWT support
 * - Run Flyway database migrations on startup
 */
@SpringBootApplication
public class PostpaidBillingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostpaidBillingSystemApplication.class, args);
    }

}
