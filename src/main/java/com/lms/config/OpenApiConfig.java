package com.lms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation
 * 
 * @author Avi Patel
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Leave Management System API")
                        .version("1.0.0")
                        .description("A comprehensive leave management system for tracking employee leaves, approvals, and balances")
                        .contact(new Contact()
                                .name("Avi Patel")
                                .email("avi.patel@example.com")
                                .url("https://github.com/Avi1606"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}