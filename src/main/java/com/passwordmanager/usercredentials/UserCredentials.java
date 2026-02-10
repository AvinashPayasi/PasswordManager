package com.passwordmanager.usercredentials;

import com.passwordmanager.Cryptography;
import com.passwordmanager.DataBlock;

import java.util.UUID;

public class UserCredentials {
    private UUID userID;
    private String username;
    private String email;
    private byte[] password;
    private String tag;
    private String keyword;
    private String website;
    private DataBlock dataBlock;

    private Cryptography cryptography=new Cryptography();

    public UserCredentials(){}

    public UserCredentials(String username, String email, String tag, String keyword, String website){
        this.username=username;
        this.email=email;
        this.tag=tag;
        this.keyword=keyword;
        this.website=website;
    }

    public void setUserID(UUID userID){
        this.userID=userID;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public UUID getUserID(){
        return userID;
    }

    public String getUsername(){
        return this.username;
    }

    public String getEmail(){
        return this.email;
    }

    public byte[] getPassword(){
        return password;
    }

    public String getTag(){
        return tag;
    }

    public String getKeyword(){
        return keyword;
    }

    public String getWebsite(){
        return website;
    }

    public void setEncryptionInfo(DataBlock dataBlock){
        this.dataBlock=dataBlock;
    }

    public DataBlock getEncryptionInfo(boolean isEncrypted){
        if(isEncrypted){
            return dataBlock;
        }
        DataBlock dataBlock=cryptography.startEncryption(password);
        return dataBlock;
    }
}
