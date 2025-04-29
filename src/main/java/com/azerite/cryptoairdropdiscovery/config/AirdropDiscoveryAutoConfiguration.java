package com.azerite.cryptoairdropdiscovery.config;

import com.azerite.cryptoairdropdiscovery.repository.AirdropTweetRepository;
import com.azerite.cryptoairdropdiscovery.service.AirdropDiscoveryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Auto-configuration for Crypto Airdrop Discovery API.
 * This configuration will be automatically loaded when the starter is included in a Spring Boot project.
 */
@Configuration
@ComponentScan(basePackages = "com.azerite.cryptoairdropdiscovery")
@EntityScan(basePackages = "com.azerite.cryptoairdropdiscovery.model")
@EnableJpaRepositories(basePackages = "com.azerite.cryptoairdropdiscovery.repository")
public class AirdropDiscoveryAutoConfiguration {

    /**
     * Creates an AirdropDiscoveryService bean if one does not already exist.
     *
     * @param repository The repository for accessing tweet data
     * @return A new AirdropDiscoveryService instance
     */
    @Bean
    @ConditionalOnMissingBean
    public AirdropDiscoveryService airdropDiscoveryService(AirdropTweetRepository repository) {
        return new AirdropDiscoveryService(repository);
    }
} 