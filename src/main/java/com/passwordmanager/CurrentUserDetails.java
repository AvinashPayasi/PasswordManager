package com.passwordmanager;

import java.util.Arrays;

public class CurrentUserDetails {
    private final byte[] encryptedDataKey;
    private final byte[] encryptionSalt;
    private final byte[] dataKeyIV;

    public CurrentUserDetails(byte[] encryptedDataKey, byte[] encryptionSalt, byte[] dataKeyIV){
        this.encryptedDataKey=encryptedDataKey;
        this.encryptionSalt=encryptionSalt;
        this.dataKeyIV=dataKeyIV;
    }

    public byte[] getEncryptedDataKey() {
        return encryptedDataKey;
    }

    public byte[] getEncryptionSalt() {
        return encryptionSalt;
    }

    public byte[] getDataKeyIV() {
        return dataKeyIV;
    }

    public void overwriteSensitiveInfo(){
        Arrays.fill(encryptedDataKey, (byte)0);
        Arrays.fill(encryptionSalt, (byte)0);
        Arrays.fill(dataKeyIV, (byte)0);
    }
}
