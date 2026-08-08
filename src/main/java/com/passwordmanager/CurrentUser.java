package com.passwordmanager;

import com.passwordmanager.exceptions.SessionExpiredException;

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
            throw new SessionExpiredException("Session Expired, Login again");
        }
        return true;
    }

    public void destroy(){
        if(dataKey!=null){
            Arrays.fill(dataKey, (byte)0);
            this.dataKey=null;
        }
        this.userId=null;
    }
}
