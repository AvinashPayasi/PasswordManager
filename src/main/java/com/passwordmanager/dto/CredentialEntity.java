package com.passwordmanager.dto;

import com.passwordmanager.DataBlock;
import java.util.UUID;

public class CredentialEntity {
    private final UUID userID;
    private final String username;
    private final String email;
    private final String keyword;
    private final String website;
    private final DataBlock dataBlock;

    public CredentialEntity(UUID userID, String username, String website, String email, String keyword, DataBlock dataBlock){
        this.userID=userID;
        this.username= username;
        this.email=email;
        this.website=website;
        this.keyword=keyword;
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
