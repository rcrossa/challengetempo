package com.tempo.challenge.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestDotEnvConfigTest {

    @ParameterizedTest
    @MethodSource("loadEnvTestCases")
    void testLoadEnv_ParameterizedScenarios(String testName, boolean shouldThrowException, int expectedCalls) {
        // Create fresh mocks for each test iteration
        ConfigurableEnvironment environment = mock(ConfigurableEnvironment.class);
        MutablePropertySources propertySources = mock(MutablePropertySources.class);
        TestDotEnvConfig testDotEnvConfig = new TestDotEnvConfig(environment);

        // Setup based on test scenario
        if (shouldThrowException) {
            when(environment.getPropertySources()).thenThrow(new RuntimeException("Test exception"));
        } else {
            when(environment.getPropertySources()).thenReturn(propertySources);
        }

        // For multiple calls scenario, call loadEnv multiple times
        if (expectedCalls > 1) {
            for (int i = 0; i < expectedCalls; i++) {
                assertThatNoException().isThrownBy(testDotEnvConfig::loadEnv);
            }
        } else {
            assertThatNoException().isThrownBy(testDotEnvConfig::loadEnv);
        }

        // Verify interactions
        verify(environment, times(expectedCalls)).getPropertySources();
    }

    static Stream<Arguments> loadEnvTestCases() {
        return Stream.of(
            Arguments.of("ValidEnvironment", false, 1),
            Arguments.of("EnvironmentException", true, 1),
            Arguments.of("ValidEnvironmentMultipleCalls", false, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("constructorTestCases")
    void testConstructor_ParameterizedScenarios(String testName, ConfigurableEnvironment env) {
        // Test constructor with different environments
        TestDotEnvConfig config = new TestDotEnvConfig(env);

        // Constructor should always create a non-null object
        assertThat(config).isNotNull();
    }

    static Stream<Arguments> constructorTestCases() {
        return Stream.of(
            Arguments.of("WithValidEnvironment", mock(ConfigurableEnvironment.class)),
            Arguments.of("WithNullEnvironment", (ConfigurableEnvironment) null)
        );
    }
}
