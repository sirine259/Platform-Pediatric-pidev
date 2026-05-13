package com.esprit.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.esprit.apigateway")
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @RestController
    static class GatewayController {
        
        @GetMapping("/")
        public Map<String, Object> home() {
            Map<String, Object> response = new HashMap<>();
            response.put("service", "API Gateway");
            response.put("status", "UP");
            response.put("version", "1.0.0");
            return response;
        }

        @GetMapping("/health")
        public Map<String, Object> health() {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("service", "API Gateway");
            return response;
        }
    }
}
