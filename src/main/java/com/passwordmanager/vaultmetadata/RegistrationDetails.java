package com.passwordmanager.vaultmetadata;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class RegistrationDetails {
    private final byte[] verificationSecretKey;
    private final byte[] verificationSalt;
    private final byte[] encryptionSalt;
    private final byte[] encryptedDataKey;
    private final byte[] dataKeyIV;
    private final byte[] encryptedDataKeyRecovery;
    private final byte[] dataKeyIVRecovery;

    public RegistrationDetails(byte[] verificationSecretKey, byte[] verificationSalt, byte[] encryptedDataKey, byte[] encryptionSalt, byte[] dataKeyIV, byte[] encryptedDataKeyRecovery, byte[] dataKeyIVRecovery){
        this.verificationSecretKey=verificationSecretKey;
        this.verificationSalt=verificationSalt;
        this.encryptedDataKey=encryptedDataKey;
        this.encryptionSalt=encryptionSalt;
        this.dataKeyIV=dataKeyIV;
        this.encryptedDataKeyRecovery=encryptedDataKeyRecovery;
        this.dataKeyIVRecovery=dataKeyIVRecovery;
    }

    public byte[] getVerificationSalt(){
        return verificationSalt;
    }

    public byte[] getVerificationSecretKey(){
        return verificationSecretKey;
    }

    public byte[] getEncryptionSalt(){
        return encryptionSalt;
    }

    public byte[] getEncryptedDataKey(){
        return encryptedDataKey;
    }

    public byte[] getDataKeyIV(){
        return dataKeyIV;
    }

    public byte[] getEncryptedDataKeyRecovery(){
        return encryptedDataKeyRecovery;
    }

    public byte[] getDataKeyIVRecovery(){
        return dataKeyIVRecovery;
    }

}
