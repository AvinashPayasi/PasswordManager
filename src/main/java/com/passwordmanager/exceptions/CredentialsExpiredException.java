package com.passwordmanager.exceptions;

public class CredentialsExpiredException extends RuntimeException {
    public CredentialsExpiredException(String message) {
        super(message);
    }
}
