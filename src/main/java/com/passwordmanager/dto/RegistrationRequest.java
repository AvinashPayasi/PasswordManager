package com.passwordmanager.dto;

public class RegistrationRequest {
    private String email;
    private byte[] password;
    private byte[] confirmPassword;

    public RegistrationRequest(String email, byte[] password, byte[] confirmPassword){
        this.email=email;
        this.password=password;
        this.confirmPassword=confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getPassword() {
        return password;
    }

    public byte[] getConfirmPassword() {
        return confirmPassword;
    }
}
