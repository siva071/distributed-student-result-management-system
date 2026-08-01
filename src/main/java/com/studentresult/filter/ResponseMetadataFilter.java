package com.studentresult.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to add instance metadata to response headers
 * This helps the dashboard track which instance handled each request
 */
@Component
public class ResponseMetadataFilter extends OncePerRequestFilter {
    
    private final Environment environment;
    
    public ResponseMetadataFilter(Environment environment) {
        this.environment = environment;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Get server port from environment or default
        String port = environment.getProperty("server.port", "8080");
        String hostname = environment.getProperty("HOSTNAME", "unknown");
        String instance = environment.getProperty("INSTANCE_NAME", "student-app-" + port.substring(port.length() - 1));
        
        // Add metadata headers
        response.setHeader("X-Served-By", port);
        response.setHeader("X-Instance", instance);
        response.setHeader("X-Hostname", hostname);
        response.setHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()));
        
        // Expose custom headers to browser (CORS)
        response.setHeader("Access-Control-Expose-Headers", "X-Served-By, X-Instance, X-Hostname, X-Response-Time");
        
        filterChain.doFilter(request, response);
    }
}
