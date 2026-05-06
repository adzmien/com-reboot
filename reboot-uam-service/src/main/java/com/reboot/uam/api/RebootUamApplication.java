package com.reboot.uam.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Reboot User Access Management Service.
 * Manages internal users, roles, and customer records.
 */
@SpringBootApplication
public class RebootUamApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebootUamApplication.class, args);
    }
}
