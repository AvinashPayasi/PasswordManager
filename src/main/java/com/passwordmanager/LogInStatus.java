package com.passwordmanager;

public enum LogInStatus {
    WRONG_PASSWORD,
    ACCOUNT_LOCKED,
    LOCKED_1_MINUTE,
    LOCKED_5_MINUTES,
    LOCKED_30_MINUTES,
    LOCKED_6_HOURS,
    LOCKED_24_HOURS,
    NO_USER,
    USER_VERIFIED,
    USER_EXIST,
    UNKNOWN;
}
