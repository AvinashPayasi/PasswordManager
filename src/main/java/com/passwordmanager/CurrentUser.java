package com.passwordmanager;

import com.passwordmanager.exceptions.CredentialsExpiredException;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class CurrentUser {

    private UUID userId;
    private byte[] dataKey;
    private final Instant sessionExpiry;

    public CurrentUser(UUID userId, byte[] dataKey){
        this.userId=userId;
        this.dataKey=dataKey;
        this.sessionExpiry=Instant.now().plus(Duration.ofMinutes(5));
    }

    public UUID getUserId(){
        return userId;
    }

    public byte[] getDataKey(){
        isSessionNonExpired();
        return dataKey;
    }

    public boolean isSessionNonExpired(){
        if(Instant.now().isAfter(sessionExpiry)){
            throw new CredentialsExpiredException("Session Expired, Login again");
        }
        return true;
    }

    private void deleteCredentials(){
        Arrays.fill(dataKey, (byte)0);
    }

    public void destroy(){
        deleteCredentials();
        this.dataKey=null;
        this.userId=null;
    }
}
