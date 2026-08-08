package com.passwordmanager;

import java.util.Arrays;
import java.util.UUID;

public class ForgetPasswordSession {
    private final UUID userID;
    private final String email;
    private byte[] dataKey;

    public ForgetPasswordSession(UUID userID, String email){
        this.userID=userID;
        this.email=email;
    }

    public UUID getUserID() {
        return userID;
    }

    public byte[] getDataKey(){
        return dataKey;
    }

    public void setDataKey(byte[] dataKey) {
        this.dataKey = dataKey;
    }

    public String getEmail(){
        return email;
    }

    public void destroy(){
        if(dataKey!=null) {
            Arrays.fill(dataKey, (byte) 0);
            dataKey = null;
        }
    }
}
