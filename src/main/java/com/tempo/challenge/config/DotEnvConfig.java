package com.tempo.challenge.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotEnvConfig {

    private final ConfigurableEnvironment environment;

    public DotEnvConfig(ConfigurableEnvironment environment) {
        System.out.println("DotEnvConfig constructor called");
        this.environment = environment;
    }

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            Map<String, Object> dotenvProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvProperties.put(entry.getKey(), entry.getValue());
            });

            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenvProperties", dotenvProperties)
            );
        } catch (Exception e) {
            System.err.println("Error loading .env file: " + e.getMessage());
        }
    }
}
