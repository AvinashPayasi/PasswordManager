package com.passwordmanager;

public class AddCredentialRequest {

    private String website;
    private String username;
    private String email;
    private byte[] password;
    private String keyword;

    public AddCredentialRequest(String website, String username, String email, byte[] password, String keyword){
        this.website=website;
        this.username=username;
        this.email=email;
        this.password=password;
        this.keyword=keyword;
    }

    public String getWebsite() {
        return website;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getPassword() {
        return password;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
