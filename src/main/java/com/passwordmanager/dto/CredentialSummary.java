package com.passwordmanager.dto;

public class CredentialSummary {

    private int credentialID;
    private String username;
    private String email;
    private String website;
    private String keyword;

    public CredentialSummary(int credentialID, String username, String email, String website, String keyword){
        this.credentialID=credentialID;
        this.username=username;
        this.email=email;
        this.website=website;
        this.keyword=keyword;
    }

    public int getCredentialID() {
        return credentialID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getKeyword() {
        return keyword;
    }
}
