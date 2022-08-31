package org.grad.eNav.atonServiceClient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Application Context Test.
 */
@SpringBootTest
@TestPropertySource("classpath:application.properties")
@Import(TestingConfiguration.class)
class AtonServiceClientTest {

    /**
     * Test tha the context loads correctly.
     */
    @Test
    void contextLoads() {
    }
}