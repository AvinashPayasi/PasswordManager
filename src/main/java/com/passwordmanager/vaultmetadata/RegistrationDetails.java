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

    public RegistrationDetails(byte[] verificationSecretKey, byte[] verificationSalt, byte[] encryptedDataKey, byte[] encryptionSalt, byte[] dataKeyIV){
        this.verificationSecretKey=verificationSecretKey;
        this.verificationSalt=verificationSalt;
        this.encryptedDataKey=encryptedDataKey;
        this.encryptionSalt=encryptionSalt;
        this.dataKeyIV=dataKeyIV;
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

}
