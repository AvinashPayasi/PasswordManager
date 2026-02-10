package com.passwordmanager.vaultmetadata;

public class VaultMetaData {
    private final String username;
    private final byte[] verificationSecretKey;
    private final byte[] verificationSalt;
    private final byte[] encryptionSalt;
    private final byte[] encryptedDataKey;
    private final byte[] dataKeyIV;

    public VaultMetaData(String username, byte[] verificationSecretKey, byte[] verificationSalt, byte[] encryptionSalt, byte[] encryptedDataKey, byte[] dataKeyIV){
        this.username=username;
        this.verificationSecretKey=verificationSecretKey;
        this.verificationSalt=verificationSalt;
        this.encryptionSalt=encryptionSalt;
        this.encryptedDataKey=encryptedDataKey;
        this.dataKeyIV=dataKeyIV;
    }

    public String getUsername(){
        return username;
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

    /*public void setDataKeyIV(byte[] dataKeyIV){
        this.dataKeyIV=dataKeyIV;
    }

    public void setEncryptedDataKey(byte[] encryptedDataKey){
        this.encryptedDataKey = encryptedDataKey;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public void setVerificationSalt(byte[] verificationSalt){
        this.verificationSalt=verificationSalt;
    }

    public void setVerificationSecretKey(byte[] verificationSecretKey){
        this.verificationSecretKey=verificationSecretKey;
    }

    public void setEncryptionSalt(byte[] encryptionSalt){
        this.encryptionSalt=encryptionSalt;
    }*/

        /*public void removeCredentials(){
            this.username=null;
            Arrays.fill(verificationSalt,(byte)0);
            Arrays.fill(verificationSecretKey,(byte)0);
            Arrays.fill(encryptionSalt,(byte)0);
        }*/
}
