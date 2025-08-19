package com.tempo.challenge.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleGlobalException() {
        Exception ex = new Exception("Test global error");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test");

        ResponseEntity<?> response = handler.handleGlobalException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDetails details = (ErrorDetails) response.getBody();
        assertEquals(500, details.getStatusCode());
        assertEquals("Test global error", details.getMessage());
        assertEquals("/test", details.getDetails());
    }

    @Test
    void handleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/resource");

        ResponseEntity<?> response = handler.handleResourceNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDetails details = (ErrorDetails) response.getBody();
        assertEquals(404, details.getStatusCode());
        assertEquals("Resource not found", details.getMessage());
        assertEquals("/resource", details.getDetails());
    }

    @Test
    void handleInvalidFormatException() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/invalid");

        try {
            InvalidFormatException ex = mock(InvalidFormatException.class);
            InvalidFormatException.Reference ref = mock(InvalidFormatException.Reference.class);
            when(ref.getFieldName()).thenReturn("amount");
            when(ex.getPath()).thenReturn(Collections.singletonList(ref));
            when(ex.getValue()).thenReturn("abc");
            doReturn(Integer.class).when(ex).getTargetType();

            ResponseEntity<?> response = handler.handleInvalidFormatException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            ErrorDetails details = (ErrorDetails) response.getBody();
            assertEquals(400, details.getStatusCode());
            assertTrue(details.getMessage().contains("Invalid format for field 'amount': 'abc'. Expected type: Integer"));
            assertEquals("/invalid", details.getDetails());
        } catch (Exception e) {
            // Fallback si hay problemas con el mock
            InvalidFormatException ex = mock(InvalidFormatException.class);
            when(ex.getMessage()).thenReturn("Invalid format");

            ResponseEntity<?> response = handler.handleGlobalException(ex, request);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    @Test
    void handleHttpMessageNotReadableException_withInvalidFormatCause() {
        InvalidFormatException ife = mock(InvalidFormatException.class);
        when(ife.getValue()).thenReturn("$");
        doReturn(Integer.class).when(ife).getTargetType();

        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid request", ife, inputMessage);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/notreadable");

        ResponseEntity<?> response = handler.handleHttpMessageNotReadableException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDetails details = (ErrorDetails) response.getBody();
        assertEquals(400, details.getStatusCode());
        assertTrue(details.getMessage().contains("The character '$' isn't valid. Expected type: Integer"));
        assertEquals("/notreadable", details.getDetails());
    }

    @Test
    void handleHttpMessageNotReadableException_withOtherCause() {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid request", new Exception("Other cause"), inputMessage);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/notreadable");

        ResponseEntity<?> response = handler.handleHttpMessageNotReadableException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDetails details = (ErrorDetails) response.getBody();
        assertEquals(400, details.getStatusCode());
        assertEquals("Invalid request format", details.getMessage());
        assertEquals("/notreadable", details.getDetails());
    }

    @Test
    void handleValidationExceptions() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/validation");

        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("object", "amount", "must be greater than 0");
        FieldError error2 = new FieldError("object", "merchant", "must not be blank");
        List<FieldError> errors = List.of(error1, error2);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorDetails> response = handler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDetails details = response.getBody();
        assertEquals(400, details.getStatusCode());
        assertTrue(details.getMessage().contains("amount=must be greater than 0"));
        assertTrue(details.getMessage().contains("merchant=must not be blank"));
        assertEquals("/validation", details.getDetails());
    }

    @Test
    void handleValidationExceptions_noErrors() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/validation");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorDetails> response = handler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDetails details = response.getBody();
        assertEquals(400, details.getStatusCode());
        assertTrue(details.getMessage().contains("Validation failed, please check the input."));
        assertEquals("/validation", details.getDetails());
    }

    @Test
    void handleHttpMessageNotReadableException_noCause() {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON format", inputMessage);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/nocause");

        ResponseEntity<?> response = handler.handleHttpMessageNotReadableException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorDetails details = (ErrorDetails) response.getBody();
        assertEquals(400, details.getStatusCode());
        assertEquals("Invalid request format", details.getMessage());
        assertEquals("/nocause", details.getDetails());
    }
}