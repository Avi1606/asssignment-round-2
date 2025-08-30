package com.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for the Leave Management System
 * 
 * This application provides a comprehensive leave management solution
 * for startups with features including employee management, leave
 * application and approval workflow, and balance tracking.
 * 
 * @author Avi Patel
 */
@SpringBootApplication
@EnableJpaAuditing
public class LeaveManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaveManagementApplication.class, args);
        System.out.println("\n🚀 Leave Management System is running!");
        System.out.println("📱 API Documentation: http://localhost:8080/swagger-ui.html");
        System.out.println("🔍 H2 Database Console: http://localhost:8080/h2-console");
        System.out.println("✨ Application: http://localhost:8080\n");
    }
}