package com.azerite.cryptoairdropdiscovery.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration properties for the Airdrop Discovery API.
 * These properties can be customized by the users of this starter.
 */
@Data
@ConfigurationProperties(prefix = "azerite.airdrop-discovery")
public class AirdropDiscoveryProperties {

    /**
     * Flag to enable/disable the Airdrop Discovery service.
     */
    private boolean enabled = true;

    /**
     * Keywords configuration for airdrop detection.
     */
    private Keywords keywords = new Keywords();

    /**
     * Database table name configurations.
     */
    private Database database = new Database();

    /**
     * Integration configurations with external services.
     */
    private Integration integration = new Integration();

    /**
     * Keywords configuration for airdrop detection.
     */
    @Data
    public static class Keywords {
        /**
         * List of positive keywords that indicate a tweet is about an airdrop.
         */
        private List<String> positive = Arrays.asList("follow", "mint", "galxe", "zk", "claim");

        /**
         * List of negative keywords that indicate a tweet should be excluded.
         */
        private List<String> negative = Arrays.asList("scam", "ended");
    }

    /**
     * Database configuration.
     */
    @Data
    public static class Database {
        /**
         * Table name for storing airdrop tweets.
         */
        private String tableName = "airdrop_tweets";
    }

    /**
     * Integration with external services configuration.
     */
    @Data
    public static class Integration {
        /**
         * Enable webhook notifications when a new airdrop is detected.
         */
        private boolean webhookEnabled = false;

        /**
         * The webhook URL to notify when a new airdrop is detected.
         */
        private String webhookUrl;
    }
} 