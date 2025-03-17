package com.tempo.challenge.exception;

public class ClientTransactionLimitException extends RuntimeException {
    public ClientTransactionLimitException(String message) {
        super(message);
    }
}

