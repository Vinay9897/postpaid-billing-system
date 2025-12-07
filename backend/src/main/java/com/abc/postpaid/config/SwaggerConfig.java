package com.abc.postpaid.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration for the Postpaid Billing System API.
 * 
 * Swagger UI is automatically available at: http://localhost:8080/swagger-ui.html
 * OpenAPI JSON spec at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Postpaid Billing System API")
                        .version("1.0.0")
                        .description("ABC Telecom Postpaid Billing System - Manage customers, subscriptions, and billing")
                        .contact(new Contact()
                                .name("ABC Telecom Support")
                                .email("support@abctelecom.com")
                                .url("https://abctelecom.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token (obtained from /api/auth/login)")));
    }
}
