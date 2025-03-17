package com.tempo.challenge.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


public class ErrorDetails {
    @JsonProperty("statuscode")
    private  int statusCode;
    @JsonProperty("message")
    private  String message;
    @JsonProperty("details")
    private  String details;

    private Map<String, String> validationErrors;

    public ErrorDetails(int statusCode, String message, String details) {
        this.statusCode = statusCode;
        this.message = message;
        this.details = details;
    }

    public ErrorDetails(int statusCode, Map<String, String> validationErrors, String details) {
        this.statusCode = statusCode;
        this.validationErrors = validationErrors;
        this.details = details;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
