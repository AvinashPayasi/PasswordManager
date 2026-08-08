package com.passwordmanager.cryptography;

import com.passwordmanager.CurrentUserDetails;
import com.passwordmanager.DataBlock;
import com.passwordmanager.exceptions.InternalServerError;
import com.passwordmanager.exceptions.InvalidRecoveryKeyException;
import com.passwordmanager.vaultmetadata.RegistrationDetails;

import javax.crypto.AEADBadTagException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class CryptoService {
    private final CryptoUtil cryptoUtil=new CryptoUtil();

    public byte[] deriveSecretKey(byte[] key, byte[] salt){
        return cryptoUtil.genSecretKey(key, salt);
    }

    public RegistrationDetails createVaultMetaData(byte[] password, byte[] dataKey, byte[] recoveryKey){
        byte[] verificationSalt=cryptoUtil.genSalt();
        byte[] encryptionSalt =cryptoUtil.genSalt();
        byte[] verificationSecretKey = cryptoUtil.genSecretKey(password,verificationSalt);
        byte[] masterKey=cryptoUtil.genSecretKey(password,encryptionSalt);
        byte[] dataKeyIV=cryptoUtil.genIV();
        byte[] dataKeyIVRecovery=cryptoUtil.genIV();
        try {
            byte[] encryptedDataKey = cryptoUtil.encryptData(masterKey, dataKey, dataKeyIV);
            byte[] encryptedDataKeyRecovery = cryptoUtil.encryptData(recoveryKey, dataKey, dataKeyIVRecovery);
            RegistrationDetails registrationDetails = new RegistrationDetails(verificationSecretKey, verificationSalt, encryptedDataKey, encryptionSalt, dataKeyIV, encryptedDataKeyRecovery, dataKeyIVRecovery);
            return registrationDetails;
        }catch (GeneralSecurityException generalSecurityException){
            throw new InternalServerError("Something went wrong");
        }
    }

    public byte[] genDataKey(){
        return cryptoUtil.genDataKey();
    }

    public byte[] getDataKey(byte[] password, CurrentUserDetails currentUserDetails){
        byte[] masterKey=cryptoUtil.genSecretKey(password, currentUserDetails.getEncryptionSalt());
        Arrays.fill(password,(byte)0);
        try {
            byte[] dataKey = cryptoUtil.decryptData(masterKey, currentUserDetails.getEncryptedDataKey(), currentUserDetails.getDataKeyIV());
            currentUserDetails.overwriteSensitiveInfo();
            return dataKey;
        }catch (GeneralSecurityException generalSecurityException){
            throw new InternalServerError("Something went wrong");
        }
    }

    public DataBlock encryptData(byte[] key, byte[] data){
        try {
            byte[] iv = cryptoUtil.genIV();
            byte[] encryptedData=cryptoUtil.encryptData(key, data, iv);
            Arrays.fill(data,(byte)0);
            return new DataBlock(encryptedData,iv);
        }catch (GeneralSecurityException generalSecurityException){
            throw new InternalServerError("Something went wrong");
        }
    }

    public byte[] decryptData(byte[] key,DataBlock dataBlock){
        try{
            byte[] encryptedData = dataBlock.getData();
            byte[] iv = dataBlock.getIV();
            byte[] password=cryptoUtil.decryptData(key, encryptedData, iv);
            return password;
        }catch (AEADBadTagException aeadBadTagException){
            throw new InvalidRecoveryKeyException("Invalid Recovery Key");
        }catch (GeneralSecurityException generalSecurityException){
            throw new InternalServerError("Something went wrong");
        }finally {
            Arrays.fill(key,(byte)0);
        }
    }

}
