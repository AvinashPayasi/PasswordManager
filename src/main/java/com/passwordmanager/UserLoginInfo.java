package com.passwordmanager;

import java.sql.Timestamp;
import java.util.UUID;

public class UserLoginInfo {
    private int loginAttempts;
    private Timestamp currentTime;
    private UUID userID;
    private Timestamp lockedUntil;
    private String interval;

    public UserLoginInfo(UUID userID, int loginAttempts, String interval){
        this.userID = userID;
        this.loginAttempts=loginAttempts;
        this.interval=interval;
    }

    public UserLoginInfo(UUID userID, int loginAttempts, Timestamp lockedUntil, Timestamp currentTime){
        this.userID = userID;
        this.loginAttempts=loginAttempts;
        this.lockedUntil=lockedUntil;
        this.currentTime= currentTime;
    }

    public UserLoginInfo(UUID userID, int loginAttempts){
        this.userID = userID;
        this.loginAttempts=loginAttempts;
    }

    public UUID getUserID(){
        return userID;
    }

    public int getLoginAttempts(){
        return loginAttempts;
    }

    public Timestamp getLockedUntil(){
        return lockedUntil;
    }

    public Timestamp getCurrentTime(){
        return currentTime;
    }

    public String getInterval(){
        return interval;
    }
}
