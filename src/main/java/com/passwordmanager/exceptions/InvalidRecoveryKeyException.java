package com.passwordmanager.exceptions;

public class InvalidRecoveryKeyException extends RuntimeException {
    public InvalidRecoveryKeyException(String message) {
        super(message);
    }
}
