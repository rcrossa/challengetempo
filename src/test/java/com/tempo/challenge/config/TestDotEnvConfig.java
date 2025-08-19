package com.tempo.challenge.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class TestDotEnvConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestDotEnvConfig.class);
    private final ConfigurableEnvironment environment;

    public TestDotEnvConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            Map<String, Object> dotenvProperties = new HashMap<>();

            if (dotenv != null) {
                dotenv.entries().forEach(entry -> {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        dotenvProperties.put(entry.getKey(), entry.getValue());
                    }
                });

                if (!dotenvProperties.isEmpty()) {
                    environment.getPropertySources().addFirst(
                        new MapPropertySource("dotenvProperties", dotenvProperties)
                    );
                    logger.info("Loaded {} properties from .env file for tests", dotenvProperties.size());
                } else {
                    logger.warn("No valid properties found in .env file");
                }
            } else {
                logger.info("No .env file found, using default test configuration");
            }

        } catch (DotenvException e) {
            logger.error("Error loading .env file for tests: {}", e.getMessage());
            // Continue with default configuration instead of failing
        } catch (Exception e) {
            logger.error("Unexpected error while loading .env file: {}", e.getMessage(), e);
            // Continue with default configuration
        }
    }
}
