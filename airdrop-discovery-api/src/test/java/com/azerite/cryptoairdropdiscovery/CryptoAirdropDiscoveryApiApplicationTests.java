package com.azerite.cryptoairdropdiscovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Simple bootstrap class for tests.
 */
@SpringBootApplication
class TestApplication {
}

/**
 * Main application test class.
 */
@SpringBootTest
@ActiveProfiles("test")
class CryptoAirdropDiscoveryApiApplicationTests {

    @Test
    void contextLoads() {
        // This test simply checks if the application context loads successfully
    }
} 