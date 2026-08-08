package com.passwordmanager.cryptography;

import com.passwordmanager.vaultmetadata.VaultService;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import static org.bouncycastle.crypto.params.Argon2Parameters.ARGON2_VERSION_13;

class CryptoUtil {
    private SecureRandom secureRandom=new SecureRandom();
    private VaultService vaultService;

    public CryptoUtil(){}

    protected byte[] genSalt() {
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public byte[] genSecretKey(byte[] masterPassword, byte[] salt) {
        Argon2Parameters.Builder builder=getArgon2Builder(salt);
        Argon2BytesGenerator generate=new Argon2BytesGenerator();
        generate.init(builder.build());
        byte[] secretKey =new byte[32];
        generate.generateBytes(masterPassword, secretKey,0, secretKey.length);
        return secretKey;
    }

    protected byte[] genDataKey(){
        byte[] dataKey=new byte[32];
        secureRandom.nextBytes(dataKey);
        return dataKey;
    }

    private SecretKey toSecretKey(byte[] key){
        SecretKey secretKey=new SecretKeySpec(key, 0, key.length, "AES");
        return secretKey;
    }

    protected byte[] genIV(){
        byte[] iv=new byte[12];
        secureRandom.nextBytes(iv);
        return iv;
    }

    private Argon2Parameters.Builder getArgon2Builder(byte[] salt){
        Argon2Parameters.Builder builder= new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(ARGON2_VERSION_13)
                .withIterations(3)
                .withMemoryAsKB(66536)
                .withParallelism(2)
                .withSalt(salt);
        return builder;
    }

    protected byte[] encryptData(byte[] key, byte[] data, byte[] iv) throws GeneralSecurityException{
        byte[] cipherText=null;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey=toSecretKey(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        cipherText = cipher.doFinal(data);
        return cipherText;
    }

    protected byte[] decryptData(byte[] key,byte[] encryptedData, byte[] iv) throws GeneralSecurityException{
        byte[] data =null;
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey secretKey=toSecretKey(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        data = cipher.doFinal(encryptedData);
        return data;
    }

}
