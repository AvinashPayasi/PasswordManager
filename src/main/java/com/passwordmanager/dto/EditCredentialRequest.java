package com.passwordmanager.dto;

public class EditCredentialRequest {
    private String website;
    private String username;
    private String email;
    private byte[] password;
    private String keyword;

    public EditCredentialRequest(String website, String username, String email, byte[] password, String keyword) {
        this.website = website;
        this.username = username;
        this.email = email;
        this.password = password;
        this.keyword = keyword;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
