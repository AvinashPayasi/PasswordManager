package com.passwordmanager.cryptography;

import com.passwordmanager.vaultmetadata.VaultMetaData;

import java.time.Instant;
import java.util.UUID;

public class CryptoService {
    private final CryptoUtil cryptoUtil=new CryptoUtil();

    public byte[] deriveSecretKey(byte[] key, byte[] salt){
        return cryptoUtil.genSecretKey(key, salt);
    }

    public VaultMetaData getVaultMetaData(String email, byte[] masterPassword){
        byte[] verificationSalt=cryptoUtil.genSalt();
        byte[] encryptionSalt =cryptoUtil.genSalt();
        byte[] verificationSecretKey = cryptoUtil.genSecretKey(masterPassword,verificationSalt);
        byte[] masterKey=cryptoUtil.genSecretKey(masterPassword,encryptionSalt);
        byte[] dataKeyIV=cryptoUtil.genIV();
        byte[] dataKey=cryptoUtil.genDataKey();
        byte[] encryptedDataKey=cryptoUtil.encryptData(masterKey,dataKey,dataKeyIV);
        VaultMetaData vaultMetaData =new VaultMetaData(verificationSecretKey,verificationSalt,encryptedDataKey,encryptionSalt,dataKeyIV);
        return vaultMetaData;
    }
}
