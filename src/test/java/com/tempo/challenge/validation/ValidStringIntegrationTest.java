package com.tempo.challenge.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidStringIntegrationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validString_annotation_withValidData_shouldPass() {
        TestObject obj = new TestObject("Valid String 123");

        Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validString_annotation_withInvalidData_shouldFail() {
        TestObject obj = new TestObject("Invalid@String");

        Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestObject> violation = violations.iterator().next();
        assertEquals("The field must contain only letters, numbers, and spaces", violation.getMessage());
    }

    @Test
    void validString_annotation_withOnlyNumbers_shouldFail() {
        TestObject obj = new TestObject("12345");

        Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<TestObject> violation = violations.iterator().next();
        assertEquals("The field must contain only letters, numbers, and spaces", violation.getMessage());
    }

    @Test
    void validString_annotation_withNullValue_shouldFail() {
        TestObject obj = new TestObject(null);

        Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    // Test class to use the ValidString annotation
    private static class TestObject {
        @ValidString
        private String testField;

        public TestObject(String testField) {
            this.testField = testField;
        }

        public String getTestField() {
            return testField;
        }
    }
}
