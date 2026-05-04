package com.reboot.uam.lib.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for MockMvc-based contract tests. Extends {@link AbstractIntegrationTest}
 * so all containers are available, and pre-configures {@link MockMvc} and
 * a shared {@link ObjectMapper} for JSON assertions.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractMockMvcTest extends AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
}
