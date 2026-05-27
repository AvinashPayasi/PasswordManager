package com.passwordmanager;

import java.time.Duration;
import java.time.Instant;

public class BruteForceAttackProtector {

    public LogInVerificationDetails manageFailedLogInAttempt(LogInVerificationDetails logInVerificationDetails) {
        int currentAttempt=logInVerificationDetails.getFailedLogInAttempts()+1;
        switch(currentAttempt){
            case 4 -> {
                logInVerificationDetails.setFailedLogInAttempts(4);
                logInVerificationDetails.setLockedUntil(Instant.now().plus(Duration.ofMinutes(1)));
            }
            case 7 -> {
                logInVerificationDetails.setFailedLogInAttempts(7);
                logInVerificationDetails.setLockedUntil(Instant.now().plus(Duration.ofMinutes(5)));
            }
            case 10 ->  {
                logInVerificationDetails.setFailedLogInAttempts(10);
                logInVerificationDetails.setLockedUntil(Instant.now().plus(Duration.ofMinutes(30)));
            }
            case 13 -> {
                logInVerificationDetails.setFailedLogInAttempts(13);
                logInVerificationDetails.setLockedUntil(Instant.now().plus(Duration.ofHours(6)));
            }
            case 15 -> {
                logInVerificationDetails.setFailedLogInAttempts(15);
                logInVerificationDetails.setLockedUntil(Instant.now().plus(Duration.ofHours(24)));
            }
            case 16 -> {
                logInVerificationDetails.setFailedLogInAttempts(0);
                logInVerificationDetails.setLockedUntil(null);
            }
            default -> {
                logInVerificationDetails.setFailedLogInAttempts(currentAttempt);
                logInVerificationDetails.setLockedUntil(null);
            }
        }
        return logInVerificationDetails;
    }

}
