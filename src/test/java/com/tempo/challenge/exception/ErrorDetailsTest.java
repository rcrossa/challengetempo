package com.tempo.challenge.exception;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorDetailsTest {

    @Test
    void getStatusCode() {
        ErrorDetails errorDetails = new ErrorDetails(404, "Not Found", "The requested resource was not found");
        assertEquals(404, errorDetails.getStatusCode());
    }

    @Test
    void getMessage() {
        ErrorDetails errorDetails = new ErrorDetails(404, "Not Found", "The requested resource was not found");
        assertEquals("Not Found", errorDetails.getMessage());
    }

    @Test
    void getDetails() {
        ErrorDetails errorDetails = new ErrorDetails(404, "Not Found", "The requested resource was not found");
        assertEquals("The requested resource was not found", errorDetails.getDetails());
    }

    @Test
    void getValidationErrors() {
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("field1", "must not be null");
        ErrorDetails errorDetails = new ErrorDetails(400, validationErrors, "Validation failed");
        assertEquals(validationErrors, errorDetails.getValidationErrors());
    }

    @Test
    void setStatusCode() {
        ErrorDetails errorDetails = new ErrorDetails(404, "Not Found", "The requested resource was not found");
        errorDetails.setStatusCode(500);
        assertEquals(500, errorDetails.getStatusCode());
    }

    @Test
    void setMessage() {
        ErrorDetails errorDetails = new ErrorDetails(404, "Not Found", "The requested resource was not found");
        errorDetails.setMessage("Internal Server Error");
        assertEquals("Internal Server Error", errorDetails.getMessage());
    }

    @Test
    void setDetails() {
        ErrorDetails errorDetails = new ErrorDetails(404, "Not Found", "The requested resource was not found");
        errorDetails.setDetails("An unexpected error occurred");
        assertEquals("An unexpected error occurred", errorDetails.getDetails());
    }

    @Test
    void setValidationErrors() {
        ErrorDetails errorDetails = new ErrorDetails(404, "Not Found", "The requested resource was not found");
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("field1", "must not be null");
        errorDetails.setValidationErrors(validationErrors);
        assertEquals(validationErrors, errorDetails.getValidationErrors());
    }
}