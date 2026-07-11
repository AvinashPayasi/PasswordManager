package com.passwordmanager.dto;

import com.passwordmanager.AddCredentialRequest;
import com.passwordmanager.DataBlock;

import java.util.UUID;

public class CredentialEntity {
    private final UUID userID;
    private final String username;
    private final String email;
    private final String keyword;
    private final String website;
    private final DataBlock dataBlock;

    public CredentialEntity(UUID userID, AddCredentialRequest addCredentialRequest, DataBlock dataBlock){
        this.userID=userID;
        this.username= addCredentialRequest.getUsername();
        this.email=addCredentialRequest.getEmail();
        this.website=addCredentialRequest.getWebsite();
        this.keyword=addCredentialRequest.getKeyword();
        this.dataBlock=dataBlock;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getWebsite() {
        return website;
    }

    public byte[] getEncryptedData(){
        return dataBlock.getData();
    }

    public byte[] getDataIV(){
        return dataBlock.getIV();
    }
}
