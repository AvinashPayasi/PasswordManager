package com.passwordmanager.dto;

public class LogInRequest {

    private String email;
    private byte[] password;

    public LogInRequest(String email, byte[] password){
        this.email=email;
        this.password=password;
    }

    public String getEmail(){
        return email;
    }

    public byte[] getPassword(){
        return password;
    }
}
