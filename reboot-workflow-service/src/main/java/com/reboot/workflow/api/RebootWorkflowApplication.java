package com.reboot.workflow.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Reboot Workflow Service.
 * Orchestrates multi-service sagas for submission creation and processing.
 */
@EnableJpaAuditing
@SpringBootApplication
public class RebootWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebootWorkflowApplication.class, args);
    }
}
