package com.azerite.cryptoairdropdiscovery.config;

import com.azerite.cryptoairdropdiscovery.controller.AirdropController;
import com.azerite.cryptoairdropdiscovery.repository.AirdropTweetRepository;
import com.azerite.cryptoairdropdiscovery.service.AirdropDiscoveryService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Auto-configuration for Crypto Airdrop Discovery API.
 * This configuration will be automatically loaded when the starter is included in a Spring Boot project.
 */
@Configuration
@AutoConfiguration
@EnableConfigurationProperties(AirdropDiscoveryProperties.class)
@ConditionalOnProperty(prefix = "azerite.airdrop-discovery", name = "enabled", havingValue = "true", matchIfMissing = true)
@EntityScan(basePackages = "com.azerite.cryptoairdropdiscovery.model")
@EnableJpaRepositories(basePackages = "com.azerite.cryptoairdropdiscovery.repository")
@ComponentScan(basePackages = {
        "com.azerite.cryptoairdropdiscovery.service",
        "com.azerite.cryptoairdropdiscovery.controller"
})
public class AirdropDiscoveryAutoConfiguration {

    /**
     * Creates an AirdropDiscoveryService bean if one does not already exist.
     *
     * @param repository The repository for accessing tweet data
     * @param properties Configuration properties for the service
     * @return A new AirdropDiscoveryService instance
     */
    @Bean
    @ConditionalOnMissingBean
    public AirdropDiscoveryService airdropDiscoveryService(AirdropTweetRepository repository,
                                                           AirdropDiscoveryProperties properties) {
        return new AirdropDiscoveryService(repository, properties);
    }

    /**
     * Creates an AirdropController bean if the service exists and a controller does not already exist.
     *
     * @param service The airdrop discovery service
     * @return A new AirdropController instance
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(AirdropDiscoveryService.class)
    public AirdropController airdropController(AirdropDiscoveryService service) {
        return new AirdropController(service);
    }
} 