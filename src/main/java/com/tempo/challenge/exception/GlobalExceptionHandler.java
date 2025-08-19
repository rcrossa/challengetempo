package com.tempo.challenge.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorDetails> handleInvalidFormatException(InvalidFormatException ex, WebRequest request) {
        String message = String.format("Invalid format for field '%s': '%s'. Expected type: %s",
                ex.getPath().get(0).getFieldName(), ex.getValue(), ex.getTargetType().getSimpleName());
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), message, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        Throwable cause = ex.getCause();
        String message = "Invalid request format";
        if (cause instanceof InvalidFormatException ife) {
            message = String.format("The character '%s' isn't valid. Expected type: %s",
                    ife.getValue(), ife.getTargetType().getSimpleName());
        }
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), message, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {

        StringBuilder validationErrors = new StringBuilder();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if (validationErrors.isEmpty()) {
                validationErrors.append(", ");
            }
            validationErrors.append(error.getField())
                    .append("=")
                    .append(error.getDefaultMessage());
        }

        if (validationErrors.isEmpty()) {
            validationErrors.append("Validation failed, please check the input.");
        }


        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), validationErrors.toString(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}