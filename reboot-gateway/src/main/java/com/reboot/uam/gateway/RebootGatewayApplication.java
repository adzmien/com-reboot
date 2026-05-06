package com.reboot.uam.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Reboot API Gateway.
 * Provides routing, JWT validation, and correlation-ID propagation
 * via Spring Cloud Gateway (WebFlux/reactive).
 */
@SpringBootApplication
public class RebootGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebootGatewayApplication.class, args);
    }
}
