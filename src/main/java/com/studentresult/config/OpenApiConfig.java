package com.studentresult.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration
 * Configures the API documentation for the application
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI studentResultManagementOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development server");
        
        Contact contact = new Contact();
        contact.setEmail("admin@studentresult.com");
        contact.setName("Student Result Management Team");
        
        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");
        
        Info info = new Info()
                .title("Distributed Student Result Management System API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides endpoints for managing students, subjects, and results. " +
                        "It automatically calculates total marks, grades, and pass/fail status.")
                .license(license);
        
        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
