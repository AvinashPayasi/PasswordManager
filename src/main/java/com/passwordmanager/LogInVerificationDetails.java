package com.passwordmanager;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class LogInVerificationDetails {
    private final UUID userID;
    private final byte[] verificationSecretKey;
    private final byte[] verificationSalt;
    private int failedLogInAttempts;
    private Instant lockedUntil;

    public LogInVerificationDetails(UUID userID, byte[] verificationSecretKey, byte[] verificationSalt, int failedLogInAttempts, Timestamp lockedUntil){
        this.userID = userID;
        this.verificationSecretKey=verificationSecretKey;
        this.verificationSalt=verificationSalt;
        this.lockedUntil=toInstant(lockedUntil);
        this.failedLogInAttempts = failedLogInAttempts;
    }

    /*public LogInVerificationDetails(UUID userID, byte[] verificationSecretKey, byte[] verificationSalt, byte failedLogInAttempts, Instant lockedUntil){
        this.userID=userID;
        this.verificationSecretKey=verificationSecretKey;
        this.verificationSalt=verificationSalt;
        this.failedLogInAttempts = failedLogInAttempts;
        this.lockedUntil=lockedUntil;
    }*/

    public UUID getUserId(){
        return userID;
    }

    public byte[] getVerificationSecretKey() {
        return verificationSecretKey;
    }

    public byte[] getVerificationSalt() {
        return verificationSalt;
    }

    public int getFailedLogInAttempts(){
        return failedLogInAttempts;
    }

    public Instant getLockedUntil(){
        return lockedUntil;
    }

    public void setFailedLogInAttempts(int failedLogInAttempts){
        this.failedLogInAttempts = failedLogInAttempts;
    }

    public void setLockedUntil(Instant lockedUntil){
        this.lockedUntil=lockedUntil;
    }

    public void overwriteSensitiveInfo(){
        Arrays.fill(verificationSecretKey, (byte)0);
        Arrays.fill(verificationSalt, (byte)0);
    }

    private Instant toInstant(Timestamp timestamp){
        if(timestamp==null){
            return null;
        }else{
            return timestamp.toInstant();
        }
    }

    public Timestamp toTimestamp(Instant instant){
        if(instant==null){
            return null;
        }
        return Timestamp.from(instant);
    }
}
