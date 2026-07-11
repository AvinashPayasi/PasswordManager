package com.passwordmanager.dto;

import com.passwordmanager.DataBlock;

import java.time.Instant;

public class CredentialResponse {

    private final String username;
    private final String email;
    private final String website;
    private final String keyword;
    private final Instant createdAt;
    private final Instant updatedAt;

    public CredentialResponse(String username, String email, String website, String keyword, Instant createdAt, Instant updatedAt){
        this.username=username;
        this.email=email;
        this.website=website;
        this.keyword=keyword;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public String getWebsite(){
        return website;
    }

    public String getKeyword(){
        return keyword;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }

    public Instant getUpdatedAt(){
        return updatedAt;
    }


}
