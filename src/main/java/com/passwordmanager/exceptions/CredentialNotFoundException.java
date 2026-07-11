package com.passwordmanager.exceptions;

public class CredentialNotFoundException extends RuntimeException {
    public CredentialNotFoundException(String message) {
        super(message);
    }
}
